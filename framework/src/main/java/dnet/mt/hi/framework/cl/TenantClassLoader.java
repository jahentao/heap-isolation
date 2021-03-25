package dnet.mt.hi.framework.cl;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TenantClassLoader extends FileSystemClassLoader implements Closeable {

    private static final RuntimePermission CLOSE_CLASS_LOADER_PERMISSION = new RuntimePermission("closeClassLoader");

    private FileSystem tenantFileSystem;
    private Map<String, Class> loadedClasses = new ConcurrentHashMap<>();
    private ProtectionDomain pd;
    private MultiTenantBootstrapClassLoader parent;

    public TenantClassLoader(String name, ClassLoader parent, Path tenantJarPath, PermissionCollection permissions, Principal[] principals) {

        super(name, parent);

        if (parent instanceof MultiTenantBootstrapClassLoader) {
            this.parent = (MultiTenantBootstrapClassLoader) parent;
        } else {
            throw new IllegalArgumentException(String.format("Parent should be an instance of %s.",
                    MultiTenantBootstrapClassLoader.class.getName()));
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
        synchronized (getClassLoadingLock(name)) {
            Class<?> c = parent.loadClass(name, resolve);
            if (c == null) {
                c = loadedClasses.get(name);
                if (c == null) {
                    c = findClass(name);
                    if (c != null) {
                        loadedClasses.put(name, c);
                    }
                }
            }
            if (c!= null) {
                if (resolve) {
                    resolveClass(c);
                }
                return c;
            }
        }
        throw new ClassNotFoundException(String.format("Couldn't find %s.", name));
    }

    @Override
    protected final Class<?> findClass(String name) {
        if (name != null && name.startsWith("java.")) { // a similar check exists in the original java.lang.ClassLoader
            throw new SecurityException
                    ("Prohibited package name: " +
                            name.substring(0, name.lastIndexOf('.')));
        } else {
            Class result = findClass(name, tenantFileSystem, pd);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(CLOSE_CLASS_LOADER_PERMISSION);
        }
        tenantFileSystem.close();
    }

}
