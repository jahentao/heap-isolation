package dnet.mt.hi.framework.instrument;

import dnet.mt.hi.framework.cl.TenantSpecificBootstrapClassLoader;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

class IsolationMethodVisitor extends MethodVisitor implements Opcodes {

    IsolationMethodVisitor(int api, MethodVisitor mv) {
        super(api, mv);
    }

    public void visitCode() {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, "java/lang/Class", "classLoader", "Ljava/lang/ClassLoader;");
        mv.visitTypeInsn(INSTANCEOF, TenantSpecificBootstrapClassLoader.class.getCanonicalName());
        Label label = new Label();
        mv.visitJumpInsn(IFEQ, label);
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ARETURN);
        mv.visitLabel(label);
        mv.visitMaxs(1, 1);
        mv.visitCode();
    }

}
