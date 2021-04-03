package dnet.mt.hi.analyzer.st;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class SharedTypeDetector {

    private static TypeHierarchyInfoExtractor hierarchyExtractor = new TypeHierarchyInfoExtractor();
    private static SharedTypeListExpander listExpander = new SharedTypeListExpander();

    public static void main(String[] args) {

        if (args.length != 2) {
            throw new IllegalArgumentException("Missing input argument(s).");
        }

        int argIndex = 0;
        String initialSeedFile = args[argIndex++];
        String output = args[argIndex++];

        try {
            Set<String> initialSeed = loadInitialSeed(initialSeedFile);
            hierarchyExtractor.extract();
            listExpander.init(initialSeed);
            listExpander.expand();
            persist(output);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void persist(String output) throws IOException {
        StringBuilder sb = new StringBuilder();
        SharedTypeListExpander.sharedTypes.forEach(c -> {
            sb.append(TypeHierarchyInfoExtractor.classToNameMap.get(c));
            sb.append("\n");
        });
        Files.write(Paths.get(output), sb.toString().getBytes());
    }

    private static Set<String> loadInitialSeed(String initialSeedFile) throws IOException {
        Set<String> initialSeed = new HashSet<>();
        BufferedReader reader;

        reader = new BufferedReader(new FileReader(
                initialSeedFile));
        String line = reader.readLine();
        while (line != null) {
            initialSeed.add(line);
            line = reader.readLine();
        }
        reader.close();

        return initialSeed;
    }

}
