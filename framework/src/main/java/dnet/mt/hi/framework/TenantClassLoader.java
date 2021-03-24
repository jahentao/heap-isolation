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

    private static final Map<String, Class> INIT_CLASSES = new HashMap<>();
    private static final List<FileSystem> sharedFileSystems = new LinkedList<>();

    private FileSystem tenantFileSystem;
    private Map<String, Class> loadedClasses = new ConcurrentHashMap<>();
    private ProtectionDomain pd;

    public static void init(Set<String> initClassNames, Path[] sharedJarPaths) {

        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new RuntimePermission("createClassLoader"));
        }

        if (INIT_CLASSES.isEmpty() && sharedFileSystems.isEmpty()) {

            initClassNames.forEach(className -> {
                try {
                    INIT_CLASSES.putIfAbsent(className,
                            TenantClassLoader.class.getClassLoader().loadClass(className));
                } catch (ClassNotFoundException e) {
                }
            });

            Map<String, String> env = new HashMap<>();
            env.put("create", "true");

            try {
                for (Path jarPath : sharedJarPaths) {
                    sharedFileSystems.add(FileSystems.
                            newFileSystem(URI.create(String.format("jar:%s", jarPath.toUri().toString())), env));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public TenantClassLoader(String name, ClassLoader parent, Path tenantJarPath, PermissionCollection permissions, Principal[] principals) {

        super(name, parent);

        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new RuntimePermission("createClassLoader"));
        }

        if (INIT_CLASSES.isEmpty() || sharedFileSystems.isEmpty()) {
            throw new IllegalStateException("The init method has not been called properly yet.");
        }

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
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (INIT_CLASSES.containsKey(name)) {
            return INIT_CLASSES.get(name);
        }
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

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class result = null;
        for (FileSystem fs : sharedFileSystems) {
            System.out.println(String.format("%s loading %s...", this.getName(), name));
            result = findClass(name, fs);
            if (result != null) {
                return result;
            }
        }
        if (name != null && name.startsWith("java.")) { // a similar check exists in the original java.lang.ClassLoader
            throw new SecurityException
                    ("Prohibited package name: " +
                            name.substring(0, name.lastIndexOf('.')));
        } else {
            result = findClass(name, tenantFileSystem);
            if (result != null) {
                return result;
            }
        }
        throw new ClassNotFoundException(String.format("Couldn't find %s.", name));
    }

    private Class<?> findClass(String name, FileSystem fs) {
        try {
            InputStream is = Files.newInputStream(fs.getPath(name.replace('.', '/').concat(".class")),
                    StandardOpenOption.READ);
            byte[] bytes = is.readAllBytes();
            /**
             * In case of classes in packages starting with 'java.', the following statement only works on custom JVM's
             * which do not throw SecurityException in the latter case.
             */
            return defineClass(name, bytes, 0, bytes.length, pd);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() throws IOException {

        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new RuntimePermission("closeClassLoader"));
        }

        tenantFileSystem.close();

    }

    public static void tearDown() {

        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new RuntimePermission("closeClassLoader"));
        }

        synchronized (sharedFileSystems) {
            sharedFileSystems.forEach(fs -> {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            sharedFileSystems.clear();
        }

        synchronized (INIT_CLASSES) {
            INIT_CLASSES.clear();
        }

    }


}