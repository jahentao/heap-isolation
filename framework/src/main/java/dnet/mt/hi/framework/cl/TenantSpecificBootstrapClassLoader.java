package dnet.mt.hi.framework.cl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class TenantSpecificBootstrapClassLoader extends AbstractMTClassLoader {

    private static Set<Class> systemClasses = new HashSet<>();
    private static final List<FileSystem> trustedCodeFileSystems = new LinkedList<>();
    private static PermissionCollection systemPermissions;

    private ProtectionDomain pd;
    private Map<String, Class> loadedClasses = new ConcurrentHashMap<>();

    public static void init(Set<String> sharedClassNames, Path[] sharedJarPaths, PermissionCollection permissions) {

        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkCreateClassLoader();
        }

        if (systemClasses.isEmpty() && trustedCodeFileSystems.isEmpty() && systemPermissions == null) {
            loadSystemClasses(sharedClassNames);
            createTrustedCodeFileSystem(sharedJarPaths);
            systemPermissions = permissions;
        }

    }

    private static void loadSystemClasses(Set<String> sharedClassNames) {
        sharedClassNames.forEach(name -> {
            try {
                systemClasses.add(Class.forName(name));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
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

        Module unnamedModule = getUnnamedModule();
        Module javaBase = ClassLoader.class.getModule();
        Set<String> sharedPackages = getSharedPackages();
        sharedPackages.forEach(pkg -> {
            if (!javaBase.isExported(pkg)) {
                javaBase.addExports(pkg, unnamedModule);
            }
        });

    }

    private Set<String> getSharedPackages() {
        Set<String> result = new HashSet<>();
        systemClasses.forEach(clazz -> result.add(clazz.getPackageName()));
        return result;
    }


    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            if (name != null) {
                Class<?> c = loadedClasses.get(name);
                if (c == null) {
                    if (systemClasses.contains(name)) {
                        c = super.loadClass(name, resolve);
                    }
                    if (c == null) {
                        c = findClass(name);
                    }
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