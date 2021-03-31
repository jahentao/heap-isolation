package dnet.mt.hi.framework.instrument;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class JavaLangSystemVisitor extends ClassVisitor {

    public JavaLangSystemVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("setProperties")) {
            mv = new SetPropertiesVisitor(api, mv);
        }
        return mv;
    }

}
