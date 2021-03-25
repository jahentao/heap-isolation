package dnet.mt.hi.framework;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

final class TenantClassLoader extends ClassLoader implements Closeable {

    public static final RuntimePermission CLOSE_CLASS_LOADER_PERMISSION = new RuntimePermission("closeClassLoader");

    private TrustedCodeLoader trustedCodeLoader;
    private UntrustedCodeLoader untrustedCodeLoader;

    public static void init(Set<String> initClassNames, Path[] sharedJarPaths, PermissionCollection permissions) {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkCreateClassLoader();
        }
        TrustedCodeLoader.init(initClassNames, sharedJarPaths, permissions);
    }


    public TenantClassLoader(String name, ClassLoader parent, Path tenantJarPath, PermissionCollection permissions, Principal[] principals) {
        super(name, parent);

        trustedCodeLoader = new TrustedCodeLoader(name.concat("_trusted"), this, principals);
        untrustedCodeLoader = new UntrustedCodeLoader(name.concat("_untrusted"), this, tenantJarPath, permissions, principals);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> result = trustedCodeLoader.loadClass(name, resolve);
        if (result == null) {
            result = untrustedCodeLoader.loadClass(name, resolve);
        }
        if (result != null) {
            return result;
        }
        throw new ClassNotFoundException(String.format("Couldn't find %s.", name));
    }

    @Override
    public void close() throws IOException {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(CLOSE_CLASS_LOADER_PERMISSION);
        }
        untrustedCodeLoader.close();
    }

    public static void tearDown() {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkCreateClassLoader();
        }
        TrustedCodeLoader.tearDown();
    }

    private static abstract class AbstractFileSystemCodeLoader extends ClassLoader implements Closeable {

        Map<String, Class> loadedClasses = new ConcurrentHashMap<>();

        private AbstractFileSystemCodeLoader(String name, ClassLoader parent) {
            super(name, parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        /*if (INIT_CLASSES.containsKey(name)) {
            return INIT_CLASSES.get(name);
        }*/
        /*if (INIT_CLASSES.containsKey(name) && name.startsWith("java.")) {
            return INIT_CLASSES.get(name);
        }*/
            synchronized (getClassLoadingLock(name)) {
                Class<?> c = loadedClasses.get(name);
                if (c == null) {
                    c = findClass(name);
                    loadedClasses.put(name, c);
                }
                if (resolve) {
                    resolveClass(c);
                }
                return c;
            }
        }

        Class<?> findClass(String name, FileSystem fs, ProtectionDomain pd) {
            try {
                InputStream is = Files.newInputStream(fs.getPath(name.replace('.', '/').concat(".class")),
                        StandardOpenOption.READ);
                byte[] bytes = is.readAllBytes();
                /**
                 * In case of classes in packages starting with 'java.', the following statement only works on custom JVM's
                 * which do not throw SecurityException in the latter case. See the patch folder in this project.
                 */
                return defineClass(name, bytes, 0, bytes.length, pd);
            } catch (IOException e) {
                //e.printStackTrace();
            }
            return null;
        }

        @Override
        public void close() throws IOException {
            loadedClasses.clear();
        }

    }

    private static class TrustedCodeLoader extends AbstractFileSystemCodeLoader {

        private static final Map<String, Class> INIT_CLASSES = new HashMap<>();
        private static final List<FileSystem> trustedCodeFileSystems = new LinkedList<>();
        private static PermissionCollection permissions;

        private ProtectionDomain pd;

        private static void init(Set<String> initClassNames, Path[] sharedJarPaths, PermissionCollection systemPermissions) {
            if (INIT_CLASSES.isEmpty() && trustedCodeFileSystems.isEmpty() && permissions == null) {

                initClassNames.forEach(className -> {
                    try {
                        INIT_CLASSES.putIfAbsent(className,
                                TenantClassLoader.class.getClassLoader().loadClass(className));
                    } catch (ClassNotFoundException ignored) {
                    }
                });

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

                permissions = systemPermissions;

            }

        }

        private TrustedCodeLoader(String name, ClassLoader parent, Principal[] principals) {

            super(name, parent);

            if (INIT_CLASSES.isEmpty() || trustedCodeFileSystems.isEmpty() || permissions == null) {
                throw new IllegalStateException("The init method has not been called properly yet.");
            }

            CodeSource cs = new CodeSource(null, (CodeSigner[]) null);
            pd = new ProtectionDomain(cs, permissions, this, principals);

            Map<String, String> env = new HashMap<>();
            env.put("create", "true");

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

        private static void tearDown() {
            trustedCodeFileSystems.forEach(fs -> {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            trustedCodeFileSystems.clear();
            INIT_CLASSES.clear();
            permissions = null;
        }

    }

    private static class UntrustedCodeLoader extends AbstractFileSystemCodeLoader implements Closeable {

        private FileSystem tenantFileSystem;
        private ProtectionDomain pd;

        private UntrustedCodeLoader(String name, ClassLoader parent, Path tenantJarPath, PermissionCollection permissions, Principal[] principals) {

            super(name, parent);

            try {
                CodeSource cs = new CodeSource(tenantJarPath.toUri().toURL(), (CodeSigner[]) null);
                pd = new ProtectionDomain(cs, permissions, this, principals);

                Map<String, String> env = new HashMap<>();
                env.put("create", "true");

                tenantFileSystem = FileSystems.
                        newFileSystem(URI.create(String.format("jar:%s", tenantJarPath.toUri().toString())), env);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Class<?> findClass(String name) {
            Class result = null;
            if (name != null && name.startsWith("java.")) { // a similar check exists in the original java.lang.ClassLoader
                throw new SecurityException
                        ("Prohibited package name: " +
                                name.substring(0, name.lastIndexOf('.')));
            } else {
                result = findClass(name, tenantFileSystem, pd);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }

        @Override
        public void close() throws IOException {
            tenantFileSystem.close();
            super.close();
        }

    }

}