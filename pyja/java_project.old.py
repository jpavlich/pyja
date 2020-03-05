import glob
import itertools
import os
import re
import subprocess
from pathlib import Path
from typing import *
from abc import ABC, abstractmethod


M2_PATH: str = f"{Path.home()}/.m2/repository"
GRADLE_PATH: str = f"{Path.home()}/.gradle/caches/modules-2/files-2.1/"
# TODO Support Gradle


class AbstractURI(ABC):
    def __init__(self):
        super().__init__()

    def jars(self) -> Iterator[str]:
        print(self.base_path(), Path(self.base_path()).exists())
        return (
            j
            for j in glob.iglob(f"{self.base_path()}/**/*.jar", recursive=True)
            if not j.endswith("sources.jar") and not j.endswith("javadoc.jar")
        )

    @abstractmethod
    def project_file(self) -> str:
        pass

    @abstractmethod
    def project_file_path(self) -> str:
        pass

    @abstractmethod
    def base_path(self) -> str:
        pass

    @abstractmethod
    def id(self) -> str:
        pass


class PathURI(AbstractURI):
    def __init__(self, project_file_path):
        super().__init__()
        self._project_file_path = project_file_path

    def project_file(self) -> str:
        return os.path.basename(self._project_file_path)

    def project_file_path(self) -> str:
        return self._project_file_path

    def base_path(self) -> str:
        return os.path.dirname(self._project_file_path)

    def id(self) -> str:
        return os.path.basename(os.path.dirname(self._project_file_path))


class DepURI(AbstractURI, ABC):
    def __init__(self, uri: str):
        super().__init__()

        dep = uri.strip().split(":")
        if self._is_maven_dep(dep):
            self.groupId = dep[0]
            self.artifactId = dep[1]
            self.version = dep[3]
            self.scope = dep[4]
        elif self._is_gradle_dep(dep):
            self.groupId = dep[0]
            self.artifactId = dep[1]
            self.version = dep[2]
            self.scope = "compile"
        else:
            raise Exception(f"Wrong dependency format {dep}")

        self.folder = self._get_folder(self.groupId)
        self._project_file = f"{self.artifactId}-{self.version}.pom"
        self._base_path = f"{self.repo_path()}/{self.folder}/{self.artifactId}/{self.version}"
        self._project_file_path = f"{self._base_path}/{self._project_file}"
        self._id = f"{self.groupId}-{self.artifactId}-{self.version}"

    def _is_maven_dep(self, dep):
        return len(dep) == 5

    def _is_gradle_dep(self, dep):
        return len(dep) == 3


    def project_file(self) -> str:
        return self._project_file

    def project_file_path(self) -> str:
        return self._project_file_path

    def base_path(self) -> str:
        return self._base_path

    def id(self) -> str:
        return self._id

    
    @abstractmethod
    def _get_folder(self, group_id: str) -> str:
        pass

    @abstractmethod
    def repo_path(self):
        pass

class MavenURI(DepURI):

    def _get_folder(self, groupId: str) -> str:
        return self.groupId.replace(".", "/") 

    def repo_path(self):
        return M2_PATH

class GradleURI(DepURI):

    def _get_folder(self, groupId: str) -> str:
        return self.groupId

    def repo_path(self):
        return GRADLE_PATH

class Project(ABC):
    def __init__(self, uri: AbstractURI):
        super().__init__()
        self.uri: AbstractURI = uri
        self.project_file_path = uri.project_file_path()
        self.project_file = uri.project_file()
        self.proj_folder = os.path.dirname(self.project_file_path)

    def source_path(self):
        target_folder = f"{self.proj_folder}/src/main/java"
        if Path(target_folder).is_dir():
            return target_folder
        else:
            return None

    def id(self):
        return self.uri.id()

    def all_jars(self) -> Iterable[str]:
        # Uses command-line maven to get the list of dependencies
        deps = self.list_dependencies()

        # Parses each line to get the attributes of each dependency
        # Then it retrieves all the jars for each dependency

        # Find jars within this project
        jars = self.uri.jars()

        # Find jars of this project's dependencies
        dep_jars = (
            DepURI(line, self.repo_path()).jars() for line in deps if len(line) > 0
        )

        # Combine all jars into one iterable
        return itertools.chain(jars, *dep_jars)

    @abstractmethod
    def list_dependencies(self) -> Iterable[str]:
        pass

    @abstractmethod
    def repo_path(self) -> str:
        pass


class GradleProject(Project):
    def __init__(self, uri: AbstractURI):
        super().__init__(uri)

    def list_dependencies(self) -> Iterable[str]:
        return (
            subprocess.check_output(
                [
                    f"{Path(__file__).resolve().parent}/gradle_deps.sh",
                    self.project_file_path,
                ],
                cwd=self.proj_folder,
            )
            .decode()
            .split("\n")
        )

    def repo_path(self) -> str:
        return GRADLE_PATH


class MavenProject(Project):
    def __init__(self, uri: AbstractURI):
        super().__init__(uri)

    def list_dependencies(self) -> Iterable[str]:

        return (
            subprocess.check_output(
                [
                    "mvn",
                    "-q",
                    "dependency:list",
                    "-DoutputFile=/dev/stdout",
                    "-f",
                    self.project_file,
                ],
                cwd=self.proj_folder,
                stderr=subprocess.STDOUT,
            )
            .decode()
            .split("\n")[2:-2]
        )

    def repo_path(self) -> str:
        return M2_PATH


if __name__ == "__main__":

    # groupId = "org.jpavlich"
    # artifactId = "javaparser-util"
    # version = "0.1-SNAPSHOT"
    # DEP_STR = f"{groupId}:{artifactId}:jar:{version}:compile"

    # p = MavenProject(PathURI(f"{Path.home()}/git/spring-petclinic/pom.xml"))
    # p = MavenProject(DepURI(DEP_STR))
    p = GradleProject(PathURI(f"{Path.home()}/git/sagan/sagan-site/build.gradle"))
    print(p.source_path())
    print("\n".join(list(p.all_jars())))
