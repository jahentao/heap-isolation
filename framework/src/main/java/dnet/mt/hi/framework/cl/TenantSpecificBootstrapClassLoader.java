package dnet.mt.hi.framework.cl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TenantSpecificBootstrapClassLoader extends AbstractMTClassLoader {

    private static final List<FileSystem> trustedCodeFileSystems = new LinkedList<>();
    private static PermissionCollection systemPermissions;

    private ProtectionDomain pd;
    private Map<String, Class> loadedClasses = new ConcurrentHashMap<>();

    public static void init(Path[] sharedJarPaths, PermissionCollection permissions) {

        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkCreateClassLoader();
        }

        if (trustedCodeFileSystems.isEmpty() && systemPermissions == null) {
            createTrustedCodeFileSystem(sharedJarPaths);
            systemPermissions = permissions;
        }

    }

    private static void createTrustedCodeFileSystem(Path[] sharedJarPaths) {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        try {
            for (Path jarPath : sharedJarPaths) {
                trustedCodeFileSystems.add(FileSystems.
                        newFileSystem(URI.create(String.format("jar:%s", jarPath.toUri().toString())), env));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TenantSpecificBootstrapClassLoader(String tenantId, ClassLoader parent, Principal[] principals) {
        super(tenantId.concat("_BootstrapClassLoader"), parent);

        if (trustedCodeFileSystems.isEmpty() || systemPermissions == null) {
            throw new IllegalStateException("The init method has not been called properly yet.");
        }

        this.tenantId = tenantId;
        CodeSource cs = new CodeSource(null, (CodeSigner[]) null);
        pd = new ProtectionDomain(cs, systemPermissions, this, principals);
    }


    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            if (name != null) {
                if (name.startsWith("java.lang.")) {
                    return super.loadClass(name, resolve);
                } else {
                    Class<?> c = loadedClasses.get(name);
                    if (c == null) {
                        c = findClass(name);
                        if (c != null) {
                            loadedClasses.put(name, c);
                        }
                    }
                    if (c != null && resolve) {
                        resolveClass(c);
                    }
                    return c;
                }
            }
        }
        throw new ClassNotFoundException(String.format("Couldn't find class file for %s.", name));
    }

    @Override
    protected Class<?> findClass(String name) {
        for (FileSystem fs : trustedCodeFileSystems) {
            Class result = findClass(name, fs, pd);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public static void tearDown() {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkCreateClassLoader();
        }
        trustedCodeFileSystems.forEach(fs -> {
            try {
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        trustedCodeFileSystems.clear();
        systemPermissions = null;
    }

}