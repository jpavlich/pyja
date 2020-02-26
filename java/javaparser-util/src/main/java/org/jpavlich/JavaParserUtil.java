package org.jpavlich;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;

public class JavaParserUtil {


    public List<List<String>> dependencies(String[] source_dirs, String[] classpath) {
        List<CompilationUnit> cus;
        try {
            cus = parse(source_dirs, classpath);
            return findDeps(cus);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Parses all java files in source_dirs
     * 
     * @param source_dirs Folders where to look for java files
     * @param classpath   List of jar files to support the type resolver
     * @return A list of CompilationUnit of the parsed java files
     * @throws IOException
     */
    public List<CompilationUnit> parse(String[] source_dirs, String[] classpath) throws IOException {
        CombinedTypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver(false));
        ParserConfiguration parserConfiguration = new ParserConfiguration()
                .setSymbolResolver(new JavaSymbolSolver(typeSolver));
        ProjectRoot root = new ProjectRoot(null, parserConfiguration);

        for (String source : source_dirs) {
            File f = new File(source);
            if (f.isDirectory()) {
                typeSolver.add(new JavaParserTypeSolver(source));
                root.addSourceRoot(FileSystems.getDefault().getPath(source));
            }
        }

        for (String cp : classpath) {
            File f = new File(cp);
            if (f.isFile() && f.getName().endsWith(".jar")) {
                typeSolver.add(new JarTypeSolver(cp));
                // System.out.println(cp);
            }
        }

        List<CompilationUnit> cus = new ArrayList<>();

        for (SourceRoot sr : root.getSourceRoots()) {
            for (ParseResult<CompilationUnit> pr : sr.tryToParse()) {
                if (pr.isSuccessful()) {
                    Optional<CompilationUnit> cu = pr.getResult();
                    cus.add(cu.get());
                }
            }
        }
        return cus;
    }

    public List<List<String>> detailedDependencies(List<CompilationUnit> cus) {
        System.out.println("Dependencies");
        List<List<String>> deps = new ArrayList<>();


        for (CompilationUnit cu : cus) {
            List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);

            for (ClassOrInterfaceDeclaration c : classes) {
                System.out.print(c.isInterface() ? "interface " : "class ");
                System.out.println(c.getFullyQualifiedName().get());
                List<FieldDeclaration> fields = c.findAll(FieldDeclaration.class);
                for (FieldDeclaration f : fields) {
                    for (VariableDeclarator v : f.getVariables()) {
                        System.out.println("\t" + v.getNameAsString() + ": " + v.getTypeAsString());
                    }
                }
                List<MethodDeclaration> methods = c.findAll(MethodDeclaration.class);
                for (MethodDeclaration m : methods) {
                    System.out.print("\t" + m.getNameAsString() + "(");
                    List<Parameter> params = m.findAll(Parameter.class);
                    for (Parameter p : params) {
                        System.out.print(p.getNameAsString() + ":" + p.getTypeAsString() + ", ");

                    }
                    System.out.println(")");
                }
            }
        }

        return deps;
    }

    public boolean isDep(Type t) {
        try {

            ResolvedType rt = t.resolve();
            if (rt.isReferenceType()) {
                return true;
            }
            
        } catch (UnsolvedSymbolException e) {
            
        }
        return false;
    }

    public List<List<String>> findDeps(List<CompilationUnit> cus) {
        List<List<String>> deps = new ArrayList<>();
        for (CompilationUnit cu : cus) {
            List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);

            for (ClassOrInterfaceDeclaration c : classes) {
                String cName = c.getFullyQualifiedName().get();
                List<Type> types = c.findAll(Type.class);
                for (Type t : types) {
                    if (t instanceof ClassOrInterfaceType) {
                        
                        if (isDep(t)) {
                            deps.add(Arrays.asList(cName, t.resolve().asReferenceType().getQualifiedName()));
                        }
                    }
                }
            }
        }

        return deps;
    }
}