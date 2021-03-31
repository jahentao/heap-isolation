package dnet.mt.hi.init;

import jdk.internal.misc.VM;
import jdk.internal.util.StaticProperty;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class should implement the Runnable interface as the latter is the only common interface between the tenant
 * code and the rest of the system.
 */
public class TenantInitializer implements Runnable {

    // This will be set by a static block generated on the fly while loading this class for a specific tenant
    private static String tenantId;

    /**
     * This performs a subset of operations implemented in initPhase1, initPhase2, and initPhase3 of the System class.
     */
    @Override
    public void run() {
        initProps();
        initIO(); // This should be after initProps as it relies on a tenant-specific user.home property
        setJavaLangAccess();
        VM.initLevel(4);
        initSecurityManager(); // This should be the last one. Otherwise, the others may face IllegalAccessException.
    }

    private void initIO() {

        System.setIn(null);

        File tenantDirectory = new File(System.getProperty("user.home"));
        tenantDirectory.mkdir();

        try {
            File outFile = new File(tenantDirectory, "jvm.out");
            outFile.delete();
            outFile.createNewFile();
            FileOutputStream fosOut = new FileOutputStream(outFile);
            System.setOut(new PrintStream(fosOut));

            File errFile = new File(tenantDirectory, "jvm.err");
            errFile.delete();
            errFile.createNewFile();
            FileOutputStream fosErr = new FileOutputStream(errFile);
            System.setErr(new PrintStream(fosErr));
        } catch (IOException e) {
            e.printStackTrace();
        }

        nullifyConsoleFileDescriptors();

    }

    private void nullifyConsoleFileDescriptors() {
        try {
            Field field = FileDescriptor.class.getField("in");
            field.setAccessible(true);
            field.set(null, null);
            field.setAccessible(false);

            field = FileDescriptor.class.getField("out");
            field.setAccessible(true);
            field.set(null, null);
            field.setAccessible(false);

            field = FileDescriptor.class.getField("err");
            field.setAccessible(true);
            field.set(null, null);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void initProps() {
        System.setProperties(null); // Passing null will trigger the VM to initialize system properties
        System.setProperty("user.home", String.format("%s/%s", System.getProperty("user.home"), tenantId));
        VM.saveAndRemoveProperties(System.getProperties());
        setLineSeparator(System.getProperty("line.separator"));
        StaticProperty.javaHome();
    }

    private void setLineSeparator(String lineSeparator) {
        try {
            Field field = System.class.getField("lineSeparator");
            field.setAccessible(true);
            field.set(null, lineSeparator);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setJavaLangAccess() {
        try {
            Method method = System.class.getMethod("setJavaLangAccess");
            method.setAccessible(true);
            method.invoke(null);
            method.setAccessible(false);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void initSecurityManager() {
        // TODO set SecurityManager
    }

}
