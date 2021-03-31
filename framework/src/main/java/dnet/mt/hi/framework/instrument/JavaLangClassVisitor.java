package dnet.mt.hi.framework.instrument;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class JavaLangClassVisitor extends ClassVisitor {

    public JavaLangClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("getClassLoader0")) {
            mv = new GetClassLoader0Visitor(api, mv);
        }
        return mv;

    }

}