package dnet.mt.hi.validation;

import dnet.mt.hi.framework.Job;
import dnet.mt.hi.framework.JobExecutor;
import dnet.mt.hi.framework.JobLoader;
import dnet.mt.hi.framework.MultiTenantServiceManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Validator {

    public static void main(String[] args) {

        System.out.println(System.getProperty("java.home"));

        Properties props = new Properties();
        try {
            props.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        MultiTenantServiceManager multiTenantServiceManager = new MultiTenantServiceManager(
                loadSharedClassNames(props.getProperty("list.shared_classes")),
                buildURI(props.getProperty("jar.java.base")));
        multiTenantServiceManager.registerTenant("tenant01", buildURI(props.getProperty("tenants.01.jar")));
        multiTenantServiceManager.registerTenant("tenant02", buildURI(props.getProperty("tenants.02.jar")));

        JobLoader jobLoader = new JobLoader();
        List<Job> jobs = jobLoader.loadJobs(buildURI(props.getProperty("jobs.csv")));

        JobExecutor jobExecutor = new JobExecutor(multiTenantServiceManager);
        jobExecutor.submit(jobs);
        jobExecutor.shutdownAfterTerminationOrTimeout(10, TimeUnit.SECONDS);

    }

    private static Set<String> loadSharedClassNames(String filePath) {
        Set<String> result = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(
                filePath))) {
            String line = reader.readLine();
            while (line != null) {
                result.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static URI buildURI(String filePath) {
        return Paths.get(System.getProperty("user.dir"), filePath).toUri();
    }

}
