package dnet.mt.hi.asmifier;

public class TenantInitializer implements Runnable {

    private static String tenantHome;

    static {
        tenantHome = "pass-as-argument-to-code-generator";
    }

    @Override
    public void run() {
    }

}
