package dnet.mt.hi.framework.instrument;

import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

public class SetPropertiesVisitor extends MethodVisitor implements Opcodes {

    private static final String JAVA_LANG_SYSTEM = System.class.getCanonicalName().replaceAll(".", "/");
    private static final String JAVA_LANG_SECURITY_MANAGER = SecurityManager.class.getCanonicalName().replaceAll(".", "/");
    private static final String JAVA_UTIL_PROPERTIES = "java/util/Properties";

    SetPropertiesVisitor(int api, MethodVisitor mv) {
        super(api, mv);
    }

    // ASM code is generated using the SetProperties class in the asmifier module
    public void visitCode() {
        Label label0 = new Label();
        mv.visitLabel(label0);
        mv.visitMethodInsn(INVOKESTATIC, JAVA_LANG_SYSTEM, "getSecurityManager", "()Ljava/lang/SecurityManager;", false);
        mv.visitVarInsn(ASTORE, 1);
        Label label1 = new Label();
        mv.visitLabel(label1);
        mv.visitVarInsn(ALOAD, 1);
        Label label2 = new Label();
        mv.visitJumpInsn(IFNULL, label2);
        Label label3 = new Label();
        mv.visitLabel(label3);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, JAVA_LANG_SECURITY_MANAGER, "checkPropertiesAccess", "()V", false);
        mv.visitLabel(label2);
        mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {JAVA_LANG_SECURITY_MANAGER}, 0, null);
        mv.visitVarInsn(ALOAD, 0);
        Label label4 = new Label();
        mv.visitJumpInsn(IFNONNULL, label4);
        Label label5 = new Label();
        mv.visitLabel(label5);
        mv.visitTypeInsn(NEW, JAVA_UTIL_PROPERTIES);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, JAVA_UTIL_PROPERTIES, "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 0);
        Label label6 = new Label();
        mv.visitLabel(label6);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, JAVA_LANG_SYSTEM, "initProperties", "(Ljava/util/Properties;)Ljava/util/Properties;", false);
        mv.visitInsn(POP);
        mv.visitLabel(label4);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(PUTSTATIC, JAVA_LANG_SYSTEM, "props", "Ljava/util/Properties;");
        Label label7 = new Label();
        mv.visitLabel(label7);
        mv.visitInsn(RETURN);
        Label label8 = new Label();
        mv.visitLabel(label8);
        mv.visitLocalVariable("props", "Ljava/util/Properties;", null, label0, label8, 0);
        mv.visitLocalVariable("sm", "Ljava/lang/SecurityManager;", null, label1, label8, 1);
        mv.visitMaxs(2, 2);
    }

}
