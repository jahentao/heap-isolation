package dnet.mt.hi.framework.instrument;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class IsolationClassVisitor extends ClassVisitor {

    public IsolationClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("getClassLoader0")) {
            mv = new IsolationMethodVisitor(api, mv);
        }
        return mv;

    }

}