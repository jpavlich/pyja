package org.jpavlich;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class JavaParserUtil {

    // public class ClassInfo {
    // public String name;
    // public boolean entity = false;
    // public boolean controller = false;
    // public boolean repository = false;

    // public ClassInfo(Optional<String> name, boolean entity, boolean controller,
    // boolean repository) {
    // this.entity = entity;
    // this.controller = controller;
    // this.repository = repository;
    // this.name = name.orElse("__anonymous__");
    // }

    // @Override
    // public String toString() {
    // return name + (entity ? " (e)" : "") + (controller ? " (c)" : "") +
    // (repository ? " (r)" : "");
    // }
    // }

    private String REPOSITORY_INTERFACE = "org.springframework.data.repository.Repository";

    public static String DEPENDENCY = "d";
    public static String SUPERCLASS = "s";
    public static String TYPE_PARAMETER = "t";
    public static String FIELD = "f";
    public static String METHOD_PARAM = "p";
    public static String RETURN_TYPE = "r";
    public static String VARIABLE = "v";

    public List<List<String>> getDependencies(String... packages) {
        List<List<String>> deps = new ArrayList<>();
        JavaVisitor v = new JavaVisitor() {

            @Override
            void visit(Class<?> c, Class<?> container) {
                System.out.println("C: "+ c);

            }

            @Override
            void visit(Method m, Class<?> c) {
                // System.out.println("\t" + m);

            }

            @Override
            void visit(Field f, Class<?> c) {
                // System.out.println("\t" + f);

            }

            @Override
            void visit(Parameter p, Class<?> c) {
                // System.out.println("\t" + p);

            }
            
        };
        v.visitAll(packages);
        return deps;
    }

    public static void main(String[] args) {
        // System.out.println(System.getProperty("java.class.path"));

        JavaParserUtil pu = new JavaParserUtil();
        System.out.println(pu.getDependencies("sagan"));
    }

}