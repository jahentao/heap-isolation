package tenant02;

import java.net.ServerSocket;

public class ServerSocketFactorySetter implements Runnable {

    @Override
    public void run() {
        try {
            ServerSocket.setSocketFactory(null);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
