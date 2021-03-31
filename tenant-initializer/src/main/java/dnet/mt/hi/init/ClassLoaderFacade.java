package dnet.mt.hi.init;

public class ClassLoaderFacade {

    private static final ClassLoader NO_OP_CLASS_LOADER = new NoOpClassLoader();

    public static ClassLoader getClassLoader(String packageName) {
        if (isBootstrapPackage(packageName)) {
            return null;
        } else {
            return NO_OP_CLASS_LOADER;
        }
    }

    private static boolean isBootstrapPackage(String packageName) {
        return  packageName.startsWith("java.") || packageName.startsWith("javax.") ||
                packageName.startsWith("com.sun.") || packageName.startsWith("sun.") ||
                packageName.startsWith("jdk.internal.");
    }

}
