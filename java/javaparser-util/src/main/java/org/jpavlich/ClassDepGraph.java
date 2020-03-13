package org.jpavlich;


import java.util.ArrayList;
import java.util.List;

public class ClassDepGraph {
    public List<ClassInfo> classes = new ArrayList<>();
    public List<List<String>> deps = new ArrayList<>();


    @Override
    public String toString() {
        return String.valueOf(classes) + "\n" + deps;
    }
}