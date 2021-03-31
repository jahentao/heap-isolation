package dnet.mt.hi.framework.instrument;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

public class TenantInitializationVisitor extends ClassVisitor implements Opcodes {

    private String tenantId;

    public TenantInitializationVisitor(int api, ClassVisitor cv, String tenantId) {
        super(api, cv);
        this.tenantId = tenantId;
    }

    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        cv.visit(version, access, name, signature, superName, interfaces);
        MethodVisitor methodVisitor = cv.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        methodVisitor.visitCode();
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLdcInsn(tenantId);
        methodVisitor.visitFieldInsn(PUTSTATIC, name, "tenantId", "Ljava/lang/String;");
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(1, 0);
        methodVisitor.visitEnd();
    }

}
