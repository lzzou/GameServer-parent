package com.game.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/*
 * 一个工具类，获取指定包下的所有文件
 */
public class ClassUtil {
    private static final Logger log = LoggerFactory.getLogger(ClassUtil.class);

    public static void main(String[] args) throws Exception {
        List<Class<?>> classList = getClassList("", true);
        for (Class<?> aClass : classList) {
            System.out.println(aClass);
        }
    }

    /**
     * 给我一个包名，获取该包下的所有class文件
     *
     * @param packageName 包名 com.xxx.xxx
     * @param isRecursive 是否递归
     * @return 返回class文件的集合
     * @throws Exception
     */
    public static List<Class<?>> getClassList(String packageName, boolean isRecursive) throws Exception {

        //声明一个返回List
        List<Class<?>> classList = new ArrayList<>();
        //将对应的包名转换为路径
        //Enumeration枚举接口，当中有两个方法，一个是判断是否有下一个元素，还有一个是取到下一个元素
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageName.replace('.', '/'));
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            //System.out.println(url);	//	file:/D:/SXTJava/annotation/bin/annotation
            if (url != null) {
                //拿到文件的协议
                String protocol = url.getProtocol();
                //如果是file
                if ("file".equals(protocol)) {
                    //取到文件的路径
                    String packagePath = url.getPath();// /D:/SXTJava/annotation/bin/annotation
                    addClass(classList, packagePath, packageName, isRecursive);
                } else if ("jar".equals(protocol)) { //如果是jar包的情况:此情况没有测试
                    JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                    JarFile jarFile = jarURLConnection.getJarFile(); //取到jar包下的文件
                    Enumeration<JarEntry> jarEntries = jarFile.entries();
                    while (jarEntries.hasMoreElements()) { //遍历jarEnyries
                        JarEntry jarEntry = jarEntries.nextElement();//取到元素
                        String jarEntryName = jarEntry.getName(); //取到文件名
                        if (jarEntryName.endsWith(".class")) { //如果文件名以.class结尾，将对应的文件添加至集合中
                            String name = jarEntryName.substring(0, jarEntryName.lastIndexOf("."));
                            System.out.println(name);
                            String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");//取到类名

                            if (isRecursive || className.substring(0, className.lastIndexOf(".")).equals(packageName)) {
                                classList.add(Class.forName(className));
                            }
                        }

                    }

                }
            }
        }
        return classList;

    }

    /**
     * 根据注解筛选对应的class文件
     *
     * @param packageName     包名
     * @param annotationClass 注解类
     * @param isRecursive     是否递归
     * @return
     */
    public static List<Class<?>> getClassListByAnnotation(String packageName, Class<? extends Annotation> annotationClass, boolean isRecursive) throws Exception {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        classList = getClassList(packageName, isRecursive);
        for (Class<?> clz : classList) {
            if (!clz.isAnnotationPresent(annotationClass)) {
                classList.remove(clz);
            }
        }
        return classList;
    }


    /**
     * 将对应包名下的所有.class文件加入到classList集合中
     *
     * @param classList   存放classList文件的集合
     * @param packagePath 包路径
     * @param packageName 包名
     * @param isRecursive 是否递归
     * @throws ClassNotFoundException
     */
    private static void addClass(List<Class<?>> classList, String packagePath, String packageName,
                                 boolean isRecursive) throws ClassNotFoundException {
        //取到路径下所有的对应的文件，包括.class文件和目录
        File[] files = getClassFiles(packagePath);

        if (files != null) {  //如果files不为空的话，对它进行迭代

            for (File file : files) {
                //取到文件名
                String fileName = file.getName(); //Column.class
                if (file.isFile()) {//如果取到的是文件
                    //取到对应的类名,这里的类名是权限定名
                    String className = getClassName(packageName, fileName);
                    classList.add(Class.forName(className));
                } else {
                    if (isRecursive) {
                        ///D:/SXTJava/annotation/bin/annotation+包名（fileName:test）
                        String subPackagePath = getSubPackagePath(packagePath, fileName);
                        String subPackageName = getSubPackageName(packageName, fileName);
                        //递归调用自己
                        addClass(classList, subPackagePath, subPackageName, isRecursive);
                    }
                }
            }

        }
    }

    /**
     * 获取子包名
     *
     * @param packageName
     * @param fileName
     * @return
     */
    public static String getSubPackageName(String packageName, String fileName) {
        String subPackageName = fileName;
        if (packageName != null && (!"".equals(packageName))) {
            subPackageName = packageName + "." + subPackageName;
        }
        return subPackageName;
    }

    /**
     * 获取子目录
     *
     * @param packagePath 包的路径
     * @param fileName    文件的路径
     * @return
     */
    public static String getSubPackagePath(String packagePath, String fileName) {
        String subPackagePath = fileName;
        if (packagePath != null && (!"".equals(packagePath))) {
            subPackagePath = packagePath + "/" + subPackagePath;
        }
        return subPackagePath;
    }

    /**
     * 根据传入的包名和文件名返回对应类的全限定名
     *
     * @param packageName 包名
     * @param fileName    文件名 	类名.后缀名
     * @return 包名.类名
     */
    private static String getClassName(String packageName, String fileName) {
        String className = fileName.substring(0, fileName.indexOf("."));
        if (packageName != null && !"".equals(packageName)) {
            className = packageName + "." + className;
        }
        return className;
    }

    /**
     * 获取class文件
     *
     * @param packagePath
     * @return
     */
    private static File[] getClassFiles(String packagePath) {
        //FileFilter文件过滤器
        return new File(packagePath).listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                //如果是.class文件，或者是目录就返回true
                return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
            }

        });
    }

    /**
     * 取得某一类所在包的所有类名 不含迭代
     *
     * @param classLocation
     * @param packageName
     * @return
     */
    public static String[] getPackageAllClassName(String classLocation, String packageName) {

        // 将packageName分解
        String[] packagePathSplit = packageName.split("[.]");
        String realClassLocation = classLocation;
        int packageLength = packagePathSplit.length;
        for (int i = 0; i < packageLength; i++) {
            realClassLocation = realClassLocation + File.separator + packagePathSplit[i];
        }
        File packeageDir = new File(realClassLocation);
        if (packeageDir.isDirectory()) {
            String[] allClassName = packeageDir.list();
            return allClassName;
        }
        return null;
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param pack
     * @return
     */
    public static Set<Class<?>> getClasses(Package pack) {
        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageName = pack.getName();
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath,
                            recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class")
                                            && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(
                                                packageName.length() + 1,
                                                name.length() - 6);
                                        try {
                                            // 添加到classes
                                            classes.add(Class.forName(packageName + '.'
                                                    + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        log.error("get classes error.", e);
                    }
                }
            }
        } catch (IOException e) {
            log.error("get classes error.", e);
        }
        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    public static void findAndAddClassesInPackageByFile(String packageName,
                                                        String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                        || (file.getName().endsWith(".class"));
            }
        });

        // 循环所有文件

        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(
                        packageName + "." + file.getName(),
                        file.getAbsolutePath(), recursive, classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0,
                        file.getName().length() - 6);

                try {
                    // 添加到集合中去
                    classes.add(Class.forName(packageName + '.' + className));

                } catch (ClassNotFoundException e) {
                    log.error("get classes error.", e);
                }
            }
        }
    }

    /**
     * Scans all classes accessible from the context class loader which belong
     * to the given package and subpackages. Adapted from
     * http://snippets.dzone.com/posts/show/4831 and extended to support use of
     * JAR files
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static List<Class<?>> getClasses(String packageName) {
        try {
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
            List<String> dirs = new ArrayList<String>();

            while (resources.hasMoreElements()) {
                URL resource = (URL) resources.nextElement();
                dirs.add(resource.getFile());
            }

            TreeSet<String> classes = new TreeSet<String>();

            for (String directory : dirs) {
                classes.addAll(findClasses(directory, packageName));
            }

            ArrayList<Class<?>> classList = new ArrayList<Class<?>>();

            for (String clazz : classes) {
                if (clazz.startsWith(".")) {
                    clazz = clazz.substring(1, clazz.length());
                }

                classList.add(Class.forName(clazz));
            }

            return classList;
        } catch (Exception e) {
            log.error("get classes error.", e);
            return null;
        }
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * subdirs. Adapted from http://snippets.dzone.com/posts/show/4831 and
     * extended to support use of JAR files
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static TreeSet<String> findClasses(String directory,
                                               String packageName) throws Exception {
        TreeSet<String> classes = new TreeSet<String>();
        if (directory.startsWith("file:") && directory.contains("!")) {
            String[] split = directory.split("!");
            URL jar = new URL(split[0]);
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            ZipEntry entry = null;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName().replaceAll("[$].*", "").replaceAll("[.]class", "").replace('/',
                            '.');
                    if (className.startsWith(packageName) || packageName.isEmpty()) {
                        classes.add(className);
                    }
                }
            }
        }
        File dir = new File(directory);
        if (!dir.exists()) {
            return classes;
        }
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file.getAbsolutePath(), packageName
                        + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(packageName
                        + '.'
                        + file.getName().substring(0,
                        file.getName().length() - 6));
            }
        }

        return classes;
    }

    /**
     * 构造一个不定参数的类
     *
     * @param newoneClass className
     * @param args
     * @return
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object newInstance(Class<?> newoneClass, String args)
            throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        Constructor<?> con = newoneClass.getConstructor(String.class);
        return con.newInstance(args);
    }

    /**
     * 通过classLoader加载类
     *
     * @param className:com.a.b.c
     * @return
     */
    public static <T> Class<T> getClass(String className) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return getClass(loader, className);
    }

    /**
     * 通过classLoader加载类
     *
     * @param loader         :类加载器
     * @param className：类包路径
     * @return
     */
    public static <T> Class<T> getClass(ClassLoader loader, String className) {
        try {
            Class<T> clazz = (Class<T>) loader.loadClass(className);
            return clazz;
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage());
        }

        return null;
    }

    /**
     * 根据类创建实例
     *
     * @param t
     * @return
     */
    public static <T> T createInstance(Class<T> t) {
        try {
            if (t == null) {
                return null;
            }

            return t.newInstance();
        } catch (InstantiationException e) {
            log.error(e.getMessage());
        } catch (IllegalAccessException e) {
            log.error(e.getMessage());
        }

        return null;

    }

    /**
     * 方法是否是抽象
     *
     * @param method
     * @return
     */
    public static boolean isAbstractMethod(Method method) {
        return Modifier.isAbstract(method.getModifiers());
    }

    /**
     * 类是否是抽象
     *
     * @param c
     * @return
     */
    public static boolean isAbstractClass(Class<?> c) {
        return Modifier.isAbstract(c.getModifiers());
    }
}
