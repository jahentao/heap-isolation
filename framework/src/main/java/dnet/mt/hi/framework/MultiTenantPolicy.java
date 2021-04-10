package dnet.mt.hi.framework;

import sun.security.util.SecurityConstants;

import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiTenantPolicy extends Policy {

    private Map<String, CodeSource> css = new ConcurrentHashMap<>();
    private Map<CodeSource, PermissionCollection> pcs = new ConcurrentHashMap<>();

    private static MultiTenantPolicy INSTANCE;

    public static synchronized MultiTenantPolicy getInstance() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(SecurityConstants.GET_POLICY_PERMISSION);
        }
        if (INSTANCE == null) {
            INSTANCE = new MultiTenantPolicy();
        }
        return INSTANCE;
    }

    private MultiTenantPolicy() {}

    @Override
    public PermissionCollection getPermissions(CodeSource cs) {
        return pcs.get(cs);
    }

    @Override
    public PermissionCollection getPermissions(ProtectionDomain domain) {
        return getPermissions(domain.getCodeSource());
    }

    public void registerTrustedCode(CodeSource cs, PermissionCollection pc) {
        pcs.putIfAbsent(cs, pc);
    }

    public void registerTenant(String tenantId, CodeSource cs, PermissionCollection pc) {
        css.putIfAbsent(tenantId, cs);
        pcs.putIfAbsent(cs, pc);
    }

    public void unregisterTenant(String tenantId) {
        CodeSource cs = css.remove(tenantId);
        pcs.remove(cs);
    }

}
