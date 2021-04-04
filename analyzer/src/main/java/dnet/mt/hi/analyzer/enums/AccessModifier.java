package dnet.mt.hi.analyzer.enums;

public enum AccessModifier {

    PRIVATE("private", 0), PROTECTED("protected", 2), PACKAGE("package", 1), PUBLIC("public", 3);

    public final String value;
    public final int order;

    AccessModifier(String value, int order) {
        this.value = value;
        this.order = order;
    }

}
