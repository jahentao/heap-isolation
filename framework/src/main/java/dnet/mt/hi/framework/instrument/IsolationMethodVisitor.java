package dnet.mt.hi.framework.instrument;

import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

class IsolationMethodVisitor extends MethodVisitor implements Opcodes {

    IsolationMethodVisitor(int api, MethodVisitor mv) {
        super(api, mv);
    }

    public void visitCode() {
        Label label0 = new Label();
        mv.visitLabel(label0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, "java/lang/Class", "packageName", "Ljava/lang/String;");
        mv.visitMethodInsn(INVOKESTATIC, "dnet/mt/hi/init/ClassLoaderFacade", "getClassLoader",
                "(Ljava/lang/String;)Ljava/lang/ClassLoader;", false);
        mv.visitInsn(ARETURN);
        Label label1 = new Label();
        mv.visitLabel(label1);
        mv.visitLocalVariable("this", "Ljava/lang/Class;", null, label0, label1, 0);
        mv.visitMaxs(1, 1);
    }

}