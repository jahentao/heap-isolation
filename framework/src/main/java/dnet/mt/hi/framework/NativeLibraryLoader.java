package dnet.mt.hi.framework;

/**
 * Native libraries should be loaded by class which itself is loader by the system class loader, a.k.a. application
 * class loader. Otherwise, the {@link dnet.mt.hi.framework.cl.MultiTenantBootstrapClassLoader} wont be able to retrieve
 * a point to the native libraries. The multi-tenant service provider should implement this interface and pass an
 * instance of it to the {@link dnet.mt.hi.framework.cl.MultiTenantBootstrapClassLoader}.
 */
public interface NativeLibraryLoader {

    public abstract void load();

}
