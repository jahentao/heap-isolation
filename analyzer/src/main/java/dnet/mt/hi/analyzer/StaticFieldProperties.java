package dnet.mt.hi.analyzer;

class StaticFieldProperties {

    String owner;
    String name;
    String type;
    boolean isFinal;
    boolean isArray;
    FieldAccess access;

    @Override
    public String toString() {
        return String.format("%s;%s;%s;%b;%b;%s\n", owner, name, type, isFinal, isArray, access.value);
    }
}
