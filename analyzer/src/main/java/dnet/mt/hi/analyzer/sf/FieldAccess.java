package dnet.mt.hi.analyzer.sf;

enum FieldAccess {

    PRIVATE("private"), PROTECTED("protected"), PACKAGE("package"), PUBLIC("public");

    String value;

    FieldAccess(String value) {
        this.value = value;
    }

}
