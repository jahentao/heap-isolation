package dnet.mt.hi.analyzer.enums;

public enum MutabilityStatus {

    MUTABLE("mutable"), IMMUTABLE("immutable"), UNKNOWN("unknown");

    public final String value;

    MutabilityStatus(String value) {
        this.value = value;
    }

}
