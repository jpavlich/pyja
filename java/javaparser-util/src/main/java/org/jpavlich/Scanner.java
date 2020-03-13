package org.jpavlich;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;

import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.code.Type;

/**
 * DepScanner
 */
public abstract class Scanner {
    Set<String> visited = new HashSet<>();
    private List<String> includedPackages = new ArrayList<>();
    private List<String> excludedPackages = new ArrayList<>();

    public Scanner() {
    }

    public void includeOnly(String... includePackages) {
        this.includedPackages.addAll(Arrays.asList(includePackages));
    }

    public void exclude(String... excludePackages) {
        this.excludedPackages.addAll(Arrays.asList(excludePackages));
    }

    public void visitAll(Iterable<? extends Element> elements) {
        for (Element c : elements) {
            if (c instanceof ClassSymbol) {
                ClassSymbol cs = (ClassSymbol) c;
                _visit(cs);
            }
        }
    }

    private void _visit(ClassSymbol cs) {
        if (!mustVisit(cs))
            return;

        String name = cs.fullname.toString();
        visited.add(name);

        for (Type i : cs.getInterfaces()) {
            TypeSymbol e = i.asElement();
            if (e instanceof ClassSymbol) {
                _visit((ClassSymbol) e);
            }
        }

        visit(cs);
        if (cs.members() != null) {
            for (Element element : cs.members().getElements()) {
                if (element instanceof MethodSymbol) {
                    MethodSymbol ms = (MethodSymbol) element;
                    visit(ms);
                    if (ms.params != null) {

                        for (VarSymbol param : ms.params) {
                            // System.out.print("\tP: ");
                            visitParam(param);
                            _visit(param.type);
                        }
                    }

                    _visit(ms.getReturnType());
                }
                if (element instanceof VarSymbol) {
                    VarSymbol vs = (VarSymbol) element;
                    visitField(vs);
                }
            }
        }
    }

    private void _visit(Type t) {
        TypeSymbol ts = t.asElement();
        if (ts instanceof ClassSymbol) {
            _visit((ClassSymbol) ts);
        } else {
            // System.out.println("\t\tT: " + t);
        }
        for (Type p : t.getParameterTypes()) {
            _visit(p);
        }
    }

    protected boolean mustVisit(ClassSymbol cs) {
        return !visited(cs) && included(cs) && !excluded(cs);
    }

    protected boolean visited(ClassSymbol cs) {
        String name = cs.fullname.toString();
        return visited.contains(name);
    }

    protected boolean included(ClassSymbol cs) {
        String name = cs.fullname.toString();
        if (includedPackages.isEmpty()) {
            return true;
        }
        for (String pkg : includedPackages) {
            if (name.startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }

    protected boolean excluded(ClassSymbol cs) {
        
        if (cs.asType().isPrimitiveOrVoid())
            return true;
        String name = cs.fullname.toString();
        if (excludedPackages.isEmpty()) {
            return false;
        }
        for (String pkg : excludedPackages) {
            if (name.startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }

    protected abstract void visit(MethodSymbol ms);

    protected abstract void visitField(VarSymbol vs);

    protected abstract void visitParam(VarSymbol vs);

    protected abstract void visit(ClassSymbol cs);

}