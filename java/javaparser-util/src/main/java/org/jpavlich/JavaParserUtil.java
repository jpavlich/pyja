package org.jpavlich;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithVariables;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedParameterDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeParameterDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.Pair;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;

public class JavaParserUtil {

    public class ClassInfo {
        public String name;
        public boolean entity = false;
        public boolean controller = false;
        public boolean repository = false;

        public ClassInfo(Optional<String> name, boolean entity, boolean controller, boolean repository) {
            this.entity = entity;
            this.controller = controller;
            this.repository = repository;
            this.name = name.orElse("__anonymous__");
        }

        @Override
        public String toString() {
            return name + (entity ? " (e)" : "") + (controller ? " (c)" : "") + (repository ? " (r)" : "");
        }
    }

    private final List<Path> sourcePaths = new ArrayList<>();
    private String[] classpath;
    private List<CompilationUnit> compilationUnits;
    private CombinedTypeSolver typeSolver;
    private String REPOSITORY_INTERFACE;

    public static String DEPENDENCY = "d";
    public static String SUPERCLASS = "s";
    public static String TYPE_PARAMETER = "t";
    public static String FIELD = "f";
    public static String METHOD_PARAM = "p";
    public static String RETURN_TYPE = "r";
    public static String VARIABLE = "v";

    /**
     * @param sourcePaths Folders where to look for java files
     * @param classpath   List of jar files to support the type resolver
     */
    public void init(final String[] source_dirs, final String[] classpath) {

        for (final String sd : source_dirs) {
            sourcePaths.add(FileSystems.getDefault().getPath(sd));
        }
        this.classpath = classpath;
        try {
            compilationUnits = parse();
            REPOSITORY_INTERFACE = "org.springframework.data.repository.Repository";
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses all java files in source_dirs.
     * 
     * @return A list of CompilationUnit of the parsed java files
     * @throws IOException
     */
    private List<CompilationUnit> parse() throws IOException {
        typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver(false));
        final ParserConfiguration parserConfiguration = new ParserConfiguration()
                .setSymbolResolver(new JavaSymbolSolver(typeSolver));
        final ProjectRoot root = new ProjectRoot(null, parserConfiguration);

        for (final Path source : sourcePaths) {
            final File f = source.toFile();
            if (f.isDirectory()) {
                typeSolver.add(new JavaParserTypeSolver(source));
                root.addSourceRoot(source);
            }
        }

        for (final String cp : classpath) {
            final File f = new File(cp);
            if (f.isFile() && f.getName().endsWith(".jar")) {
                typeSolver.add(new JarTypeSolver(cp));
            }
        }

        final List<CompilationUnit> cus = new ArrayList<>();
        for (final SourceRoot sr : root.getSourceRoots()) {
            for (final ParseResult<CompilationUnit> pr : sr.tryToParse()) {
                if (pr.isSuccessful()) {
                    final Optional<CompilationUnit> cu = pr.getResult();
                    cus.add(cu.get());
                }
            }
        }
        return cus;
    }

    private boolean isEntity(ClassOrInterfaceDeclaration c) {
        return c.getAnnotationByName("Entity").isPresent();
    }

    protected boolean isController(ClassOrInterfaceDeclaration c) {
        return c.getAnnotationByName("Controller").isPresent();
    }

    private boolean isRepository(ClassOrInterfaceDeclaration c) {
        ResolvedReferenceTypeDeclaration ct = c.resolve();
        if (ct.isClass()) {
            Set<String> interfaces = ct.asClass().getAllInterfaces().stream().map(t -> t.getQualifiedName())
                    .collect(Collectors.toSet());
            return interfaces.contains(REPOSITORY_INTERFACE);

        } else if (ct.isInterface()) {
            Set<String> interfaces = c.resolve().asInterface().getAllInterfacesExtended().stream()
                    .map(t -> t.getQualifiedName()).collect(Collectors.toSet());
            return interfaces.contains(REPOSITORY_INTERFACE);
        }
        return false;

    }

    public List<ClassInfo> getSourceClasses() {
        return compilationUnits.stream().map(cu -> cu.findAll(ClassOrInterfaceDeclaration.class))
                .flatMap(Collection::stream)
                .map(c -> new ClassInfo(c.getFullyQualifiedName(), isEntity(c), isController(c), isRepository(c)))
                .collect(Collectors.toList());
    }

    public List<List<String>> getDependencies() {
        return getDependencies(false, false);
    }

    public List<List<String>> getDependencies(boolean includeGetters, boolean includeSetters) {
        final List<List<String>> deps = new ArrayList<>();
        for (final CompilationUnit cu : compilationUnits) {
            cu.accept(new VoidVisitorAdapter<ClassOrInterfaceDeclaration>() {
                @Override
                public void visit(ClassOrInterfaceDeclaration n, ClassOrInterfaceDeclaration c) {
                    if (c == null) {
                        c = n;
                    }
                    final String cName = c.getFullyQualifiedName().get();
                    final ResolvedReferenceTypeDeclaration ct = c.resolve();
                    for (final ResolvedReferenceType sup : ct.getAllAncestors()) {
                        deps.add(Arrays.asList(cName, SUPERCLASS, sup.getQualifiedName()));
                        addTypeParameterDeps(c, TYPE_PARAMETER, sup);
                    }
                    super.visit(n, c);
                }

                @Override
                public void visit(Parameter n, ClassOrInterfaceDeclaration c) {
                    try {
                        final String cName = c.getFullyQualifiedName().get();
                        ResolvedParameterDeclaration param = n.resolve();
                        CallableDeclaration<?> m = n.findAncestor(CallableDeclaration.class).get();

                        if (includeGetters && isGetter(m) || includeSetters && isSetter(m)
                                || !isGetter(m) && !isSetter(m)) {
                            ResolvedType t = param.getType();
                            if (t.isReferenceType()) {
                                deps.add(Arrays.asList(cName, METHOD_PARAM, t.asReferenceType().getQualifiedName()));
                                addTypeParameterDeps(c, METHOD_PARAM, t.asReferenceType());
                            }
                        }
                    } catch (Exception e) {
                        Node parent = n.getParentNode().get();
                        if (parent instanceof LambdaExpr) {
                            System.err.println("Lambda expression cannot be resolved: \n" + parent);
                        } else {
                            e.printStackTrace();
                        }
                    }
                    super.visit(n, c);
                }

                @Override
                public void visit(FieldDeclaration n, ClassOrInterfaceDeclaration c) {
                    processVar(n, FIELD, c);
                    super.visit(n, c);
                }

                @Override
                public void visit(MethodDeclaration n, ClassOrInterfaceDeclaration c) {
                    final String cName = c.getFullyQualifiedName().get();
                    if (includeGetters && isGetter(n) || includeSetters && isSetter(n)
                            || !isGetter(n) && !isSetter(n)) {
                        ResolvedType t = n.resolve().getReturnType();
                        if (t.isReferenceType()) {
                            deps.add(Arrays.asList(cName, RETURN_TYPE, t.asReferenceType().getQualifiedName()));
                            addTypeParameterDeps(c, RETURN_TYPE, t.asReferenceType());
                        }
                    }
                    super.visit(n, c);
                }

                @Override
                public void visit(VariableDeclarationExpr n, ClassOrInterfaceDeclaration c) {
                    processVar(n, VARIABLE, c);
                    super.visit(n, c);
                }

                public void addTypeParameterDeps(ClassOrInterfaceDeclaration c, String depType, ResolvedReferenceType t) {
                    final String cName = c.getFullyQualifiedName().get();
                    for (Pair<ResolvedTypeParameterDeclaration, ResolvedType> tPair : t
                            .getTypeParametersMap()) {
                        if (tPair.b.isReferenceType()) {
                            deps.add(Arrays.asList(cName, depType, tPair.b.asReferenceType().getQualifiedName()));
                            addTypeParameterDeps(c, depType, tPair.b.asReferenceType());
                        }
                    }
                }

                public <V extends Node> void processVar(NodeWithVariables<V> n, String depType,
                        ClassOrInterfaceDeclaration c) {
                    final String cName = c.getFullyQualifiedName().get();
                    for (VariableDeclarator v : n.getVariables()) {
                        ResolvedValueDeclaration rv = v.resolve();
                        ResolvedType t = rv.getType();
                        if (t.isReferenceType()) {
                            deps.add(Arrays.asList(cName, depType, t.asReferenceType().getQualifiedName()));
                            addTypeParameterDeps(c, depType, t.asReferenceType());
                        }
                    }
                }
            }, null);

        }
        return deps;
    }

    protected boolean isGetter(CallableDeclaration<?> n) {
        return n.getNameAsString().startsWith("get") && n.getParameters().isEmpty();
    }

    protected boolean isSetter(CallableDeclaration<?> n) {
        return n.getNameAsString().startsWith("set") && n.getParameters().size() == 1;
    }

}