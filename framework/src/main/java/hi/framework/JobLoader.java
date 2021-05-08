package hi.framework;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class JobLoader {

    public List<Job> loadJobs(URI jobsCSVPath) {
        List<Job> jobs = new LinkedList<Job>();

        try (Stream<String> stream = Files.lines(Paths.get(jobsCSVPath))) {
            stream.forEach(line -> jobs.add(new Job(line.split(","))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jobs;
    }

}
