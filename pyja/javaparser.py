import jpype
import jpype.imports
from jpype.types import *
from pyja.maven import MavenProject, DepURI, PathURI
from typing import *
from pathlib import Path
from pyja.jvm import JvmMgr


groupId = "org.jpavlich"
artifactId = "javaparser-util"
version = "0.1-SNAPSHOT"
DEP_STR = f"{groupId}:{artifactId}:jar:{version}:compile"


class JavaParser(object):
    def __init__(self):
        super().__init__()

    def configure_jvm(self, jvm: JvmMgr):
        self.jvm = jvm
        self.jvm.add_classpath(*MavenProject(DepURI(DEP_STR)).jars())

    def dependencies(self, *maven_projects: "MavenProject"):
        sources: Set[str] = set({})
        classpath: Set[str] = set({})
        for p in maven_projects:
            sources.add(p.source_path())
            classpath.update(p.jars())

        # print("---")
        # print(sources)
        # print("\",\n\"".join(classpath))

        from org.jpavlich import JavaParserUtil

        app = JavaParserUtil()
        return app.dependencies(list(sources), list(classpath))


if __name__ == "__main__":
    # Configure java jvm to parse java files (using javaparser library https://javaparser.org/)
    jvm = JvmMgr()
    p = JavaParser()
    p.configure_jvm(jvm)

    # jvm must start before doing the parsing
    jvm.start()

    deps = p.dependencies(MavenProject(PathURI(f"{Path.home()}/git/spring-petclinic/pom.xml")))
    for dep in deps:
        print(dep)

    jvm.stop()
