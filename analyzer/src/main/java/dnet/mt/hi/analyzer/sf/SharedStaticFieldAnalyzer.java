package dnet.mt.hi.analyzer.sf;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SharedStaticFieldAnalyzer {

    private static final Collection<String> PRIMITIVE_TYPES = Arrays.asList("int", "long", "float", "double", "char",
            "byte", "short", "boolean");
    private static SourceRoot sourceRoot;

    public static void main(String[] args) {

        if (args.length != 3) {
            throw new IllegalArgumentException("Missing input argument(s).");
        }

        int argIndex = 0;
        String input = args[argIndex++];
        String bootstrapClassesFile = args[argIndex++];
        String outputBase = args[argIndex++];

        try {
            Set<String> bootstrapClassNames = loadBootstrapClassNames(bootstrapClassesFile);
            init(input);
            analyze(bootstrapClassNames);
            persist(outputBase.concat("sfp_arrays.csv"), sfp -> sfp.isArray);
            persist(outputBase.concat("sfp_primitives.csv"), sfp -> isPrimitive(sfp));
            persist(outputBase.concat("sfp_privates.csv"), sfp -> !sfp.isArray && !isPrimitive(sfp) && sfp.access.equals(FieldAccess.PRIVATE));
            persist(outputBase.concat("sfp_package-privates.csv"), sfp -> !sfp.isArray && !isPrimitive(sfp) && sfp.access.equals(FieldAccess.PACKAGE));
            persist(outputBase.concat("sfp_protecteds.csv"), sfp -> !sfp.isArray && !isPrimitive(sfp) && sfp.access.equals(FieldAccess.PROTECTED));
            persist(outputBase.concat("sfp_publics.csv"), sfp -> !sfp.isArray && !isPrimitive(sfp) && sfp.access.equals(FieldAccess.PUBLIC));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static boolean isPrimitive(StaticFieldProperties sfp) {
        return PRIMITIVE_TYPES.contains(sfp.type);
    }

    private static void persist(String output, Predicate<StaticFieldProperties> predicate) throws IOException {
        StringBuilder sb = new StringBuilder("Owner;Name;Type;Final;Array;Access\n");
        StaticFieldPropertiesExtractor.fields.stream().filter(predicate).forEach(sb::append);
        Files.write(Paths.get(output), sb.toString().getBytes());
    }

    private static void analyze(Set<String> bootstrapClassNames) {
        sourceRoot.getCompilationUnits().parallelStream().forEach(cu -> {
            cu.accept(new StaticFieldPropertiesExtractor(bootstrapClassNames), null);
        });
    }

    private static Set<String> loadBootstrapClassNames(String bootstrapClassesFile) throws IOException {
        Set<String> bootstrapClassNames = new HashSet<>();
        BufferedReader reader;

        reader = new BufferedReader(new FileReader(
                bootstrapClassesFile));
        String line = reader.readLine();
        while (line != null) {
            bootstrapClassNames.add(line);
            line = reader.readLine();
        }
        reader.close();

        return bootstrapClassNames;
    }

    private static void init(String inputSource) throws IOException {

        Log.setAdapter(new Log.Adapter() {

            @Override
            public void info(Supplier<String> message) {
            }

            @Override
            public void trace(Supplier<String> message) {
            }

            @Override
            public void error(Supplier<Throwable> throwableSupplier, Supplier<String> messageSupplier) {
                System.err.println(messageSupplier.get());
                throwableSupplier.get().printStackTrace();
            }
        });

        Path path = (new File(inputSource)).toPath();
        sourceRoot = new SourceRoot(path);
        sourceRoot.getParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_11);
        sourceRoot.getParserConfiguration().setSymbolResolver(new JavaSymbolSolver(new JavaParserTypeSolver(path)));

        List<ParseResult<CompilationUnit>> results = sourceRoot.tryToParse();
        results.stream().filter(pr -> !pr.isSuccessful()).map(pr -> pr.toString()).
                collect(Collectors.toSet()).forEach(System.err::println);

    }

}
