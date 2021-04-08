package dnet.mt.hi.framework;

import dnet.mt.hi.framework.cl.TenantClassLoader;
import dnet.mt.hi.framework.cl.TenantSpecificBootstrapClassLoader;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AllPermission;
import java.security.Permissions;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MultiTenantServiceManager {

    private Map<String, TenantClassLoader> classLoaders = new ConcurrentHashMap<>();

    private static MultiTenantServiceManager INSTANCE;

    public static MultiTenantServiceManager getInstance(Set<String> sharedClasses, URI... sharedJars) {
        if (INSTANCE == null) {
            INSTANCE = new MultiTenantServiceManager(sharedClasses, sharedJars);
        }
        return INSTANCE;
    }

    private MultiTenantServiceManager(Set<String> sharedClasses, URI... sharedJars) {

        Path[] sharedJarPaths = Arrays.stream(sharedJars).map(Path::of).toArray(Path[]::new);
        TenantSpecificBootstrapClassLoader.init(sharedClasses, sharedJarPaths, (new AllPermission()).newPermissionCollection());

        MultiTenantPrintStream out = new MultiTenantPrintStream(System.out);
        System.setOut(out);
        MultiTenantPrintStream err = new MultiTenantPrintStream(System.err);
        System.setOut(err);
        System.setIn(null);

        System.setSecurityManager(new SecurityManager());

    }

    public void registerTenant(String tenantId, URI tenantJar) throws IOException {
        if (!classLoaders.containsKey(tenantId)) {

            Path tenantFolder = Files.createDirectory(Path.of(System.getProperty("user.home"), tenantId));

            FilePermission filePermission = new FilePermission(String.format("%s/-", tenantFolder.toString()), "read,write");
            Permissions permissions = new Permissions();
            permissions.add(filePermission);

            TenantSpecificBootstrapClassLoader bootstrapClassLoader = new TenantSpecificBootstrapClassLoader(
                    tenantId, MultiTenantServiceManager.class.getClassLoader(), null);
            TenantClassLoader tenantClassLoader = new TenantClassLoader(tenantId, bootstrapClassLoader,
                    Path.of(tenantJar), permissions, null);
            classLoaders.put(tenantId, tenantClassLoader);


            MultiTenantPrintStream out = (MultiTenantPrintStream) System.out;
            out.registerTenant(tenantId, new PrintStream(new FileOutputStream(
                    new File(tenantFolder.toString(), tenantId.concat(".out")))));

            MultiTenantPrintStream err = (MultiTenantPrintStream) System.err;
            err.registerTenant(tenantId, new PrintStream(new FileOutputStream(
                    new File(tenantFolder.toString(), tenantId.concat(".err")))));

        }
    }

    TenantClassLoader getTenantClassLoader(String tenantId) {
        return classLoaders.get(tenantId);
    }

}
