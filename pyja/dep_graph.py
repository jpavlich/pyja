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
        print(f"{sys.argv[0]} <project_file (pom.xml or build.gradle)> <output file (pdf)> [java packages to analyze in the source code]")
        exit(1)

    # Configure java jvm to parse java files
    jvm = JvmMgr()
    p = JavaParser()
    p.configure_jvm(jvm)

    # jvm must start before doing the parsing
    jvm.start()

    project = Project.create(sys.argv[1])
    packages = [pkg for pkg in sys.argv[3:]]
    p.analyze(project)
    p.include_only(*packages)

    dep =  p.dependencies()
    classes = dep.classes
    edges = dep.deps

    G = nx.DiGraph()


    for c in classes:
        shape = (
            "trapezium"
            if c.stereotype == c.Stereotype.ENTITY
            else "ellipse"
            if c.stereotype == c.Stereotype.CONTROLLER
            else "cylinder"
            if c.stereotype == c.Stereotype.REPOSITORY
            else "polygon"
            if c.stereotype == c.Stereotype.SERVICE
            else "box"
        )
        G.add_node(c.name, label=to_string(c), shape=shape)

    for s, d, t in edges:
        label = str(d)

        if G.has_edge(s, t):
            G.get_edge_data(s, t)["depTypes"].append(label)
        else:
            G.add_edge(s, t, depTypes=[label])

    for s, t in G.edges():
        depTypes = G.get_edge_data(s, t)["depTypes"]
        G.get_edge_data(s, t)["label"] = ",".join(sorted(set(depTypes)))

    print(f"Found {len(classes)} classes, {G.number_of_edges()} edges")

    S = G.subgraph([n.name for n in classes])

    A = nx.nx_agraph.to_agraph(S)
    A.layout(prog="dot")
    A.draw(sys.argv[2])

    jvm.stop()
