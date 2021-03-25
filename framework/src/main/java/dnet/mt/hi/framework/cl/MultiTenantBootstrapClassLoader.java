package dnet.mt.hi.framework.cl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class MultiTenantBootstrapClassLoader extends FileSystemClassLoader {

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

            systemPermissions = permissions;

        }

    }

    public MultiTenantBootstrapClassLoader(String name, ClassLoader parent, Principal[] principals) {
        super(name, parent);

        if (trustedCodeFileSystems.isEmpty() || systemPermissions == null) {
            throw new IllegalStateException("The init method has not been called properly yet.");
        }

        CodeSource cs = new CodeSource(null, (CodeSigner[]) null);
        pd = new ProtectionDomain(cs, systemPermissions, this, principals);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) {
        synchronized (getClassLoadingLock(name)) {
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

    @Override
    protected Class<?> findClass(String name) {
        Class result = null;
        for (FileSystem fs : trustedCodeFileSystems) {
            result = findClass(name, fs, pd);
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