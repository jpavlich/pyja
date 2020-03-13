import glob
import itertools
import os
import re
import subprocess
from pathlib import Path
from abc import ABC, abstractmethod
from typing import *


# gradle proj: build.gradle, no repo_path -> source folder, jars
# gradel dep: gradle dep uri, gradle repo_path -> jars
# maven proj: pom.xml, no repo path -> source folder, jars
# maven dep: maven dep uri, m2 repo path -> jars


M2_PATH: str = f"{Path.home()}/.m2/repository"
GRADLE_PATH: str = f"{Path.home()}/.gradle/caches/modules-2/files-2.1"


def run_script(script, *args) -> List[str]:
    return (
        subprocess.check_output(
            [f"{Path(__file__).resolve().parent}/scripts/{script}", *args],
        )
        .decode()
        .split("\n")
    )


class ProjDesc(object):
    def __init__(self, groupId, artifactId, version, scope="compile"):
        super().__init__()
        self.groupId = groupId
        self.artifactId = artifactId
        self.version = version
        self.scope = scope

    def __repr__(self):
        return f"{self.groupId}-{self.artifactId} ({self.version}) {self.scope}"


# TODO Support multimodule projects: find all child projects from the parent
class Project(ABC):
    def __init__(self, proj_file):
        super().__init__()
        self.proj_folder = Path(proj_file).parent
        self.proj_file = proj_file

    def source_folders(self) -> Iterable[str]:
        deps = []
        mm_deps = self.multimodule_dependencies()
        for d in mm_deps:
            folder = f"{self.proj_folder}/../{d}/src/main/java"
            if (Path(folder).is_dir()):
                deps.append(folder)

        target_folder = f"{self.proj_folder}/src/main/java"
        if Path(target_folder).is_dir():
            deps.append(target_folder)

        return deps

    @staticmethod
    def create(proj_file):
        if is_gradle(proj_file):
            return Gradle(proj_file)
        elif is_maven(proj_file):
            return Maven(proj_file)
        else:
            raise Exception(f"Invalid project file {proj_file}")

    @abstractmethod
    def multimodule_dependencies(self) -> Iterable[str]:
        pass


class Gradle(Project):
    def __init__(self, proj_file):
        super().__init__(proj_file)

    def dependencies(self) -> Iterable[ProjDesc]:
        return (
            Gradle.dep_parse(dep_str)
            for dep_str in run_script("gradle_deps.sh", self.proj_file)
            if len(dep_str) > 0
        )

    def multimodule_dependencies(self) -> Iterable[str]:
        return (
            dep_str
            for dep_str in run_script("gradle_multimodule_deps.sh", self.proj_file)
            if len(dep_str) > 0
        ) 

    @staticmethod
    def dep_parse(dep_str):
        dep = dep_str.strip().split(":")
        # print(dep)
        p = ProjDesc(groupId=dep[0], artifactId=dep[1], version=dep[2])
        # print(Path(Gradle.dep_folder(p)).exists())
        return p

    @staticmethod
    def dep_folder(p: ProjDesc) -> str:
        return f"{GRADLE_PATH}/{p.groupId}/{p.artifactId}/{p.version}"

    @staticmethod
    def dep_file(p: ProjDesc) -> str:
        raise NotImplementedError

    @staticmethod
    def dep(p: ProjDesc):
        raise NotImplementedError

    def all_jars(self):
        jars = find_jars(self.proj_folder)
        for dep in self.dependencies():
            for jar in find_jars(Gradle.dep_folder(dep)):
                jars.append(jar)

        return jars


class Maven(Project):
    def __init__(self, proj_file):
        super().__init__(proj_file)

    def dependencies(self) -> Iterable[ProjDesc]:
        return (
            Maven.dep_parse(dep_str)
            for dep_str in run_script("maven_deps.sh", self.proj_file,)[2:-2]
        )

    @staticmethod
    def dep_parse(dep_str):
        dep = dep_str.strip().split(":")
        return ProjDesc(
            groupId=dep[0], artifactId=dep[1], version=dep[3], scope=dep[4],
        )

    @staticmethod
    def dep_folder(p: ProjDesc):
        return f"{M2_PATH}/{p.groupId.replace('.', '/')}/{p.artifactId}/{p.version}"

    @staticmethod
    def dep_file(p: ProjDesc) -> str:
        return f"{Maven.dep_folder(p)}/{p.artifactId}-{p.version}.pom"

    @staticmethod
    def dep(p: ProjDesc):
        return Maven(Maven.dep_file(p))

    def all_jars(self):
        jars = find_jars(self.proj_folder)
        for dep in self.dependencies():
            for jar in find_jars(Maven.dep_folder(dep)):
                jars.append(jar)

        return jars

    def multimodule_dependencies(self) -> Iterable[str]:
        return []


def find_jars(folder) -> List[str]:
    return [
        j
        for j in glob.iglob(f"{folder}/**/*.jar", recursive=True)
        if not j.endswith("sources.jar") and not j.endswith("javadoc.jar")
    ]


def is_gradle(proj_file: str) -> bool:
    file = Path(proj_file).name
    return file.endswith(".gradle")


def is_maven(proj_file: str) -> bool:
    file = Path(proj_file).name
    return file == "pom.xml" or file.endswith(".pom")


if __name__ == "__main__":
    print(Project.create(f"{Path.home()}/git/spring-petclinic/pom.xml").all_jars())
    # print(Project.create(f"{Path.home()}/git/sagan/sagan-site/build.gradle").all_jars())

