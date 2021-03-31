package dnet.mt.hi.framework.instrument;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TenantInitializationVisitor extends ClassVisitor implements Opcodes {

    private static final String systemProperties;
    private String tenantId;

    static {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            System.getProperties().store(baos, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        systemProperties = baos.toString();
        System.out.println(systemProperties);
    }

    public TenantInitializationVisitor(int api, ClassVisitor cv, String tenantId) {
        super(api, cv);
        this.tenantId = tenantId;
    }

    // ASM code is gerenated using the TenantInitializer class in the asmifier module
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        cv.visit(version, access, name, signature, superName, interfaces);
        MethodVisitor methodVisitor = cv.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        methodVisitor.visitCode();
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLdcInsn(tenantId);
        methodVisitor.visitFieldInsn(PUTSTATIC, "dnet/mt/hi/init/TenantInitializer", "tenantId", "Ljava/lang/String;");
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLdcInsn(systemProperties);
        methodVisitor.visitFieldInsn(PUTSTATIC, "dnet/mt/hi/init/TenantInitializer", "systemProperties", "Ljava/lang/String;");
        Label label2 = new Label();
        methodVisitor.visitLabel(label2);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(1, 0);
        methodVisitor.visitEnd();
    }

}
