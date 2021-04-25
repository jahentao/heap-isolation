package tenant01;

import java.util.Locale;

public class DefaultFormatLocaleSetter implements Runnable {

    @Override
    public void run() {
        try {
            Locale.setDefault(Locale.Category.FORMAT, Locale.ITALIAN);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
