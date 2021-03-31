package dnet.mt.hi.framework.cl;

import dnet.mt.hi.framework.MultiTenantServiceManager;
import dnet.mt.hi.framework.instrument.JavaLangClassVisitor;
import dnet.mt.hi.framework.instrument.TenantInitializationVisitor;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.stream.Stream;

abstract class AbstractMTClassLoader extends ClassLoader {

    protected String tenantId;

    private static final String EXCEPTION_MESSAGE = "This class loader is merely responsible for isolating tenant code" +
            " from the rest of the execution environment.";

    AbstractMTClassLoader(String name, ClassLoader parent) {
        super(name, parent);
    }

    Class<?> findClass(String name, FileSystem fs, ProtectionDomain pd) {
        try {
            InputStream is = Files.newInputStream(fs.getPath(name.replace('.', '/').concat(".class")),
                    StandardOpenOption.READ);
            byte[] bytes = getBytecode(name, is);
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

    private byte[] getBytecode(String name, InputStream is) throws IOException {
        if (name.equals(Class.class.getCanonicalName())) {
            return instrumentJavaLangClass(is);
        } else if (name.equals(MultiTenantServiceManager.TENANT_INITIALIZER_CLASS_NAME)) {
            return instrumentTenantInitializer(is, tenantId);
        } else {
            return is.readAllBytes();
        }
    }

    private byte[] instrumentJavaLangClass(InputStream is) throws IOException {
        ClassReader cr = new ClassReader(is);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new JavaLangClassVisitor(Opcodes.ASM6, cw);
        cr.accept(cv, 0);
        return cw.toByteArray();
    }

    private byte[] instrumentTenantInitializer(InputStream is, String tenantId) throws IOException {
        ClassReader cr = new ClassReader(is);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        String tenantHome = String.format("%s/%s", System.getProperty("user.home"), tenantId);
        ClassVisitor cv = new TenantInitializationVisitor(Opcodes.ASM6, cw, tenantHome);
        cr.accept(cv, 0);
        return cw.toByteArray();
    }

    protected URL findResource(String moduleName, String name) {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    public URL getResource(String name) {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }


    public Enumeration<URL> getResources(String name) {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    public Stream<URL> resources(String name) {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    protected URL findResource(String name) {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    protected Enumeration<URL> findResources(String name) {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

}
