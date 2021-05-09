package dnet.mt.hi.analyzer.processors;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class Initializer {

    public static Map<String, Class> nameToClassMap = new ConcurrentHashMap<>();
    public static Map<Class, String> classToNameMap = new ConcurrentHashMap<>();

    public void init() {
        FileSystem fs = FileSystems.getFileSystem(URI.create("jrt:/"));
        Path top = fs.getPath("/");

        try (Stream<Path> stream = Files.walk(top)) {
            stream.forEach(path -> loadClass(path.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadClass(String path) {
        if (path.endsWith(".class")) {
            String[] elements = path.split("/");
            if (elements[2].equals("java.base")) {
                String className = extractClassName(elements);
                try {
                    Class clazz = Class.forName(className, false, null);
                    if (clazz != null) {
                        Initializer.nameToClassMap.put(className, clazz);
                        Initializer.classToNameMap.put(clazz, className);
                    }
                } catch (Throwable t) {
                    System.err.println(String.format("Unable to load %s.", className));
                }
            }
        }
    }

    private String extractClassName(String[] elements) {
        StringBuilder sb = new StringBuilder();
        for (int i = 3; i < elements.length; i++) {
            sb.append(elements[i]);
            if (i != elements.length - 1) {
                sb.append(".");
            }
        }
        return sb.toString().replace(".class", "");
    }

}
