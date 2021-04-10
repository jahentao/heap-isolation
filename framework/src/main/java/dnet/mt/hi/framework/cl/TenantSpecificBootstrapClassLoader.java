package dnet.mt.hi.framework.cl;

import dnet.mt.hi.framework.MultiTenantPolicy;
import sun.security.util.SecurityConstants;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class TenantSpecificBootstrapClassLoader extends AbstractMTClassLoader {

    private static PermissionCollection ALL_PERMISSION_COLLECTION = SecurityConstants.ALL_PERMISSION.newPermissionCollection();

    private static Map<String, Class> sharedClasses = new ConcurrentHashMap<>();
    private static Map<CodeSource, FileSystem> trustedCode = new ConcurrentHashMap<>();

    private Map<CodeSource, ProtectionDomain> pds = new ConcurrentHashMap<>();
    private Map<String, Class> loadedClasses = new ConcurrentHashMap<>();


    public static void init(Set<String> sharedClassNames, Path[] sharedJarPaths) {

        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkCreateClassLoader();
        }

        if (sharedClasses.isEmpty() && trustedCode.isEmpty()) {
            loadSharedClasses(sharedClassNames);
            initTrustedCode(sharedJarPaths);
            ALL_PERMISSION_COLLECTION.add(SecurityConstants.ALL_PERMISSION);
        }

    }

    private static void loadSharedClasses(Set<String> sharedClassNames) {
        sharedClassNames.forEach(name -> {
            try {
                sharedClasses.put(name, getSystemClassLoader().loadClass(name));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    private static void initTrustedCode(Path[] sharedJarPaths) {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        try {
            for (Path jarPath : sharedJarPaths) {
                CodeSource cs = new CodeSource(jarPath.toUri().toURL(), (CodeSigner[]) null);
                MultiTenantPolicy.getInstance().registerTrustedCode(cs, ALL_PERMISSION_COLLECTION);
                trustedCode.put(cs, FileSystems.newFileSystem(URI.create(String.format("jar:%s", jarPath.toUri().toString())), env));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TenantSpecificBootstrapClassLoader(String tenantId, ClassLoader parent, Principal[] principals) {

        super(tenantId, tenantId.concat("_BootstrapClassLoader"), parent);

        if (trustedCode.isEmpty()) {
            throw new IllegalStateException("The init method has not been called properly yet.");
        }

        trustedCode.forEach((cs, fs) -> {
            pds.put(cs, new ProtectionDomain(cs, ALL_PERMISSION_COLLECTION, this, principals));
        });

        loadedClasses.putAll(sharedClasses);

        Module unnamedModule = getUnnamedModule();
        Module javaBase = ClassLoader.class.getModule();
        Set<String> sharedPackages = getSharedPackages();
        sharedPackages.forEach(pkg -> {
            if (!javaBase.isOpen(pkg)) {
                javaBase.addOpens(pkg, unnamedModule);
            }
        });

    }

    private Set<String> getSharedPackages() {
        Set<String> result = new HashSet<>();
        sharedClasses.forEach((k, v) -> result.add(v.getPackageName()));
        return result;
    }


    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            if (name != null) {
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
        throw new ClassNotFoundException(String.format("Couldn't find class file for %s.", name));
    }

    @Override
    protected Class<?> findClass(String name) {
        for (CodeSource cs : trustedCode.keySet()) {
            Class result = findClass(name, trustedCode.get(cs), pds.get(cs));
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
        trustedCode.values().forEach(fs -> {
            try {
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        trustedCode.clear();
    }

}