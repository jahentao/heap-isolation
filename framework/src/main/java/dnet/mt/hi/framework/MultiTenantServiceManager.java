package dnet.mt.hi.framework;

import dnet.mt.hi.framework.cl.TenantSpecificBootstrapClassLoader;
import dnet.mt.hi.framework.cl.TenantClassLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.file.Path;
import java.security.AllPermission;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiTenantServiceManager {

    public static final String TENANT_INITIALIZER_CLASS_NAME = "dnet.mt.hi.init.TenantInitializer";
    private Map<String, TenantClassLoader> classLoaders = new ConcurrentHashMap<>();

    public MultiTenantServiceManager(URI... sharedJars) {
        Path[] sharedJarPaths = Arrays.stream(sharedJars).map(Path::of).toArray(Path[]::new);
        TenantSpecificBootstrapClassLoader.init(sharedJarPaths, (new AllPermission()).newPermissionCollection());
    }

    public void registerTenant(String tenantId, URI tenantJar) {
        if (!classLoaders.containsKey(tenantId)) {
            TenantSpecificBootstrapClassLoader bootstrapClassLoader = new TenantSpecificBootstrapClassLoader(
                    tenantId.concat("_BootstrapLoader"),
                    MultiTenantServiceManager.class.getClassLoader(), null);
            TenantClassLoader tenantClassLoader = new TenantClassLoader(tenantId.concat("_ClassLoader"),
                    bootstrapClassLoader, Path.of(tenantJar), null, null); // TODO fix permissions
            classLoaders.put(tenantId, tenantClassLoader);
            try {
                Class clazz = bootstrapClassLoader.loadClass(TENANT_INITIALIZER_CLASS_NAME);
                Constructor constructor = clazz.getConstructor();
                Runnable initializer = (Runnable) constructor.newInstance();
                initializer.run();
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public TenantClassLoader getTenantClassLoader(String tenantId) {
        return classLoaders.get(tenantId);
    }

}
