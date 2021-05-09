package dnet.mt.hi.framework;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JobExecutor {

    private ScheduledExecutorService scheduledExecutorService = new dnet.mt.hi.framework.MultiTenantScheduledThreadPoolExecutor(4);
    private MultiTenantServiceManager multiTenantServiceManager;

    public JobExecutor(MultiTenantServiceManager multiTenantServiceManager) {
        this.multiTenantServiceManager = multiTenantServiceManager;
    }

    public void submit(List<Job> jobs) {
        jobs.forEach(job ->
                scheduledExecutorService.schedule(createRunnable(job.tenantId, job.runnableClassName), job.delayInSeconds, TimeUnit.SECONDS)
        );
    }

    private Runnable createRunnable(String tenantId, String runnableClassName) {
        try {
            Class runnableClass = multiTenantServiceManager.getTenantClassLoader(tenantId).loadClass(runnableClassName);
            Constructor constructor = runnableClass.getConstructor();
            return (Runnable) constructor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void shutdownAfterTerminationOrTimeout(long timeout, TimeUnit tu) {
        scheduledExecutorService.shutdown();
        try {
            scheduledExecutorService.awaitTermination(timeout, tu);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
