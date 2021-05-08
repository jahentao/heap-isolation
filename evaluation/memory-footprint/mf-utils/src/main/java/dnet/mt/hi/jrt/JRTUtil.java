package dnet.mt.hi.jrt;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class JRTUtil {

    private static final List<String> ALL_JAVA_BASE_CLASS_NAMES = new LinkedList<>();

    private static final String JAVA_BASE_MODULE_NAME = "java.base";

    public static List<String> getAllJavaBaseClassNames() {
        if (ALL_JAVA_BASE_CLASS_NAMES.isEmpty()) {
            FileSystem fs = FileSystems.getFileSystem(URI.create("jrt:/"));
            Path top = fs.getPath("/");

            try (Stream<Path> stream = Files.walk(top)) {
                stream.forEach(path -> processClass(path.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ALL_JAVA_BASE_CLASS_NAMES;
    }

    private static void processClass(String path) {
        if (path.endsWith(".class")) {

            String[] elements = path.split("/");
            if (JAVA_BASE_MODULE_NAME.equals(elements[2])) {
                ALL_JAVA_BASE_CLASS_NAMES.add(extractClassName(elements));
            }

        }
    }

    private static String extractClassName(String[] elements) {

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
