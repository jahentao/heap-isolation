package dnet.mt.hi.framework.cl;

import dnet.mt.hi.framework.NativeLibraryLoader;

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

public final class TenantSpecificBootstrapClassLoader extends FileSystemClassLoader {

    private static final String NATIVE_LIBRARIES_FIELD_NAME = "nativeLibraries";
    private static final List<FileSystem> trustedCodeFileSystems = new LinkedList<>();
    private static final Map<String, Class> bypass = new HashMap<>();
    private static PermissionCollection systemPermissions;

    private ProtectionDomain pd;
    private Map<String, Class> loadedClasses = new ConcurrentHashMap<>();

    public static void init(Path[] sharedJarPaths, NativeLibraryLoader nativeLibraryLoader,
                            PermissionCollection permissions, String... bypassClassNames) {

        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkCreateClassLoader();
        }

        if (trustedCodeFileSystems.isEmpty() && bypass.isEmpty() && systemPermissions == null) {
            createTrustedCodeFileSystem(sharedJarPaths);
            //nativeLibraryLoader.load();
            systemPermissions = permissions;
            try {
                for (String className : bypassClassNames) {
                    bypass.put(className, Class.forName(className));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
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

    public TenantSpecificBootstrapClassLoader(String name, ClassLoader parent, Principal[] principals) {
        super(name, parent);

        if (trustedCodeFileSystems.isEmpty() || systemPermissions == null) {
            throw new IllegalStateException("The init method has not been called properly yet.");
        }

        CodeSource cs = new CodeSource(null, (CodeSigner[]) null);
        pd = new ProtectionDomain(cs, systemPermissions, this, principals);

        /*try {
            Field field = ClassLoader.class.getDeclaredField(NATIVE_LIBRARIES_FIELD_NAME);
            field.setAccessible(true); // This implies that the framework should be loaded as a patch to java.base
            field.set(this, field.get(ClassLoader.getSystemClassLoader()));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }*/
    }


    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            if (name != null) {
                if (name.startsWith("java.lang.") /*|| name.startsWith("jdk.internal.") ||
                        name.startsWith("com.sun.") || name.startsWith("sun.")*/) {
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
        bypass.clear();
        systemPermissions = null;
    }

}