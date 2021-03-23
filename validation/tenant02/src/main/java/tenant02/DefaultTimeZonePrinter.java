package tenant02;

import java.util.TimeZone;

public class DefaultTimeZonePrinter implements Runnable {

    public void run() {
        System.out.println("run run");
        System.out.println(TimeZone.getDefault().getID());
    }

}
