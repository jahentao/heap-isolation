package tenant01;

import java.util.TimeZone;

public class DefaultTimeZoneSetter implements Runnable {

    static String DEFAULT_TIME_ZONE_ID = "America/Sao_Paulo";

    public void run() {
        TimeZone.setDefault(TimeZone.getTimeZone(DEFAULT_TIME_ZONE_ID));
    }

}
