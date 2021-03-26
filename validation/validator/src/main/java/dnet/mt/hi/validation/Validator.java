package dnet.mt.hi.validation;

import dnet.mt.hi.framework.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Validator {

    public static void main(String[] args) {

        Properties props = new Properties();
        try {
            props.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        MultiTenantServiceManager multiTenantServiceManager = new MultiTenantServiceManager(
                new NativeLibraryLoaderImpl(buildURI(props.getProperty("java.native.lib"))),
                buildURI(props.getProperty("java.base.jar")));
        multiTenantServiceManager.registerTenant("tenant01", buildURI(props.getProperty("tenants.01.jar")));
        multiTenantServiceManager.registerTenant("tenant02", buildURI(props.getProperty("tenants.02.jar")));

        JobLoader jobLoader = new JobLoader();
        List<Job> jobs = jobLoader.loadJobs(buildURI(props.getProperty("jobs.csv")));

        JobExecutor jobExecutor = new JobExecutor(multiTenantServiceManager);
        jobExecutor.submit(jobs);
        jobExecutor.shutdownAfterTerminationOrTimeout(10, TimeUnit.SECONDS);

    }

    private static URI buildURI(String filePath) {
        return Paths.get(System.getProperty("user.dir"), filePath).toUri();
    }

    private static class NativeLibraryLoaderImpl implements NativeLibraryLoader {

        private URI uri;

        private NativeLibraryLoaderImpl(URI uri) {
            this.uri = uri;
        }

        @Override
        public void load() {
            System.load(Path.of(uri).toString());
        }
    }

}
