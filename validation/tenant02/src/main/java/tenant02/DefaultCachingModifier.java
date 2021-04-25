package tenant02;

import java.net.URLConnection;

public class DefaultCachingModifier implements Runnable {

    @Override
    public void run() {
        try {
           URLConnection.setDefaultUseCaches("http", false);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
