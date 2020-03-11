package org.jpavlich;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;
import com.sun.source.util.JavacTask;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Symbol.*;

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

    private String REPOSITORY_INTERFACE = "org.springframework.data.repository.Repository";

    private Iterable<? extends Element> results;

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
    public void analyze(final String[] source_dirs, final String[] classpath) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try {
            StandardJavaFileManager fm = compiler.getStandardFileManager(null, null, null);
            StringWriter out = new StringWriter();
            PrintWriter outWriter = new PrintWriter(out);

            String cp = String.join(":", classpath);
            cp += ":" + String.join(":", source_dirs);

            // System.out.println(cp);

            List<File> files = Files.walk(Paths.get(source_dirs[0])).map(f -> f.toFile())
                    .filter(f -> f.getAbsolutePath().toLowerCase().endsWith(".java")).collect(Collectors.toList());

            Iterable<? extends JavaFileObject> input = fm.getJavaFileObjects(files.toArray(new File[0]));

            List<String> options = Arrays.asList("-cp", cp);

            JavacTask task = (JavacTask) compiler.getTask(outWriter, fm, null, options, null, input); // .call();
            // compiler.getTask(outWriter, fm, null, options, null, input).call();
            task.parse();
            this.results = task.analyze();

            System.out.println(out.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<List<String>> getDependencies() {
        List<List<String>> deps = new ArrayList<>();
        DepScanner ds = new DepScanner() {
            ClassSymbol currentClass;

            @Override
            protected void visit(MethodSymbol ms) {

            }

            @Override
            protected void visitField(VarSymbol vs) {
                TypeSymbol t = vs.type.asElement();
                if (t instanceof ClassSymbol) {
                    ClassSymbol cs = (ClassSymbol) t;
                    deps.add(Arrays.asList(currentClass.fullname.toString(), FIELD, cs.fullname.toString()));
                }

            }

            @Override
            protected void visitParam(VarSymbol vs) {

            }

            @Override
            protected void visit(ClassSymbol cs) {
                currentClass = cs;
            }
            

        };
        ds.exclude("java", "com.sun");
        ds.include("sagan");
        ds.visitAll(results);
        return deps;
    }

}