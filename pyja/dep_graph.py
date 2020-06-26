#!/usr/bin/python3
from pyja.javaparser import JavaParser
from pyja.java_project import Project, ProjDesc
from pathlib import Path
from pyja.jvm import JvmMgr
import networkx as nx
import os
import sys
import pandas as pd


def to_string(class_name) -> str:
    # return os.path.splitext(str(class_name))[1][1:]
    return class_name


def process_java_project(project_file, output_file, java_packages):
    # Configure java jvm to parse java files
    jvm = JvmMgr()
    p = JavaParser()
    p.configure_jvm(jvm)

    # jvm must start before doing the parsing
    jvm.start()

    project = Project.create(project_file)
    packages = [pkg for pkg in java_packages]
    p.analyze(project)
    p.include_only(*packages)

    dep = p.dependencies()

    nodes_df = pd.DataFrame(
        [[str(c.name), str(c.stereotype).lower()] for c in dep.classes],
        columns=["id", "stereotype"],
    )

    edges_df = pd.DataFrame(
        [[str(s), str(t), str(d)] for s, d, t in dep.deps],
        columns=["source", "target", "depType"],
    )

    with pd.ExcelWriter(output_file + ".xlsx") as writer:
        nodes_df.to_excel(writer, "nodes")
        edges_df.to_excel(writer, "edges")
        writer.save()

    jvm.stop()

    return nodes_df, edges_df


def to_dep_graph(nodes_df: pd.DataFrame, edges_df: pd.DataFrame, output_file: str):
    G = nx.DiGraph()

    shape = {
        "c": "ellipse",     # Controller
        "r": "cylinder",    # Repository
        "e": "polygon",     # Entity
        "s": "box",         # Service
    }

    for _, row in nodes_df.iterrows():
        G.add_node(
            row["id"], label=to_string(row["id"]), shape=shape[row["stereotype"]]
        )

    for _, row in edges_df.iterrows():
        s = row["source"]
        t = row["target"]
        depType = row["depType"]

        if G.has_edge(s, t):
            G.get_edge_data(s, t)["depTypes"].append(depType)
        else:
            G.add_edge(s, t, depTypes=[depType])

    for s, t in G.edges():
        depTypes = G.get_edge_data(s, t)["depTypes"]
        G.get_edge_data(s, t)["label"] = ",".join(sorted(set(depTypes)))

    print(f"Found {len(G)} classes, {G.number_of_edges()} edges")

    A = nx.nx_agraph.to_agraph(G)
    A.layout(prog="dot")
    A.draw(output_file)


if __name__ == "__main__":
    if len(sys.argv) < 3:
        print(
            f"{sys.argv[0]} <project_file (pom.xml or build.gradle)> <output file (pdf)> [java packages to analyze in the source code]"
        )
        exit(1)

    nodes_df, edges_df = process_java_project(
        project_file=sys.argv[1], output_file=sys.argv[2], java_packages=sys.argv[3:]
    )

    to_dep_graph(nodes_df, edges_df, output_file=sys.argv[2])

