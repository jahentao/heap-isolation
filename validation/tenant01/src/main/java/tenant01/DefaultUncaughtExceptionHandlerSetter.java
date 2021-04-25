package tenant01;

public class DefaultUncaughtExceptionHandlerSetter implements Runnable {

    @Override
    public void run() {
        try {
            Thread.setDefaultUncaughtExceptionHandler(null);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
