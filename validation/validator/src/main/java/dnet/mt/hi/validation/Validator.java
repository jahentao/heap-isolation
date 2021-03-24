package dnet.mt.hi.validation;

import dnet.mt.hi.framework.Job;
import dnet.mt.hi.framework.JobExecutor;
import dnet.mt.hi.framework.JobLoader;
import dnet.mt.hi.framework.TenantRegistry;

import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Validator {

    public static void main(String[] args) {

        if (args.length != 5) {
            throw new IllegalArgumentException("Input arguments should be speficied in the following order:\n" +
                    "\t\t1) relative path to a file containing the list of init classes\n" +
                    "\t\t2) relative path to java.base.jar file\n" +
                    "\t\t3) relative path to tenant01.jar file\n" +
                    "\t\t4) relative path to tenant02.jar file\n" +
                    "\t\t5) relative path to jobs.csv file\n");
        }

        int argIndex = 0;

        String initClassNamesPath = args[argIndex++];
        String javaBaseJarPath = args[argIndex++];
        String tenant01JarPath = args[argIndex++];
        String tenant02JarPath = args[argIndex++];
        String jobsCSVPath = args[argIndex++];

        TenantRegistry tenantRegistry = new TenantRegistry(buildURI(initClassNamesPath), buildURI(javaBaseJarPath));
        tenantRegistry.registerTenant("tenant01", buildURI(tenant01JarPath));
        tenantRegistry.registerTenant("tenant02", buildURI(tenant02JarPath));

        JobLoader jobLoader = new JobLoader();
        List<Job> jobs = jobLoader.loadJobs(buildURI(jobsCSVPath));

        JobExecutor jobExecutor = new JobExecutor(tenantRegistry);
        jobExecutor.submit(jobs);
        jobExecutor.shutdownAfterTerminationOrTimeout(10, TimeUnit.SECONDS);

    }

    private static URI buildURI(String filePath) {
        return Paths.get(System.getProperty("user.dir"), filePath).toUri();
    }

}
