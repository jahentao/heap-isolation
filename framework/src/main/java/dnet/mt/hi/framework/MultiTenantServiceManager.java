package dnet.mt.hi.framework;

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
            TenantClassLoader.init(initClassNames, sharedJarPaths, (new AllPermission()).newPermissionCollection());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerTenant(String tenantId, URI tenantJar) {
        classLoaders.putIfAbsent(tenantId,
                new TenantClassLoader(tenantId.concat("ClassLoader"),
                        this.getClass().getClassLoader(),
                        Path.of(tenantJar), null, null));
    }

    public TenantClassLoader getTenantClassLoader(String tenantId) {
        return classLoaders.get(tenantId);
    }

}
