package dnet.mt.hi.analyzer;

import dnet.mt.hi.analyzer.enums.AccessModifier;
import dnet.mt.hi.analyzer.enums.MutabilityStatus;
import dnet.mt.hi.analyzer.model.StaticFieldProperties;
import dnet.mt.hi.analyzer.processors.Initializer;
import dnet.mt.hi.analyzer.processors.SharedTypeListExpander;
import dnet.mt.hi.analyzer.processors.StaticFieldPropertyExtractor;
import dnet.mt.hi.analyzer.processors.TypeHierarchyBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {

    private static Initializer initializer = new Initializer();
    private static TypeHierarchyBuilder hierarchyBuilder = new TypeHierarchyBuilder();
    private static SharedTypeListExpander listExpander = new SharedTypeListExpander();
    private static StaticFieldPropertyExtractor propertyExtractor = new StaticFieldPropertyExtractor();

    public static void main(String[] args) {

        if (args.length != 2) {
            throw new IllegalArgumentException("Missing input argument(s).");
        }

        int argIndex = 0;
        String initialSeedFile = args[argIndex++];
        String outputBase = args[argIndex++];

        try {
            System.out.println("Loading the initial seed...");
            Set<String> initialSeed = loadInitialSeed(initialSeedFile);
            System.out.println("Loading classes...");
            initializer.init();
            System.out.println("Building the class hierarchy...");
            hierarchyBuilder.build();
            System.out.println("Initializing the expander...");
            listExpander.init(initialSeed);
            System.out.println("Expanding...");
            listExpander.expand();
            System.out.println("Persisting shared types...");
            persistSharedTypes(outputBase);
            System.out.println("Extracting static field properties...");
            propertyExtractor.extract();
            System.out.println("Persisting static field properties...");
            persistFieldProperties(outputBase);
            System.out.println("Persisting distinct types of static fields...");
            persistDistinctTypes(outputBase);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void persistDistinctTypes(String outputBase) throws IOException {
        Set<String> distinctTypes = StaticFieldPropertyExtractor.properties.stream().
                filter(sfp -> sfp.isFinal && !sfp.isArray && !sfp.mutabilityStatus.equals(MutabilityStatus.IMMUTABLE)).
                map(sfp -> sfp.type).collect(Collectors.toSet());
        StringBuilder sb = new StringBuilder();
        distinctTypes.forEach(t -> {
            sb.append(t);
            sb.append("\n");
        });
        Files.write(Paths.get(outputBase, "distinct_final_non_array_field_types.list"), sb.toString().getBytes());
    }

    private static void persistFieldProperties(String outputBase) throws IOException {
        persist(outputBase, "final_immutables.csv", sfp -> !sfp.isArray && isImmutableFinal(sfp));

        persist(outputBase, "arrays/final_privates.csv", sfp -> sfp.isArray && sfp.isFinal && sfp.access.equals(AccessModifier.PRIVATE));
        persist(outputBase, "arrays/nonfinal_privates.csv", sfp -> sfp.isArray && !sfp.isFinal && sfp.access.equals(AccessModifier.PRIVATE));
        persist(outputBase, "arrays/final_package-privates.csv", sfp -> sfp.isArray && sfp.isFinal && sfp.access.equals(AccessModifier.PACKAGE));
        persist(outputBase, "arrays/nonfinal_package-privates.csv", sfp -> sfp.isArray && !sfp.isFinal && sfp.access.equals(AccessModifier.PACKAGE));
        persist(outputBase, "arrays/final_protecteds.csv", sfp -> sfp.isArray && sfp.isFinal && sfp.access.equals(AccessModifier.PROTECTED));
        persist(outputBase, "arrays/nonfinal_protecteds.csv", sfp -> sfp.isArray && !sfp.isFinal && sfp.access.equals(AccessModifier.PROTECTED));
        persist(outputBase, "arrays/final_publics.csv", sfp -> sfp.isArray && sfp.isFinal && sfp.access.equals(AccessModifier.PUBLIC));
        persist(outputBase, "arrays/nonfinal_publics.csv", sfp -> sfp.isArray && !sfp.isFinal && sfp.access.equals(AccessModifier.PUBLIC));

        persist(outputBase, "references/nonfinal_privates.csv", sfp -> !sfp.isArray && !isImmutableFinal(sfp) && !sfp.isFinal && sfp.access.equals(AccessModifier.PRIVATE));
        persist(outputBase, "references/final_privates.csv", sfp -> !sfp.isArray && !isImmutableFinal(sfp) && sfp.isFinal && sfp.access.equals(AccessModifier.PRIVATE));
        persist(outputBase, "references/nonfinal_package-privates.csv", sfp -> !sfp.isArray && !isImmutableFinal(sfp) && !sfp.isFinal && sfp.access.equals(AccessModifier.PACKAGE));
        persist(outputBase, "references/final_package-privates.csv", sfp -> !sfp.isArray && !isImmutableFinal(sfp) && sfp.isFinal && sfp.access.equals(AccessModifier.PACKAGE));
        persist(outputBase, "references/nonfinal_protecteds.csv", sfp -> !sfp.isArray && !isImmutableFinal(sfp) && !sfp.isFinal && sfp.access.equals(AccessModifier.PROTECTED));
        persist(outputBase, "references/final_protecteds.csv", sfp -> !sfp.isArray && !isImmutableFinal(sfp) && sfp.isFinal && sfp.access.equals(AccessModifier.PROTECTED));
        persist(outputBase, "references/nonfinal_publics.csv", sfp -> !sfp.isArray && !isImmutableFinal(sfp) && !sfp.isFinal && sfp.access.equals(AccessModifier.PUBLIC));
        persist(outputBase, "references/final_publics.csv", sfp -> !sfp.isArray && !isImmutableFinal(sfp) && sfp.isFinal && sfp.access.equals(AccessModifier.PUBLIC));
    }

    private static boolean isImmutableFinal(StaticFieldProperties sfp) {
        return sfp.isFinal && sfp.mutabilityStatus.equals(MutabilityStatus.IMMUTABLE);
    }

    private static void persist(String outputBase, String outputFile, Predicate<StaticFieldProperties> predicate) throws IOException {
        StringBuilder sb = new StringBuilder("Owner;Name;Type\n");
        StaticFieldPropertyExtractor.properties.stream().filter(predicate).forEach(sb::append);
        Files.write(Paths.get(outputBase, outputFile), sb.toString().getBytes());
    }

    private static void persistSharedTypes(String outputBase) throws IOException {
        StringBuilder sb = new StringBuilder();
        SharedTypeListExpander.sharedTypes.forEach(c -> {
            sb.append(Initializer.classToNameMap.get(c));
            sb.append("\n");
        });
        Files.write(Paths.get(outputBase, "shared_classes.list"), sb.toString().getBytes());
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
