package dnet.mt.hi.analyzer.processors;

import dnet.mt.hi.analyzer.enums.MutabilityStatus;
import sun.misc.Unsafe;

import java.lang.invoke.MethodType;
import java.nio.ByteOrder;
import java.time.*;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DecimalStyle;
import java.util.*;
import java.util.regex.Pattern;

public class MutabilityDetector {

    private static List<Class> KNOWN_IMMUTABLE_CLASSES = Arrays.asList(Class.class, String.class, Integer.class, Long.class,
            Character.class, Short.class, Double.class, Float.class, Byte.class, Boolean.class, ByteOrder.class,
            IsoChronology.class, DateTimeFormatter.class, DecimalStyle.class, Instant.class, LocalDate.class, LocalDateTime.class,
            OffsetDateTime.class, OffsetTime.class, Period.class, ZoneOffset.class, Locale.class, MethodType.class, Unsafe.class,
            StackWalker.class, Pattern.class, Runtime.Version.class, Optional.class, OptionalDouble.class, OptionalInt.class,
            OptionalLong.class, Character.UnicodeBlock.class, Character.UnicodeScript.class);

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
