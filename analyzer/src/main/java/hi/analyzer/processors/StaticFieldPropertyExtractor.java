package dnet.mt.hi.analyzer.processors;

import dnet.mt.hi.analyzer.model.StaticFieldProperties;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class StaticFieldPropertyExtractor {

    public static final Set<StaticFieldProperties> properties = new HashSet<>();

    private AccessDetector accessDetector = new AccessDetector();
    private MutabilityDetector mutabilityDetector = new MutabilityDetector();

    public void extract() {
        SharedTypeListExpander.sharedTypes.forEach(c -> processFields(c));
    }

    private void processFields(Class c) {

        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            int modifier = field.getModifiers();
            if (Modifier.isStatic(modifier)) {

                StaticFieldProperties sfp = new StaticFieldProperties();
                sfp.access = accessDetector.detect(field);
                sfp.name = field.getName();
                sfp.owner = Initializer.classToNameMap.get(c);
                sfp.type = field.getGenericType().getTypeName();
                sfp.isArray = field.getType().isArray();
                sfp.isFinal = Modifier.isFinal(modifier);
                sfp.mutabilityStatus = mutabilityDetector.detect(field.getType());

                properties.add(sfp);

            }
        }

    }

}
