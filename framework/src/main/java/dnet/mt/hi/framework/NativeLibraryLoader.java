package dnet.mt.hi.framework;

import dnet.mt.hi.framework.cl.TenantSpecificBootstrapClassLoader;

/**
 * Native libraries should be loaded by class which itself is loaded by the system class loader, a.k.a. application
 * class loader. Otherwise, the {@link TenantSpecificBootstrapClassLoader} wont be able to retrieve
 * a point to the native libraries. The multi-tenant service provider should implement this interface and pass an
 * instance of it to the {@link TenantSpecificBootstrapClassLoader}.
 */
public interface NativeLibraryLoader {

    public abstract void load();

}
