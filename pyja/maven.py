import glob
import itertools
import os
import re
import subprocess
from pathlib import Path
from typing import *

M2_PATH: str = f"{Path.home()}/.m2/repository"
# TODO Support Gradle

class AbstractURI(object):
    def __init__(self, m2path):
        super().__init__()
        self.m2path = m2path

    def jars(self) -> Iterator[str]:
        return (
            j
            for j in glob.iglob(f"{self.base_path()}/**/*.jar", recursive=True)
            if not j.endswith("sources.jar") and not j.endswith("javadoc.jar")
        )

    def pom_file(self) -> str:
        pass

    def pom_path(self) -> str:
        pass

    def base_path(self) -> str:
        pass

    def id(self) -> str:
        pass


class PathURI(AbstractURI):
    def __init__(self, pom_path, m2path=M2_PATH):
        super().__init__(m2path)
        self._pom_path = pom_path

    def pom_file(self) -> str:
        return os.path.basename(self._pom_path)

    def pom_path(self) -> str:
        return self._pom_path

    def base_path(self) -> str:
        return os.path.dirname(self._pom_path)

    def id(self) -> str:
        return os.path.basename(os.path.dirname(self._pom_path))

class DepURI(AbstractURI):
    def __init__(self, uri: str, m2path=M2_PATH):
        super().__init__(m2path)
        dep = uri.strip().split(":")
        self.groupId = dep[0]
        self.folder = dep[0].replace(".", "/")
        self.artifactId = dep[1]
        self.version = dep[3]
        self.scope = dep[4]

    def pom_file(self) -> str:
        return f"{self.artifactId}-{self.version}.pom"

    def pom_path(self) -> str:
        return f"{self.base_path()}/{self.pom_file()}"

    def base_path(self) -> str:
        return f"{self.m2path}/{self.folder}/{self.artifactId}/{self.version}"

    def id(self) -> str:
        return f"{self.groupId}-{self.artifactId}-{self.version}"

class Project(object):
    def __init__(self, uri: AbstractURI):
        super().__init__()
        self.uri: AbstractURI = uri
        self.pom_path = uri.pom_path()
        self.pom_file = uri.pom_file()

        self.proj_folder = os.path.dirname(self.pom_path)

    def source_path(self):
        target_folder = f"{self.proj_folder}/src/main/java"
        if Path(target_folder).is_dir():
            return target_folder
        else:
            return None

    def id(self):
        return self.uri.id()

    def jars(self) -> Iterable[str]:
        # Uses command-line maven to get the list of dependencies
        out = subprocess.check_output(
            [
                "mvn",
                "-q",
                "dependency:list",
                "-DoutputFile=/dev/stdout",
                "-f",
                self.pom_file,
            ],
            cwd=self.proj_folder,
            stderr=subprocess.STDOUT,
        ).decode()

        # Parses each line to get the attributes of each dependency
        # Then it retrieves all the jars for each dependency

        # Find jars within this project
        jars = self.uri.jars()

        # Find jars of this project's dependencies
        dep_jars = (DepURI(line).jars() for line in out.split("\n")[2:-2])

        # Combine all jars into one iterable
        return itertools.chain(jars, *dep_jars)


if __name__ == "__main__":

    groupId = "org.jpavlich"
    artifactId = "javaparser-util"
    version = "0.1-SNAPSHOT"
    DEP_STR = f"{groupId}:{artifactId}:jar:{version}:compile"

    p = Project(PathURI(f"{Path.home()}/git/spring-petclinic/pom.xml"))
    # p = MavenProject(DepURI(DEP_STR))
    # print(p.source_path())
    # print("\n".join(list(p.jars())))
