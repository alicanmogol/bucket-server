package com.ferer.server.test;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * acm
 */
public class GenericTester {

    public static void main(String[] args) {
        new GenericTester();
    }

    public GenericTester() {
        //runFilesTest();
        runClassLoaderTest();
    }

    private void runClassLoaderTest() {
        URLClassLoader classLoader = null;
        try {
            classLoader = new URLClassLoader(new URL[]{new URL("file:///tmp/Users/alicanmogol/projects/bucket-server-and-SampleApplication/bfm/out/production/bfm/")},this.getClass().getClassLoader()) {
                @Override
                public Class<?> loadClass(String name) throws ClassNotFoundException {
                    try {
                        Class<?> cl = super.loadClass(name);
                        return cl;
                    } catch (Exception ex) {
                        String file = name.replace('.', File.separatorChar) + ".class";
                        byte[] b = null;
                        try {
                            // This loads the byte code data from the file
                            b = loadClassFileData(file);
                            // defineClass is inherited from the ClassLoader class
                            // that converts byte array into a Class. defineClass is Final
                            // so we cannot override it
                            Class c = defineClass(name, b, 0, b.length);
                            resolveClass(c);
                            return c;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                }

                private byte[] loadClassFileData(String name) throws IOException {
                    // getClass().getClassLoader().getResourceAsStream(name);
                    InputStream stream = new FileInputStream(
                            new File("/tmp/Users/alicanmogol/projects/bucket-server-and-SampleApplication/bfm/out/production/bfm/" + name)
                    );
                    int size = stream.available();
                    byte buff[] = new byte[size];
                    DataInputStream in = new DataInputStream(stream);
                    in.readFully(buff);
                    in.close();
                    return buff;
                }
            };
            Thread.currentThread().setContextClassLoader(classLoader);
            Class classToLoad = Class.forName("com.bfm.app.action.ProductCRUDAction", true, classLoader);
            Object o = classToLoad.newInstance();
            System.out.println("classToLoad:" + classToLoad + " o: " + o);


            /*
            Application application = (Application) classToLoad.newInstance();
            application.setDevelopmentMode(true);
            application.start();
            // create headers and params maps
            ParamMap<String, Param<String, Object>> headers = new ParamMap<>();
            ParamMap<String, Param<String, Object>> params = new ParamMap<>();

            // add defaults request method
            params.addParam(new Param<String, Object>(RequestKeys.REQUEST_METHOD.getValue(), "GET"));
            params.addParam(new Param<String, Object>(RequestKeys.URI.getValue(), ""));
            params.addParam(new Param<String, Object>(RequestKeys.PROTOCOL.getValue(), "HTTP/1.1"));

            // add default headers
            headers.addParam(new Param<String, Object>(RequestKeys.HOST.getValue(), "localhost"));
            headers.addParam(new Param<String, Object>(RequestKeys.HOST_NAME.getValue(), "localhost"));
            headers.addParam(new Param<String, Object>(RequestKeys.HOST_PORT.getValue(), 80));

            application.runApplication(new Request(params, headers, new Session("")));
            System.out.println(application.getClass());
            */
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void runFilesTest() {
        findJars("/tmp/Users/alicanmogol/projects/bucket-server-and-SampleApplication/bfm/out/production/bfm/", new ArrayList<URL>());
    }

    private void findJars(String path, List<URL> jars) {
        File currentDir = new File(path);
        if (currentDir.exists()) {
            File[] files = currentDir.listFiles();
            if (files != null) {
                for (File currentFile : files) {
                    if (currentFile.isDirectory()) {
                        findJars(currentFile.getAbsolutePath(), jars);
                    } else {
                        if (currentFile.getAbsolutePath().endsWith(".jar")) {
                            System.out.println("current jar: " + currentFile.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }
}
