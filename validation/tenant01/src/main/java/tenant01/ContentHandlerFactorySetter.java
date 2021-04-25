package tenant01;

import java.net.URLConnection;

public class ContentHandlerFactorySetter implements Runnable {

    @Override
    public void run() {
        try {
            URLConnection.setContentHandlerFactory(null);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
