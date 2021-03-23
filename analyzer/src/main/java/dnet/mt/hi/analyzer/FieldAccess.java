package dnet.mt.hi.analyzer;

enum FieldAccess {

    PRIVATE("private"), PROTECTED("protected"), PACKAGE("package"), PUBLIC("public");

    String value;

    FieldAccess(String value) {
        this.value = value;
    }

}
