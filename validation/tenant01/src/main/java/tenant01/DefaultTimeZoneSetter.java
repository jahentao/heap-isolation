package tenant01;

import java.util.TimeZone;

public class DefaultTimeZoneSetter implements Runnable {

    public void run() {
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
