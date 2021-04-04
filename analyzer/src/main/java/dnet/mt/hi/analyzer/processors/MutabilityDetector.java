package dnet.mt.hi.analyzer.processors;

import dnet.mt.hi.analyzer.enums.MutabilityStatus;

import java.util.Arrays;
import java.util.List;

public class MutabilityDetector {

    private static List<Class> KNOWN_IMMUTABLE_CLASSES = Arrays.asList(Class.class, String.class, Integer.class, Long.class,
            Character.class, Short.class, Double.class, Float.class, Byte.class, Character.UnicodeBlock.class,
            Character.UnicodeScript.class);

    public MutabilityStatus detect(Class clazz) {
        if (clazz.isPrimitive() || clazz.isEnum()) {
            return MutabilityStatus.IMMUTABLE;
        }
        if (KNOWN_IMMUTABLE_CLASSES.contains(clazz)) {
            return MutabilityStatus.IMMUTABLE;
        }
        if (clazz.isArray()) {
            return detect(clazz.getComponentType());
        }
        return MutabilityStatus.UNKNOWN;
    }

}
