import jpype
import jpype.imports
from jpype.types import *
from typing import List
from pathlib import Path

JDK_PATH = "/usr/lib/jvm/java-1.8.0-openjdk-amd64/"
JVM_PATH = f"{JDK_PATH}/jre/lib/amd64/server/libjvm.so"
TOOLS_JAR_PATH = f"{JDK_PATH}/lib/tools.jar"

class JvmMgr(object):
    def __init__(self):
        pass

    def add_classpath(self, *classpath: str):
        print("Configuring classpath...")
        for path in classpath:
            jpype.addClassPath(path)
        print("Classpath done")

    def start(self):
        jpype.startJVM(JVM_PATH, convertStrings=False)

    def stop(self):
        jpype.shutdownJVM()

