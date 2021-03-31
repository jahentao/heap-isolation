package dnet.mt.hi.init;

import jdk.internal.misc.VM;
import jdk.internal.util.StaticProperty;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * This class should implement the Runnable interface as the latter is the only common interface between the tenant
 * code and the rest of the system.
 */
public class TenantInitializer implements Runnable {

    // This will be set by a static block generated on the fly while loading this class for a specific tenant
    private static String tenantHome;

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

        try {
            File outFile = Paths.get(tenantHome, "jvm.out").toFile();
            outFile.delete();
            outFile.createNewFile();
            FileOutputStream fosOut = new FileOutputStream(outFile);
            System.setOut(new PrintStream(fosOut));

            File errFile = Paths.get(tenantHome, "jvm.err").toFile();
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
        try {

            Properties props = new Properties();
            FileInputStream fis = new FileInputStream(tenantHome);
            props.load(fis);
            fis.close();

            props.setProperty("user.home", tenantHome);
            System.setProperties(props);

            VM.saveAndRemoveProperties(props);
            setLineSeparator(props.getProperty("line.separator"));
            StaticProperty.javaHome();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setLineSeparator(String lineSeparator) {
        try {
            Field field = System.class.getDeclaredField("lineSeparator");
            field.setAccessible(true);
            field.set(null, lineSeparator);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setJavaLangAccess() {
        try {
            Method method = System.class.getDeclaredMethod("setJavaLangAccess");
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
