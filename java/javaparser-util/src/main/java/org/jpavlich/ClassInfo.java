package org.jpavlich;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;

import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Type;

import org.springframework.stereotype.Controller;

public class ClassInfo {

    private static String REPOSITORY_INTERFACE = "org.springframework.data.repository.Repository";

    public String name;
    public boolean entity = false;
    public boolean controller = false;
    public boolean repository = false;

    public ClassInfo(String name, boolean entity, boolean controller, boolean repository) {
        this.entity = entity;
        this.controller = controller;
        this.repository = repository;
        this.name = name;
    }

    public static ClassInfo from(ClassSymbol cs) {
        return new ClassInfo(cs.fullname.toString(), isEntity(cs), isController(cs), isRepository(cs));
    }

    private static boolean isEntity(ClassSymbol cs) {
        return cs.getAnnotation(Entity.class) != null;
    }

    private static boolean isController(ClassSymbol cs) {
        return cs.getAnnotation(Controller.class) != null;
    }

    private static boolean isRepository(ClassSymbol cs) {
        Set<String> interfaces = getAllInterfaces(cs.type);
        return interfaces.contains(REPOSITORY_INTERFACE);

    }

    private static Set<String> getAllInterfaces(Type t) {
        ClassSymbol cs = (ClassSymbol) t.asElement();
        Set<String> interfaces = new HashSet<String>();
        for (Type i : cs.getInterfaces()) {
            interfaces.add(((ClassSymbol) i.asElement()).fullname.toString());
            interfaces.addAll(getAllInterfaces(i));
        }
        return interfaces;

    }

    @Override
    public String toString() {
        return name + (entity ? " (e)" : "") + (controller ? " (c)" : "") + (repository ? " (r)" : "");
    }
}