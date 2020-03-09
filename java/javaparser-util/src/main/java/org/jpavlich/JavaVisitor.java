package org.jpavlich;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.google.common.reflect.TypeToken;

import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * JavaVisitor
 */
public abstract class JavaVisitor {
    Set<Type> visited = new HashSet<>();
    private String[] packages;

    abstract void visit(Class<?> c, Class<?> container);

    abstract void visit(Method m, Class<?> c);

    abstract void visit(Field f, Class<?> c);

    abstract void visit(Parameter p, Class<?> c);

    private boolean isInPackages(String packageName) { // TODO Optimize
        for (String pkg : packages) {
            if (packageName.startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }

    private boolean shouldVisit(Type t) {
        boolean isInPackages = true;
        if (t instanceof Class) {
            Class<?> c = (Class<?>) t;
            isInPackages = isInPackages(c.getPackage().getName());
        }

        return !visited.contains(t) && isInPackages;
    }

    void visitAll(String... packages) {
        visited.clear();
        this.packages = packages;
        List<String> classes = getClassesInPackages(packages);
        for (String cname : classes) {
            Class<?> c;
            try {
                c = Class.forName(cname);
                _visit(c, null);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void _visit(ParameterizedType pt) {
        if (shouldVisit(pt)) {
            visited.add(pt);
            for (Type ta : pt.getActualTypeArguments()) {
                if (ta instanceof Class) {
                    Class<?> c = (Class<?>) ta;
                    if (isInPackages(c.getPackage().getName())) {
                        visit(c, null);
                    }
                }
            }
        }
    }

    private void _visit(Class<?> c, Class<?> container) {
        if (shouldVisit(c)) {
            visited.add(c);
            TypeToken<?> ctok = TypeToken.of(c);
            visit(c, container);
            for (TypeToken<?> t : ctok.getTypes()) {
                _visit(t.getRawType(), container);
                Type rt = t.getType();
                if (rt instanceof ParameterizedType) {
                    _visit((ParameterizedType) rt);
                }

            }

            for (Field f : FieldUtils.getAllFields(c)) {
                visit(f, c);
                Type t = TypeToken.of(f.getType()).getType();
                if (t instanceof ParameterizedType) {
                    _visit((ParameterizedType) t);
                }
            }

            for (Method m : getAllMethods(c)) {
                visit(m, c);
                for (Parameter p : m.getParameters()) {
                    visit(p, c);
                }
                Type t = TypeToken.of(m.getReturnType()).getType();
                if (t instanceof ParameterizedType) {
                    _visit((ParameterizedType) t);
                }
            }
            for (Class<?> ic : getAllClasses(c)) {
                _visit(ic, c);
            }
        }
    }

    private List<Method> getAllMethods(Class<?> c) {
        List<Method> methods = new ArrayList<Method>();

        if (c != null && c != Object.class) {
            methods.addAll(Arrays.asList(c.getDeclaredMethods()));
            methods.addAll(getAllMethods(c.getSuperclass()));
            for (Class<?> i : c.getInterfaces()) {
                methods.addAll(getAllMethods(i));
            }
        }
        return methods;
    }

    private List<Class<?>> getAllClasses(Class<?> c) {
        List<Class<?>> classes = new ArrayList<Class<?>>();

        if (c != null && c != Object.class) {
            classes.addAll(Arrays.asList(c.getDeclaredClasses()));
            classes.addAll(getAllClasses(c.getSuperclass()));
            for (Class<?> i : c.getInterfaces()) {
                classes.addAll(getAllClasses(i));
            }
        }
        return classes;
    }

    private List<String> getClassesInPackages(String... packages) {
        List<String> classes = new ArrayList<>();
        for (String pkg : packages) {
            classes.addAll(getClassesInPackage(pkg));
        }
        return classes;
    }

    // https://stackoverflow.com/a/28678088
    private final List<String> getClassesInPackage(String packageName) {
        String path = packageName.replaceAll("\\.", File.separator);
        List<String> classes = new ArrayList<>();
        String[] classPathEntries = System.getProperty("java.class.path").split(System.getProperty("path.separator"));

        String name;
        for (String classpathEntry : classPathEntries) {
            if (classpathEntry.endsWith(".jar")) {
                File jar = new File(classpathEntry);
                try {
                    JarInputStream is = new JarInputStream(new FileInputStream(jar));
                    JarEntry entry;
                    while ((entry = is.getNextJarEntry()) != null) {
                        name = entry.getName();
                        if (name.endsWith(".class")) {
                            if (name.contains(path) && name.endsWith(".class")) {
                                String classPath = name.substring(0, entry.getName().length() - 6);
                                classPath = classPath.replaceAll("[\\|/]", ".");
                                System.out.println("CP: " + classPath);
                                classes.add(classPath);
                            }
                        }
                    }
                } catch (Exception ex) {
                    // Silence is gold
                }
            } else {
                try {
                    File base = new File(classpathEntry + File.separatorChar + path);

                    Files.walk(base.toPath()).filter(f -> f.toString().endsWith("class")).forEach(f -> {
                        // name = f.
                        // name = name.substring(0, name.length() - 6);
                        // classes.add(Class.forName(packageName + "." + name));
                        String classname = FileSystems.getDefault().getPath(classpathEntry).relativize(f).toString()
                                .replaceAll(File.separator, ".");
                        classname = classname.substring(0, classname.lastIndexOf("."));
                        if (!classname.substring(classname.lastIndexOf(".")).contains("$")) { // Excludes inner classes
                            // System.out.println(classname);
                            classes.add(classname);
                        }
                    });
                } catch (Exception ex) {
                    // Silence is gold
                }
            }
        }

        return classes;
    }

}