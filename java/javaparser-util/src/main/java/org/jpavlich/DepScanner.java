package org.jpavlich;

import java.util.Arrays;

import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.code.Type;

/**
 * DepScanner
 */
public class DepScanner extends Scanner {

    public static String DEPENDENCY = "d";
    public static String SUPERTYPE = "s";
    public static String TYPE_PARAMETER = "t";
    public static String FIELD = "f";
    public static String METHOD_PARAM = "p";
    public static String RETURN_TYPE = "r";
    public static String VARIABLE = "v";

    private ClassSymbol currentClass;
    private ClassDepGraph classDepGraph = new ClassDepGraph();

    @Override
    protected void visit(ClassSymbol cs) {
        currentClass = cs;
        classDepGraph.classes.add(ClassInfo.from(cs));
        processType(cs.getSuperclass().asElement(), SUPERTYPE);
        for (Type i : cs.getInterfaces()) {
            processType(i.asElement(), SUPERTYPE);
        }
    }

    @Override
    protected void visit(MethodSymbol ms) {
        processType(ms.getReturnType().asElement(), RETURN_TYPE);
    }

    @Override
    protected void visitField(VarSymbol vs) {
        processVar(vs, FIELD);
    }

    @Override
    protected void visitParam(VarSymbol vs) {
        processVar(vs, METHOD_PARAM);
    }

    private void processVar(VarSymbol vs, String depType) {
        processType(vs.type.asElement(), depType);
    }

    private void processType(TypeSymbol t, String depType) {
        if (t instanceof ClassSymbol) {
            ClassSymbol cs = (ClassSymbol) t;

            if (!included(cs) || excluded(cs))
                return;

            String c1 = currentClass.fullname.toString();
            String c2 = cs.fullname.toString();
            if (!c1.equals(c2)) {
                classDepGraph.deps.add(Arrays.asList(c1, depType, c2));
            }
        }
    }

    /**
     * @return the classDepGraph
     */
    public ClassDepGraph getClassDepGraph() {
        return classDepGraph;
    }

};