package dnet.mt.hi.eval.mf.process;

import dnet.mt.hi.jrt.JRTUtil;

import java.util.List;

public class WorstCaseMemoryFootprintGenerator {

    public static void main(String[] args) {
        List<String> classNames = JRTUtil.getAllJavaBaseClassNames();
        for (String className : classNames) {
            try {
                Class.forName(className);
            } catch (ClassNotFoundException e) {
                System.err.println(e.getMessage());
            }
        }
        System.out.println("I'm ready for docker stat!");
        while (true);
    }

}
