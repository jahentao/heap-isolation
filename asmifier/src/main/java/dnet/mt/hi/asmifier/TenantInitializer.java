package dnet.mt.hi.asmifier;

public class TenantInitializer implements Runnable {

    private static String tenantId, systemProperties;

    static {
        tenantId = "pass-as-argument-to-code-generator";
        systemProperties = "pass-as-argument-to-code-generator";
    }

    @Override
    public void run() {
    }

}
