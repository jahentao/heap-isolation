package tenant01;

import java.util.TimeZone;

public class DefaultTimeZonePrinter implements Runnable {

    public void run() {
        try {
            System.out.println("run");
            System.out.println(TimeZone.getDefault().getID());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
