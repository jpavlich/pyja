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


class ProjDesc(ABC):
    def __init__(self, groupId, artifactId, version, scope):
        super().__init__()
        self.groupId = groupId
        self.artifactId = artifactId
        self.version = version
        self.scope = scope


def find_jars(folder) -> List[str]:
    return [
        j
        for j in glob.iglob(f"{folder}/**/*.jar", recursive=True)
        if not j.endswith("sources.jar") and not j.endswith("javadoc.jar")
    ]


def source_folder(proj_folder):
    target_folder = f"{proj_folder}/src/main/java"
    if Path(target_folder).is_dir():
        return target_folder
    else:
        return None


def maven_dependencies(project_file) -> Iterable[ProjDesc]:

    return (
        maven_dep_parse(dep_str)
        for dep_str in subprocess.check_output(
            [
                "mvn",
                "-q",
                "dependency:list",
                "-DoutputFile=/dev/stdout",
                "-f",
                project_file,
            ],
            # cwd=proj_folder,
        )
        .decode()
        .split("\n")[2:-2]
    )


def gradle_dependencies(project_file) -> Iterable[ProjDesc]:
    return (
        gradle_dep_parse(dep_str)
        for dep_str in subprocess.check_output(
            [f"{Path(__file__).resolve().parent}/gradle_deps.sh", project_file,],
            # cwd=self.proj_folder,
        )
        .decode()
        .split("\n")
        if len(dep_str) > 0
    )


def gradle_dep_parse(dep_str):
    dep = dep_str.strip().split(":")
    print(dep)
    return ProjDesc(groupId=dep[0], artifactId=dep[1], version=dep[2], scope="compile",)


def maven_dep_parse(dep_str):
    dep = dep_str.strip().split(":")
    return ProjDesc(groupId=dep[0], artifactId=dep[1], version=dep[3], scope=dep[4],)


def gradle_dep_folder(p: ProjDesc) -> str:
    return f"{GRADLE_PATH}/{p.groupId}/{p.artifactId}/{p.version}"


def maven_dep_folder(p: ProjDesc):
    return f"{M2_PATH}/{p.groupId.replace('.', '/')}/{p.artifactId}/{p.version}"


def is_gradle(proj_file: str) -> bool:
    file = Path(proj_file).name
    return file.endswith(".gradle")


def is_maven(proj_file: str) -> bool:
    file = Path(proj_file).name
    return file == "pom.xml" or file.endswith(".pom")


def all_jars(proj_file):
    if is_gradle(proj_file):
        jars = find_jars(Path(proj_file).parent)
        for dep in gradle_dependencies(proj_file):
            for jar in find_jars(gradle_dep_folder(dep)):
                jars.append(jar)

        return jars

    elif is_maven(proj_file):
        jars = find_jars(Path(proj_file).parent)
        for dep in maven_dependencies(proj_file):
            for jar in find_jars(maven_dep_folder(dep)):
                jars.append(jar)

        return jars
    else:
        raise Exception(f"Invalid project file {proj_file}")


if __name__ == "__main__":
    # print(all_jars(f"{Path.home()}/git/spring-petclinic/pom.xml"))
    print(all_jars(f"{Path.home()}/git/sagan/sagan-site/build.gradle"))

