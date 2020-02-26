import os
from setuptools.command.develop import develop
from setuptools.command.install import install
import subprocess


JAVA_PROJ_PATH = "java/javaparser-util/"


def install_java_module():
    # print("Installing maven module")
    subprocess.run(["mvn", "clean", "install"], cwd=JAVA_PROJ_PATH)


class Develop(develop):
    def run(self):
        install_java_module()
        develop.run(self)


class Install(install):
    def run(self):
        install.run(self)
        install_java_module()

