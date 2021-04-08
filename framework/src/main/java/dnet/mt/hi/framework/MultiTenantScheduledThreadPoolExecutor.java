package dnet.mt.hi.framework;

import dnet.mt.hi.framework.cl.TenantClassLoader;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class MultiTenantScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    public MultiTenantScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        TenantClassLoader tcl = (TenantClassLoader) r.getClass().getClassLoader();
        String tenantId = tcl.getTenantId();

        MultiTenantPrintStream out = (MultiTenantPrintStream) System.out;
        out.tenantId.set(tenantId);

        MultiTenantPrintStream err = (MultiTenantPrintStream) System.err;
        err.tenantId.set(tenantId);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        MultiTenantPrintStream out = (MultiTenantPrintStream) System.out;
        out.tenantId.remove();

        MultiTenantPrintStream err = (MultiTenantPrintStream) System.err;
        err.tenantId.remove();
    }

}
