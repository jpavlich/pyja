package org.jpavlich;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;

import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Type;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

public class ClassInfo {

    public enum Stereotype {ENTITY("E"), CONTROLLER("C"), REPOSITORY("R"), SERVICE("S"), COMPONENT("C"), CLASS("c");
        
        private String code;

        private Stereotype(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return code;
        }
    };

    private static String REPOSITORY_INTERFACE = "org.springframework.data.repository.Repository";

    public String name;

    public Stereotype stereotype;

    public ClassInfo(String name, Stereotype stereotype) {
        this.name = name;
        this.stereotype = stereotype;
    }

    public static ClassInfo from(ClassSymbol cs) {
        return new ClassInfo(cs.fullname.toString(), findStereotype(cs));
    }


    private static Stereotype findStereotype(ClassSymbol cs) {
        if (isEntity(cs)) return Stereotype.ENTITY;
        if (isService(cs)) return Stereotype.SERVICE;
        if (isController(cs)) return Stereotype.CONTROLLER;
        if (isRepository(cs)) return Stereotype.REPOSITORY;
        if (isComponent(cs)) return Stereotype.COMPONENT;
        return Stereotype.CLASS;
    }

    private static boolean isEntity(ClassSymbol cs) {
        return cs.getAnnotation(Entity.class) != null;
    }
    
    private static boolean isService(ClassSymbol cs) {
        return cs.getAnnotation(Service.class) != null;
    }
    
    private static boolean isController(ClassSymbol cs) {
        return cs.getAnnotation(Controller.class) != null || cs.getAnnotation(RestController.class) != null;
    }
    
    private static boolean isRepository(ClassSymbol cs) {
        Set<String> interfaces = getAllInterfaces(cs.type);
        return interfaces.contains(REPOSITORY_INTERFACE);
        
    }
    
    private static boolean isComponent(ClassSymbol cs) {
        return cs.getAnnotation(Component.class) != null;
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
        return name + "(" + stereotype + ")";
    }
}