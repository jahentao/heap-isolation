package tenant01;

import java.security.Security;

public class PackageDefinitionInvalidator implements Runnable {

    @Override
    public void run() {
        try {
            Security.setProperty("key", "value");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
