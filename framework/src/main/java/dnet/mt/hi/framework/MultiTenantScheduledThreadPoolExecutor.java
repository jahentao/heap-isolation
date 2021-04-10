package dnet.mt.hi.framework;

import dnet.mt.hi.framework.cl.TenantClassLoader;

import java.lang.reflect.Field;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class MultiTenantScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    public MultiTenantScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        Runnable tenantRunnable = retrieveTenantRunnable(r);

        TenantClassLoader tcl = (TenantClassLoader) tenantRunnable.getClass().getClassLoader();
        String tenantId = tcl.getTenantId();

        MultiTenantPrintStream out = (MultiTenantPrintStream) System.out;
        out.tenantId.set(tenantId);

        MultiTenantPrintStream err = (MultiTenantPrintStream) System.err;
        err.tenantId.set(tenantId);
    }

    private Runnable retrieveTenantRunnable(Runnable r) {
        Runnable tenantRunnable = null;
        try {
            Field field = r.getClass().getField("callable");
            field.setAccessible(true);
            Object object = field.get(r);
            field.setAccessible(false);

            field = object.getClass().getField("task");
            field.setAccessible(true);
            tenantRunnable = (Runnable) field.get(object);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return tenantRunnable;
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        MultiTenantPrintStream out = (MultiTenantPrintStream) System.out;
        out.tenantId.remove();

        MultiTenantPrintStream err = (MultiTenantPrintStream) System.err;
        err.tenantId.remove();
    }

}
