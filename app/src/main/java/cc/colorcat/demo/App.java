package cc.colorcat.demo;

import android.app.Application;

/**
 * Created by cxx on 17-2-23.
 * xx.ch@outlook.com
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApiService.init(this);
        LogUtils.init(this);
    }
}
