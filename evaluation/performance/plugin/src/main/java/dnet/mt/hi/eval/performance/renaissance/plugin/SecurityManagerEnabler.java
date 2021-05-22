package dnet.mt.hi.eval.performance.renaissance.plugin;

import org.renaissance.Plugin;

import java.security.*;

public class SecurityManagerEnabler implements Plugin {

    public SecurityManagerEnabler() {
        Policy.setPolicy(new Policy() {

            private PermissionCollection ALL_PERMISSION_COLLECTION = new AllPermission().newPermissionCollection();

            @Override
            public PermissionCollection getPermissions(CodeSource cs) {
                return ALL_PERMISSION_COLLECTION;
            }

            @Override
            public PermissionCollection getPermissions(ProtectionDomain domain) {
                return getPermissions(domain.getCodeSource());
            }
            
        });
        System.setSecurityManager(new SecurityManager());
    }

}
