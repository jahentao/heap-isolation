package tenant01;

import java.net.URL;
import java.net.URLConnection;

public class DefaultUseCachesModifier implements Runnable {

    @Override
    public void run() {
        try {
            URL oracle = new URL("http://www.oracle.com/");
            URLConnection connection = oracle.openConnection();
            connection.setDefaultUseCaches(false);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
