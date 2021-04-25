package tenant02;

import java.net.URL;

public class URLStreamHandlerFactorySetter implements Runnable {

    @Override
    public void run() {
        try {
            URL.setURLStreamHandlerFactory(null);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
