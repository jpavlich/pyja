import jpype
import jpype.imports
from jpype.types import *
from typing import *
from pathlib import Path
from pyja.jvm import JvmMgr
from pyja.java_project import Maven, ProjDesc, Project


groupId = "org.jpavlich"
artifactId = "javaparser-util"
version = "0.1-SNAPSHOT"


DEPENDENCY = "d"
SUPERCLASS = "s"
FIELD = "f"
METHOD_PARAM = "p"
METHOD_RETURN_TYPE = "r"


class JavaParser(object):
    def __init__(self):
        super().__init__()

    def configure_jvm(self, jvm: JvmMgr):
        self.jvm = jvm
        javaparser_dep = Maven.dep(ProjDesc(groupId, artifactId, version))
        self.jvm.add_classpath(*javaparser_dep.all_jars())

    def analyze(self, *projects):
        sources: Set[str] = set({})
        classpath: Set[str] = set({})
        for p in projects:
            sources.update(p.source_folders())
            print(sources)
            classpath.update(p.all_jars())

        from org.jpavlich import JavaParserUtil

        self.jpu = JavaParserUtil()
        self.jpu.analyze(list(sources), list(classpath))

    def dependencies(self):
        return self.jpu.getDependencies()



if __name__ == "__main__":
    # Configure java jvm to parse java files (using javaparser library https://javaparser.org/)
    jvm = JvmMgr()
    p = JavaParser()
    p.configure_jvm(jvm)

    # jvm must start before doing the parsing
    jvm.start()

    p.analyze(Project.create(f"{Path.home()}/git/spring-petclinic/pom.xml"))
    

    jvm.stop()
