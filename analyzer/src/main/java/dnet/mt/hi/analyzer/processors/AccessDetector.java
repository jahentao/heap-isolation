package dnet.mt.hi.analyzer.processors;

import dnet.mt.hi.analyzer.enums.AccessModifier;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class AccessDetector {

    public AccessModifier detect(Field field) {
        AccessModifier fieldModifier = map(field.getModifiers());
        AccessModifier ownerModifier = map(field.getDeclaringClass().getModifiers());
        return ownerModifier.order <= fieldModifier.order ? ownerModifier : fieldModifier;
    }

    private AccessModifier map(int modifier) {
        return Modifier.isPrivate(modifier) ? AccessModifier.PRIVATE :
                Modifier.isProtected(modifier) ? AccessModifier.PROTECTED :
                        Modifier.isPublic(modifier) ? AccessModifier.PUBLIC : AccessModifier.PACKAGE;
    }

}
