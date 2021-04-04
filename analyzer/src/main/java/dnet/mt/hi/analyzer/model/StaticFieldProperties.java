package dnet.mt.hi.analyzer.model;

import dnet.mt.hi.analyzer.enums.FieldAccess;
import dnet.mt.hi.analyzer.enums.MutabilityStatus;

public class StaticFieldProperties {

    public String owner;
    public String name;
    public String type;
    public boolean isFinal;
    public boolean isArray;
    public FieldAccess access;
    public MutabilityStatus mutabilityStatus;

    @Override
    public String toString() {
        return String.format("%s;%s;%s;%b;%s;%b;%s\n", owner, name, type, isFinal, mutabilityStatus.value, isArray, access.value);
    }

}
