package dnet.mt.hi.eval.performance.renaissance.plugin;

import org.renaissance.Plugin;

import java.security.*;

public class SecurityManagerEnabler implements Plugin {

    public SecurityManagerEnabler() {
        Policy.setPolicy(new Policy() {
            @Override
            public boolean implies(ProtectionDomain domain, Permission permission) {
                return true;
            }
        });
        System.setSecurityManager(new SecurityManager());
    }

}
