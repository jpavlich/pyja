#!/usr/bin/python3
from pyja.javaparser import JavaParser
from pyja.maven import Project, PathURI
from pathlib import Path
from pyja.jvm import JvmMgr
import networkx as nx
import os
import sys

def simple_name(name:str) -> str:
   return os.path.splitext(str(name))[1][1:]



if __name__ == "__main__":
    if len(sys.argv) == 1:
        print(f"{sys.argv[0]} <pom.xml>")
        exit(1)

    # Configure java jvm to parse java files
    jvm = JvmMgr()
    p = JavaParser()
    p.configure_jvm(jvm)

    # jvm must start before doing the parsing
    jvm.start()

    project = Project(PathURI(sys.argv[1]))
    p.init(project)
    nodes = p.classes()
    sources = [simple_name(s) for s in p.source_classes()]
    edges = p.dependencies()

    G = nx.DiGraph()

    G.add_nodes_from(simple_name(n) for n in nodes)

    for s,d,t in edges:
        source = simple_name(s)
        target = simple_name(t)
        label = str(d)
        if  G.has_edge(source,target):
            G.get_edge_data(source,target)["depTypes"].append(label)
        else:
            G.add_edge(source, target, depTypes=[label])
    
    print(G.get_edge_data("PetController","Pet"))

    for source, target in G.edges():
        depTypes = G.get_edge_data(source, target)["depTypes"]
        G.get_edge_data(source, target)["label"] = ",".join(set(depTypes))

    S = G.subgraph(sources)

    


    A = nx.nx_agraph.to_agraph(S)
    A.layout(prog="dot")  
    A.draw(f"tmp/{project.id()}.png")


    jvm.stop()
