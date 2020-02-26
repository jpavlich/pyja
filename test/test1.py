from pyja.javaparser import JavaParser
from pyja.maven import MavenProject, PathURI
from pathlib import Path
from pyja.jvm import JvmMgr

if __name__ == "__main__":
    # Configure java jvm to parse java files
    jvm = JvmMgr()
    p = JavaParser()
    p.configure_jvm(jvm)

    # jvm must start before doing the parsing
    jvm.start()

    deps = p.dependencies(MavenProject(PathURI(f"{Path.home()}/git/spring-petclinic/pom.xml")))
    print(deps)
    jvm.stop()
