package tenant02;

import java.util.TimeZone;

public class DefaultTimeZoneSetter implements Runnable {

    static String DEFAULT_TIME_ZONE_ID = "Asia/Kolkata";

    public void run() {
        TimeZone.setDefault(TimeZone.getTimeZone(DEFAULT_TIME_ZONE_ID));
    }

}
