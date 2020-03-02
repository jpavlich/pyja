import jpype
import jpype.imports
from jpype.types import *
from pyja.maven import Project, DepURI, PathURI
from typing import *
from pathlib import Path
from pyja.jvm import JvmMgr


groupId = "org.jpavlich"
artifactId = "javaparser-util"
version = "0.1-SNAPSHOT"
DEP_STR = f"{groupId}:{artifactId}:jar:{version}:compile"


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
        self.jvm.add_classpath(*Project(DepURI(DEP_STR)).jars())

    def init(self, *maven_projects: "Project"):
        sources: Set[str] = set({})
        classpath: Set[str] = set({})
        for p in maven_projects:
            sources.add(p.source_path())
            classpath.update(p.jars())

        from org.jpavlich import JavaParserUtil

        self.jpu = JavaParserUtil()
        self.jpu.init(list(sources), list(classpath))

    def classes(self):
        return self.jpu.getClasses()

    def dependencies(self):
        return self.jpu.getDependencies()

    def source_classes(self):
        return self.jpu.getSourceClasses()


if __name__ == "__main__":
    # Configure java jvm to parse java files (using javaparser library https://javaparser.org/)
    jvm = JvmMgr()
    p = JavaParser()
    p.configure_jvm(jvm)

    # jvm must start before doing the parsing
    jvm.start()

    p.init(Project(PathURI(f"{Path.home()}/git/spring-petclinic/pom.xml")))
    

    jvm.stop()
