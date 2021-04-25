package tenant02;

public class SystemOutSetter implements Runnable {

    @Override
    public void run() {
        try {
            System.setOut(null);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
