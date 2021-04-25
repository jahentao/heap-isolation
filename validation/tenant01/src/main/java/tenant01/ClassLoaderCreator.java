package tenant01;

import java.net.URLClassLoader;

public class ClassLoaderCreator implements Runnable {

    @Override
    public void run() {
        try {
            ClassLoader cl = new URLClassLoader(null);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
