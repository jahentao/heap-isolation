package dnet.mt.hi.analyzer.st;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

class TypeHierarchyInfoExtractor {

    static Map<Class, TypeNode> allTypeNodes = new ConcurrentHashMap<>();
    static Map<String, Class> nameToClassMap = new ConcurrentHashMap<>();
    static Map<Class, String> classToNameMap = new ConcurrentHashMap<>();

    void extract() {

        FileSystem fs = FileSystems.getFileSystem(URI.create("jrt:/"));
        Path top = fs.getPath("/");

        try (Stream<Path> stream = Files.walk(top)) {
            stream.forEach(path -> processType(path.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void processType(String path) {

        if (path.endsWith(".class")) {

            String[] elements = path.split("/");
            if (elements[2].equals("java.base")) {

                String className = extractTypeName(elements);
                try {
                    System.out.println(String.format("Loading %s...", className));
                    Class clazz = Class.forName(className);
                    nameToClassMap.put(className, clazz);
                    classToNameMap.put(clazz, className);
                    if (clazz != null) {
                        TypeNode node = new TypeNode(clazz);
                        processParent(node, clazz.getSuperclass());
                        for (Class i : clazz.getInterfaces()) {
                            processParent(node, i);
                        }
                        allTypeNodes.put(clazz, node);
                    }
                } catch (Throwable t) {
                    System.err.println(String.format("Unable to load %s.", className));
                }

            }
        }

    }

    private static String extractTypeName(String[] elements) {

        StringBuilder sb = new StringBuilder();

        for (int i = 3; i < elements.length; i++) {

            sb.append(elements[i]);
            if (i != elements.length - 1) {
                sb.append(".");
            }

        }

        return sb.toString().replace(".class", "");

    }

    void processParent(TypeNode childNode, Class parentClazz) {
        if (parentClazz != null) {
            TypeNode parentNode;
            if (allTypeNodes.containsKey(parentClazz)) {
                parentNode = allTypeNodes.get(parentClazz);
            } else {
                parentNode = new TypeNode(parentClazz);
            }
            parentNode.directChildren.add(childNode);
        }
    }

}
