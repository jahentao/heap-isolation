package tenant02;

import java.util.Locale;

public class DefaultDisplayLocaleSetter implements Runnable {

    @Override
    public void run() {
        try {
            Locale.setDefault(Locale.Category.DISPLAY, Locale.CHINA);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
