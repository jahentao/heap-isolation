package dnet.mt.hi.framework.cl;

import dnet.mt.hi.framework.instrument.IsolationClassVisitor;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.ProtectionDomain;

abstract class FileSystemClassLoader extends ClassLoader {

    FileSystemClassLoader(String name, ClassLoader parent) {
        super(name, parent);
    }

    Class<?> findClass(String name, FileSystem fs, ProtectionDomain pd) {
        try {
            InputStream is = Files.newInputStream(fs.getPath(name.replace('.', '/').concat(".class")),
                    StandardOpenOption.READ);
            byte[] bytes;
            if (name.equals("java.lang.Class")) {
                bytes = instrument(is);
            } else {
                bytes = is.readAllBytes();
            }
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

    private byte[] instrument(InputStream is) throws IOException {
        ClassReader cr = new ClassReader(is);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new IsolationClassVisitor(Opcodes.ASM6, cw);
        cr.accept(cv, 0);
        return cw.toByteArray();
    }

}
