package dnet.mt.hi.analyzer.processors;

import dnet.mt.hi.analyzer.enums.MutabilityStatus;

import java.util.Arrays;
import java.util.List;

public class MutabilityDetector {

    private static List<Class> KNOWN_IMMUTABLE_CLASSES = Arrays.asList(String.class, Integer.class, Long.class,
            Character.class, Short.class, Double.class, Float.class, Byte.class);

    public MutabilityStatus detect(Class clazz) {
        if (clazz.isPrimitive()) {
            return MutabilityStatus.IMMUTABLE;
        }
        if (clazz.isArray()) {
            return MutabilityStatus.MUTABLE;
        }
        if (KNOWN_IMMUTABLE_CLASSES.contains(clazz)) {
            return MutabilityStatus.IMMUTABLE;
        }
        return MutabilityStatus.UNKNOWN;
    }

}
