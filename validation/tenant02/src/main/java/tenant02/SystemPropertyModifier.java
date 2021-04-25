package tenant02;

import java.util.Properties;

public class SystemPropertyModifier implements Runnable {

    @Override
    public void run() {
        try {
            Properties props = System.getProperties();
            props.setProperty("user.home", "/");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
