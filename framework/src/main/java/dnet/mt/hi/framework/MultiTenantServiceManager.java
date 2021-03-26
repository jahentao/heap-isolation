package dnet.mt.hi.framework;

import dnet.mt.hi.framework.cl.MultiTenantBootstrapClassLoader;
import dnet.mt.hi.framework.cl.TenantClassLoader;

import java.net.URI;
import java.nio.file.Path;
import java.security.AllPermission;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiTenantServiceManager {

    private Map<String, TenantClassLoader> classLoaders = new ConcurrentHashMap<>();

    public MultiTenantServiceManager(NativeLibraryLoader nativeLibraryLoader, URI... sharedJars) {
        Path[] sharedJarPaths = Arrays.stream(sharedJars).map(Path::of).toArray(Path[]::new);
        MultiTenantBootstrapClassLoader.init(sharedJarPaths, nativeLibraryLoader, (new AllPermission()).newPermissionCollection());
    }

    public void registerTenant(String tenantId, URI tenantJar) {
        if (!classLoaders.containsKey(tenantId)) {
            MultiTenantBootstrapClassLoader bootstrapClassLoader = new MultiTenantBootstrapClassLoader(
                    tenantId.concat("_BootstrapLoader"),
                    MultiTenantServiceManager.class.getClassLoader(), null);
            TenantClassLoader tenantClassLoader = new TenantClassLoader(tenantId.concat("_ClassLoader"),
                    bootstrapClassLoader, Path.of(tenantJar), null, null); // TODO fix permissions
            classLoaders.put(tenantId, tenantClassLoader);
        }
    }

    public TenantClassLoader getTenantClassLoader(String tenantId) {
        return classLoaders.get(tenantId);
    }

}
