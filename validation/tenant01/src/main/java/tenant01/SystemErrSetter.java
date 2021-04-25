package tenant01;

public class SystemErrSetter implements Runnable {

    @Override
    public void run() {
        try {
            System.setErr(null);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
