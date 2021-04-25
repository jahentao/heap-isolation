package tenant02;

public class NativeLibraryLoader implements Runnable {

    @Override
    public void run() {
        try {
           System.load("file.lib");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
