package dnet.mt.hi.framework;

import dnet.mt.hi.framework.cl.TenantClassLoader;
import dnet.mt.hi.framework.cl.TenantSpecificBootstrapClassLoader;
import sun.security.util.SecurityConstants;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MultiTenantServiceManager {

    private Map<String, TenantClassLoader> classLoaders = new ConcurrentHashMap<>();
    private Map<String, Path> tenantFolders = new ConcurrentHashMap<>();

    private static MultiTenantServiceManager INSTANCE;

    private PrintStream systemOut, systemErr;

    public static MultiTenantServiceManager getInstance(CodeSource applicationCodeSource, Set<String> sharedClasses, URI... sharedJars) {
        if (INSTANCE == null) {
            INSTANCE = new MultiTenantServiceManager(applicationCodeSource, sharedClasses, sharedJars);
        }
        return INSTANCE;
    }

    private MultiTenantServiceManager(CodeSource applicationCodeSource, Set<String> sharedClasses, URI... sharedJars) {

        Path[] sharedJarPaths = Arrays.stream(sharedJars).map(Path::of).toArray(Path[]::new);
        TenantSpecificBootstrapClassLoader.init(sharedClasses, sharedJarPaths);

        systemOut = System.out;
        MultiTenantPrintStream out = new MultiTenantPrintStream(System.out);
        System.setOut(out);
        systemErr = System.err;
        MultiTenantPrintStream err = new MultiTenantPrintStream(System.err);
        System.setErr(err);
        System.setIn(null);

        MultiTenantPolicy policy = MultiTenantPolicy.getInstance();
        PermissionCollection pc = SecurityConstants.ALL_PERMISSION.newPermissionCollection();
        pc.add(SecurityConstants.ALL_PERMISSION);
        policy.registerTrustedCode(applicationCodeSource, pc);
        Policy.setPolicy(policy);
        System.setSecurityManager(new SecurityManager());

    }

    public void registerTenant(String tenantId, URI tenantJar) throws IOException {
        if (!classLoaders.containsKey(tenantId)) {

            Path tenantFolder = Files.createDirectories(Path.of(System.getProperty("user.dir"), tenantId));
            tenantFolders.put(tenantId, tenantFolder);

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

    public void stop() throws IOException {
        MultiTenantPrintStream out = (MultiTenantPrintStream) System.out;
        MultiTenantPrintStream err = (MultiTenantPrintStream) System.err;
        for (String tenantId : classLoaders.keySet()) {
            classLoaders.get(tenantId).close();
            out.unregisterTenant(tenantId);
            err.unregisterTenant(tenantId);
            /*Files.walk(tenantFolders.get(tenantId))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);*/
            MultiTenantPolicy.getInstance().unregisterTenant(tenantId);
        }
        System.setOut(systemOut);
        System.setErr(systemErr);
    }

    TenantClassLoader getTenantClassLoader(String tenantId) {
        return classLoaders.get(tenantId);
    }

}
