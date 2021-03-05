package org.fincl.miss.server.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ServiceClassLoader extends URLClassLoader {
    
    private ClassLoader parent;
    
    public ServiceClassLoader(String filePath, String basePackageName) throws ClassNotFoundException, IOException {
        
        super(new URL[] { new File(filePath).toURI().toURL() }, Thread.currentThread().getContextClassLoader());
        
        parent = Thread.currentThread().getContextClassLoader();
        
        // System.out.println("parent:" + this.getClass().getClassLoader());
        // spring application context에 classloader로 설정하면, context가 초기화 되면서
        // 지정된 class path 하위에서 자동 component scan됨.
        // allLoadClass(this, basePackageName);
    }
    
    private void allLoadClass(ClassLoader classLoader, String packageName) throws ClassNotFoundException, IOException {
        // assert classLoader != null;
        // System.out.println(classLoader);
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            
            File file = new File(resource.getFile());
            System.out.println("classloader while : [" + resource.getPath() + "]");
            if (file.isDirectory()) {
                dirs.add(new File(resource.getFile()));
            }
            else {
                // System.out.println(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                classLoader.loadClass(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                // classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        
        for (File directory : dirs) {
            findClasses(classLoader, directory, packageName);
        }
    }
    
    private void findClasses(ClassLoader classLoader, File directory, String packageName) throws ClassNotFoundException {
        if (!directory.exists()) {
            return;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            System.out.println("classloader files : [" + file.getAbsolutePath() + "]");
            if (file.isDirectory()) {
                // assert !file.getName().contains(".");
                findClasses(classLoader, file, packageName + "." + file.getName());
            }
            else if (file.getName().endsWith(".class")) {
                // System.out.println(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                classLoader.loadClass(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
            }
        }
    }
}
