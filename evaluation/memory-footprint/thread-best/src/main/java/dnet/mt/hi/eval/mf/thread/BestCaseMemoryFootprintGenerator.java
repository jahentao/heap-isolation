package dnet.mt.hi.eval.mf.thread;

import dnet.mt.hi.framework.MultiTenantServiceManager;
import dnet.mt.hi.shared.SharedClassUtil;

import javax.xml.validation.Validator;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

public class BestCaseMemoryFootprintGenerator {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Specify the number of tenants (valid options: from 1 to 5)");
        }

        Integer tenants = Integer.parseInt(args[0]);
        if (tenants < 1 || tenants > 5) {
            throw new IllegalArgumentException("Valid options: from 1 to 5.");
        }

        MultiTenantServiceManager multiTenantServiceManager = MultiTenantServiceManager.getInstance(
                Validator.class.getProtectionDomain().getCodeSource(),
                SharedClassUtil.loadSharedClassNames("shared_classes.list"),
                buildURI("java.base.jar"));

        for (int i = 1; i < tenants + 1; i++) {
            multiTenantServiceManager.registerTenant(String.format("tenant_0%d", i),
                    buildURI(String.format("tenant_0%d.jar", i)));
        }

        System.out.println("I'm ready for docker stats!");
        while (true) ;
    }

    private static URI buildURI(String filePath) {
        return Paths.get(System.getProperty("user.dir"), filePath).toUri();
    }

}
