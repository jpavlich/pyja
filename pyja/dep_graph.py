#!/usr/bin/python3
from pyja.javaparser import JavaParser
from pyja.maven import Project, PathURI
from pathlib import Path
from pyja.jvm import JvmMgr
import networkx as nx
import os
import sys

def to_string(class_info) -> str:
   return f"{os.path.splitext(str(class_info.name))[1][1:]}{' e' if class_info.entity else ''}{' c' if class_info.controller else ''}{' r' if class_info.repository else ''}"



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
    
    nodes = p.source_classes()
    print(nodes)
    edges = p.dependencies()
    print(edges)

    G = nx.DiGraph()

    for n in nodes:
        G.add_node(n.name, label=to_string(n))

    for s,d,t in edges:
        label = str(d)
        if  G.has_edge(s,t):
            G.get_edge_data(s,t)["depTypes"].append(label)
        else:
            G.add_edge(s, t, depTypes=[label])
    

    for s, t in G.edges():
        depTypes = G.get_edge_data(s, t)["depTypes"]
        G.get_edge_data(s, t)["label"] = ",".join(set(depTypes))

    S = G.subgraph([n.name for n in nodes])

    


    A = nx.nx_agraph.to_agraph(S)
    A.layout(prog="dot")  
    A.draw(f"tmp/{project.id()}.png")


    jvm.stop()
