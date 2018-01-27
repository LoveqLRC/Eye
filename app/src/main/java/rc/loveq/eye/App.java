package rc.loveq.eye;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

/**
 * Author：Rc
 * 0n 2018/1/27 21:24
 */

public class App extends Application {
    private static Context sApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        sApplicationContext = this;
    }

    public static Context getsApplicationContext() {
        return sApplicationContext;
    }
}
