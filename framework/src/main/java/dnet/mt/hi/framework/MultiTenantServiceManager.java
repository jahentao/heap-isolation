package dnet.mt.hi.framework;

import dnet.mt.hi.framework.cl.MultiTenantBootstrapClassLoader;
import dnet.mt.hi.framework.cl.TenantClassLoader;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AllPermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MultiTenantServiceManager {

    private Map<String, TenantClassLoader> classLoaders = new ConcurrentHashMap<>();

    public MultiTenantServiceManager(URI initClassesFile, URI... sharedJars) {
        try {
            Set<String> initClassNames = new HashSet<>();
            Files.lines(Path.of(initClassesFile)).forEach(initClassNames::add);
            Path[] sharedJarPaths = Arrays.stream(sharedJars).map(Path::of).toArray(Path[]::new);
            MultiTenantBootstrapClassLoader.init(sharedJarPaths, (new AllPermission()).newPermissionCollection());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
