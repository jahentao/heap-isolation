package dnet.mt.hi.framework.cl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class TenantSpecificBootstrapClassLoader extends AbstractMTClassLoader {

    private static Map<String, Class> sharedClasses = new ConcurrentHashMap<>();
    private static List<FileSystem> trustedCodeFileSystems = new LinkedList<>();

    private Map<String, Class> loadedClasses = new ConcurrentHashMap<>();


    public static void init(Set<String> sharedClassNames, Path[] sharedJarPaths) {

        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkCreateClassLoader();
        }

        if (sharedClasses.isEmpty() && trustedCodeFileSystems.isEmpty()) {
            loadSharedClasses(sharedClassNames);
            initTrustedCode(sharedJarPaths);
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
                trustedCodeFileSystems.add(FileSystems.newFileSystem(URI.create(String.format("jar:%s", jarPath.toUri().toString())), env));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TenantSpecificBootstrapClassLoader(String tenantId, ClassLoader parent, Principal[] principals) {

        super(tenantId, tenantId.concat("_BootstrapClassLoader"), parent);

        if (trustedCodeFileSystems.isEmpty()) {
            throw new IllegalStateException("The init method has not been called properly yet.");
        }

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
        for (FileSystem fs : trustedCodeFileSystems) {
            Class result = findClass(name, fs, TenantSpecificBootstrapClassLoader.class.getProtectionDomain());
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
    }

}