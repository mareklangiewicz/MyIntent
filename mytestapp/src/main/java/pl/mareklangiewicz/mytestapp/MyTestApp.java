package pl.mareklangiewicz.mytestapp;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Marek Langiewicz on 19.10.15.
 *
 */
public class MyTestApp extends Application {

    @Override public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
