package tenant02;

import java.net.URLConnection;

public class FileNameMapSetter implements Runnable {

    @Override
    public void run() {
        try {
            URLConnection.setFileNameMap(null);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
