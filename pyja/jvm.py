import jpype
import jpype.imports
from jpype.types import *
from typing import List
from pathlib import Path


class JvmMgr(object):
    def __init__(self):
        pass

    def add_classpath(self, *classpath: str):
        print("Configuring classpath...")
        for path in classpath:
            jpype.addClassPath(path)
        print("Classpath done")

    def start(self):
        jpype.startJVM(convertStrings=False)

    def stop(self):
        jpype.shutdownJVM()

