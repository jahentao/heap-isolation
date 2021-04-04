package dnet.mt.hi.analyzer.enums;

public enum FieldAccess {

    PRIVATE("private"), PROTECTED("protected"), PACKAGE("package"), PUBLIC("public");

    public final String value;

    FieldAccess(String value) {
        this.value = value;
    }

}
