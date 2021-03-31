package dnet.mt.hi.asmifier;

import org.objectweb.asm.util.ASMifier;

import java.io.IOException;

public class ASMCodeGenerator {

    public static void main(String[] args) {
        try {
            ASMifier.main(new String[] {
                    GetClassLoader0.class.getCanonicalName()
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
