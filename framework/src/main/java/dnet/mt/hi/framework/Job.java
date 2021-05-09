package dnet.mt.hi.framework;

public class Job {

    long delayInSeconds;
    String tenantId;
    String runnableClassName;

    Job(String[] array) {
        if (array.length != 3) {
            throw new IllegalArgumentException("The array should have three elements.");
        }
        delayInSeconds = Long.parseLong(array[0]);
        tenantId = array[1];
        runnableClassName = array[2];
    }

}
