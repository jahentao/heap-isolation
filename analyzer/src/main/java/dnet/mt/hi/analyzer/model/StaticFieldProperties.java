package dnet.mt.hi.analyzer.model;

import dnet.mt.hi.analyzer.enums.AccessModifier;
import dnet.mt.hi.analyzer.enums.MutabilityStatus;

public class StaticFieldProperties {

    public String owner;
    public String name;
    public String type;
    public boolean isFinal;
    public boolean isArray;
    public AccessModifier access;
    public MutabilityStatus mutabilityStatus;

    @Override
    public String toString() {
        return String.format("%s;%s;%s\n", owner, name, type);
    }

}
