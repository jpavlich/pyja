from pyja.javaparser import JavaParser
from pyja.maven import MavenProject, PathURI
from pathlib import Path
from pyja.jvm import JvmMgr
import networkx as nx
import os

def simple_name(name:str) -> str:
   return os.path.splitext(str(name))[1][1:]



if __name__ == "__main__":
    # Configure java jvm to parse java files
    jvm = JvmMgr()
    p = JavaParser()
    p.configure_jvm(jvm)

    # jvm must start before doing the parsing
    jvm.start()

    p.init(MavenProject(PathURI(f"{Path.home()}/git/spring-petclinic/pom.xml")))
    nodes = p.classes()
    sources = [simple_name(s) for s in p.source_classes()]
    edges = p.dependencies()

    G = nx.DiGraph()

    G.add_nodes_from(simple_name(n) for n in nodes)

    for s,d,t in edges:
        G.add_edge(simple_name(s),simple_name(t), label=str(d))

    # G = nx.path_graph()

    S = G.subgraph(sources)


    A = nx.nx_agraph.to_agraph(S)
    A.layout(prog="dot")  # layout with default (neato)
    A.draw("tmp/petclinic.png")


    jvm.stop()
