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

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedParameterDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;

public class JavaParserUtil {

    private final List<Path> sourcePaths = new ArrayList<>();
    private String[] classpath;
    private List<CompilationUnit> compilationUnits;

    public static String DEPENDENCY = "d";
    public static String SUPERCLASS = "s";
    public static String FIELD = "f";
    public static String METHOD_PARAM = "p";
    public static String METHOD_RETURN_TYPE = "r";

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
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public List<List<String>> getDependencies() {
        final List<List<String>> deps = new ArrayList<>();
        for (final CompilationUnit cu : compilationUnits) {
            final List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);
            for (final ClassOrInterfaceDeclaration c : classes) {
                deps.addAll(getSuperclassDeps(c));
                deps.addAll(getFieldDeps(c));
                deps.addAll(getMethodParamDeps(c));
                deps.addAll(getMethodReturnTypeDeps(c));
            }
        }
        return deps;
    }

    public List<List<String>> getSuperclassDeps(final ClassOrInterfaceDeclaration c) {
        final String cName = c.getFullyQualifiedName().get();
        final List<List<String>> deps = new ArrayList<>();
        final ResolvedReferenceTypeDeclaration ct = c.resolve();
        for (final ResolvedReferenceType sup : ct.getAllAncestors()) {
            deps.add(Arrays.asList(cName, SUPERCLASS, sup.getQualifiedName()));
        }
        return deps;

    }

    private Collection<? extends List<String>> getFieldDeps(final ClassOrInterfaceDeclaration c) {
        final String cName = c.getFullyQualifiedName().get();
        final List<List<String>> deps = new ArrayList<>();
        final ResolvedReferenceTypeDeclaration ct = c.resolve();
        for (final ResolvedFieldDeclaration f : ct.getAllFields()) {
            final ResolvedType t = f.getType();
            if (t.isReferenceType()) {
                deps.add(Arrays.asList(cName, FIELD, t.asReferenceType().getQualifiedName()));
            }
        }
        return deps;
    }

    private Collection<? extends List<String>> getMethodParamDeps(final ClassOrInterfaceDeclaration c) {
        final String cName = c.getFullyQualifiedName().get();
        final List<List<String>> deps = new ArrayList<>();
        final ResolvedReferenceTypeDeclaration ct = c.resolve();
        for (final MethodUsage m : ct.getAllMethods()) {
            final ResolvedMethodDeclaration md = m.getDeclaration();
            for (int i = 0; i < md.getNumberOfParams(); i++) {
                ResolvedParameterDeclaration param = md.getParam(i);
                ResolvedType t = param.getType();
                if (t.isReferenceType()) {
                    deps.add(Arrays.asList(cName, METHOD_PARAM, t.asReferenceType().getQualifiedName()));
                }
            }
        }
        return deps;
    }

    private Collection<? extends List<String>> getMethodReturnTypeDeps(final ClassOrInterfaceDeclaration c) {
        final String cName = c.getFullyQualifiedName().get();
        final List<List<String>> deps = new ArrayList<>();
        final ResolvedReferenceTypeDeclaration ct = c.resolve();
        for (final MethodUsage m : ct.getAllMethods()) {
            final ResolvedMethodDeclaration md = m.getDeclaration();
            ResolvedType t = md.getReturnType();
            if (t.isReferenceType()) {
                deps.add(Arrays.asList(cName, METHOD_RETURN_TYPE, t.asReferenceType().getQualifiedName()));
            }
            
        }
        return deps;
    }

    public List<List<String>> getAllDependencies() {
        final List<List<String>> deps = new ArrayList<>();
        for (final CompilationUnit cu : compilationUnits) {
            final List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);

            for (final ClassOrInterfaceDeclaration c : classes) {
                final String cName = c.getFullyQualifiedName().get();
                final List<Type> types = c.findAll(Type.class);
                for (final Type t : types) {
                    if (t instanceof ClassOrInterfaceType) {
                        if (isDep(t)) {
                            deps.add(Arrays.asList(cName, DEPENDENCY, t.resolve().asReferenceType().getQualifiedName()));
                        }
                    }
                }
            }
        }

        return deps;
    }

    public List<String> getClasses() {
        final ArrayList<String> classes = new ArrayList<String>();
        for (final CompilationUnit cu : compilationUnits) {
            final List<ClassOrInterfaceDeclaration> cuClasses = cu.findAll(ClassOrInterfaceDeclaration.class);

            for (final ClassOrInterfaceDeclaration c : cuClasses) {
                classes.add(c.getFullyQualifiedName().get());
            }
        }
        return classes;
    }

    private boolean isSource(final CompilationUnit cu) {
        final Path path = cu.getStorage().get().getPath();
        for (final Path sourcePath : sourcePaths) {
            if (path.startsWith(sourcePath)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getSourceClasses() {
        final ArrayList<String> sourceClasses = new ArrayList<String>();
        for (final CompilationUnit cu : compilationUnits) {
            if (isSource(cu)) {
                final List<ClassOrInterfaceDeclaration> cuClasses = cu.findAll(ClassOrInterfaceDeclaration.class);
                for (final ClassOrInterfaceDeclaration c : cuClasses) {
                    sourceClasses.add(c.getFullyQualifiedName().get());
                }
            }
        }
        return sourceClasses;
    }

    /**
     * Parses all java files in source_dirs.
     * @return A list of CompilationUnit of the parsed java files
     * @throws IOException
     */
    private List<CompilationUnit> parse() throws IOException {
        final CombinedTypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver(false));
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
                // System.out.println(cp);
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

    private List<List<String>> detailedDependencies(final List<CompilationUnit> cus) {
        System.out.println("Dependencies");
        final List<List<String>> deps = new ArrayList<>();

        for (final CompilationUnit cu : cus) {
            final List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);

            for (final ClassOrInterfaceDeclaration c : classes) {
                System.out.print(c.isInterface() ? "interface " : "class ");
                System.out.println(c.getFullyQualifiedName().get());
                final List<FieldDeclaration> fields = c.findAll(FieldDeclaration.class);
                for (final FieldDeclaration f : fields) {
                    for (final VariableDeclarator v : f.getVariables()) {
                        System.out.println("\t" + v.getNameAsString() + ": " + v.getTypeAsString());
                    }
                }
                final List<MethodDeclaration> methods = c.findAll(MethodDeclaration.class);
                for (final MethodDeclaration m : methods) {
                    System.out.print("\t" + m.getNameAsString() + "(");
                    final List<Parameter> params = m.findAll(Parameter.class);
                    for (final Parameter p : params) {
                        System.out.print(p.getNameAsString() + ":" + p.getTypeAsString() + ", ");

                    }
                    System.out.println(")");
                }
            }
        }

        return deps;
    }

    private boolean isDep(final Type t) {
        try {

            final ResolvedType rt = t.resolve();
            if (rt.isReferenceType()) {
                return true;
            }

        } catch (final UnsolvedSymbolException e) {

        }
        return false;
    }

}