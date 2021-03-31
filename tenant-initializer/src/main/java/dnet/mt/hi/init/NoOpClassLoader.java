package dnet.mt.hi.init;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.stream.Stream;

/**
 * If tenant code calls the getClassLoader on any Class object, it will get an instance of TenantClassLoader which
 * inherits from a different ClassLoader class than the one recognized by the tenant code. This may cause JVM crashes.
 * In order to avoid JVM crashes, the NoOpClassLoader instance should be returned to tenant code every time the
 * getClassLoader is invoked.
 */
class NoOpClassLoader extends ClassLoader {

    private static final String EXCEPTION_MESSAGE = "This class loader is merely responsible for isolating tenant code" +
            " from the rest of the execution environment.";

    @Override
    public String getName() {
        return String.format("NoOpClassLoader_%d", hashCode());
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public URL getResource(String name) {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public Stream<URL> resources(String name) {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public void setDefaultAssertionStatus(boolean enabled) {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public void setPackageAssertionStatus(String packageName, boolean enabled) {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public void setClassAssertionStatus(String className, boolean enabled) {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public void clearAssertionStatus() {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

}
