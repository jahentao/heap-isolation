package tenant01;

import java.net.Socket;

public class SocketImplFactorySetter implements Runnable {

    @Override
    public void run() {
        try {
            Socket.setSocketImplFactory(null);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
