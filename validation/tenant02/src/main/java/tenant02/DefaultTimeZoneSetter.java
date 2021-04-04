package tenant02;

import java.util.TimeZone;

public class DefaultTimeZoneSetter implements Runnable {

    public void run() {
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
