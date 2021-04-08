package dnet.mt.hi.framework;

import sun.security.util.SecurityConstants;

import java.security.Permission;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiTenantPolicy extends Policy {

    private Map<String, ProtectionDomain> pds = new ConcurrentHashMap<>();

    private static MultiTenantPolicy INSTANCE;

    public static MultiTenantPolicy getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MultiTenantPolicy();
        }
        return INSTANCE;
    }

    private MultiTenantPolicy() {}

    public void registerTenant(String tenantId, ProtectionDomain protectionDomain) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(SecurityConstants.GET_PD_PERMISSION);
        }
        pds.putIfAbsent(tenantId, protectionDomain);
    }

    @Override
    public boolean implies(ProtectionDomain domain, Permission permission) {
        if (pds.entrySet().contains(domain)) {
            return super.implies(domain, permission);
        }
        return true;
    }

}
