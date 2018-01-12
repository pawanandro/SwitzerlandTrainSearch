package switzersearch.com.switzertrainsearch.helper;

import java.util.concurrent.Callable;

/**
 * Created by ${Pawan} on 1/12/2018.
 */

public class Application {
    private static Application ourInstance = new Application();

    public static Application getInstance() {
        return ourInstance;
    }

    private Callable<Void> mLogoutCallable;

    private Application() {
    }

    public void setLogoutCallable(Callable<Void> callable) {
        mLogoutCallable = callable;
    }
}
