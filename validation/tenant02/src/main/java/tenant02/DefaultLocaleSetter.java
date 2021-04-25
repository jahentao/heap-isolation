package tenant02;

import java.util.Locale;

public class DefaultLocaleSetter implements Runnable {

    public void run() {
        try {
            Locale.setDefault(Locale.GERMAN);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
