package dnet.mt.hi.framework.instrument;

import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

class GetClassLoader0Visitor extends MethodVisitor implements Opcodes {

    GetClassLoader0Visitor(int api, MethodVisitor mv) {
        super(api, mv);
    }

    // ASM code is generated using the GetClassLoader0 in the asmifier module
    public void visitCode() {
        Label label0 = new Label();
        mv.visitLabel(label0);
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ARETURN);
        Label label1 = new Label();
        mv.visitLabel(label1);
        mv.visitLocalVariable("this", "Ljava/lang/Class;", null, label0, label1, 0);
        mv.visitMaxs(1, 1);
    }

}