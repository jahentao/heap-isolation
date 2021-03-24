package tenant02;

import java.util.TimeZone;

public class DefaultTimeZonePrinter implements Runnable {

    public void run() {
        try {
            System.out.println(String.format("tenant02 is in %s.", TimeZone.getDefault().getID()));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
