package org.jpavlich;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.sun.source.util.JavacTask;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

public class JavaParserUtil {

    private Iterable<? extends Element> results;
    private DepScanner ds = new DepScanner();

    /**
     * @param sourcePaths Folders where to look for java files
     * @param classpath   List of jar files to support the type resolver
     */
    public void analyze(final String[] sourceDirs, final String[] classpath) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try {
            StandardJavaFileManager fm = compiler.getStandardFileManager(null, null, null);
            StringWriter out = new StringWriter();
            PrintWriter outWriter = new PrintWriter(out);

            String cp = String.join(":", classpath);
            cp += ":" + String.join(":", sourceDirs);

            List<File> files = new ArrayList<>();
            for (String sourceDir : sourceDirs) {
                files.addAll(FileUtils.listFiles(new File(sourceDir), new String[] {"java"}, true));
            }

            System.out.println("Processing " + files.size() + " files");
            // for (File file : files) {
            //     System.out.println(file);
            // }

            Iterable<? extends JavaFileObject> input =
                    fm.getJavaFileObjects(files.toArray(new File[0]));

            List<String> options = Arrays.asList("-cp", cp);

            JavacTask task =
                    (JavacTask) compiler.getTask(outWriter, fm, null, options, null, input); // .call();
            // compiler.getTask(outWriter, fm, null, options, null, input).call();
            task.parse();
            this.results = task.analyze();

            System.out.println(out.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ClassDepGraph getDependencies() {

        try {
            ds.exclude("java", "com.sun");
            ds.visitAll(results);
            return ds.getClassDepGraph();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public void includeOnly(String... includePackages) {
        ds.includeOnly(includePackages);
    }

    public void exclude(String... excludePackages) {
        ds.exclude(excludePackages);
    }

}
