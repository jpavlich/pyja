#!/usr/bin/python3
from pyja.javaparser import JavaParser
from pyja.java_project import Project, ProjDesc
from pathlib import Path
from pyja.jvm import JvmMgr
import networkx as nx
import os
import sys

def to_string(class_info) -> str:
   return os.path.splitext(str(class_info.name))[1][1:]



if __name__ == "__main__":
    if len(sys.argv) < 3:
        print(f"{sys.argv[0]} <pom.xml> <out_file>")
        exit(1)

    # Configure java jvm to parse java files
    jvm = JvmMgr()
    p = JavaParser()
    p.configure_jvm(jvm)

    # jvm must start before doing the parsing
    jvm.start()

    project = Project.create(sys.argv[1])
    p.init(project)
    
    classes = p.source_classes()
    edges = p.dependencies()

    G = nx.DiGraph()

    for c in classes:
        shape = "trapezium" if c.entity else "ellipse" if c.controller else "cylinder" if c.repository else "box"
        G.add_node(c.name, label=to_string(c), shape=shape)

    for s,d,t in edges:
        label = str(d)
        
        if  G.has_edge(s,t):
            G.get_edge_data(s,t)["depTypes"].append(label)
        else:
            G.add_edge(s, t, depTypes=[label])
    

    for s, t in G.edges():
        depTypes = G.get_edge_data(s, t)["depTypes"]
        G.get_edge_data(s, t)["label"] = ",".join(sorted(set(depTypes)))

    S = G.subgraph([n.name for n in classes])

    


    A = nx.nx_agraph.to_agraph(S)
    A.layout(prog="dot")  
    A.draw(sys.argv[2])


    jvm.stop()
