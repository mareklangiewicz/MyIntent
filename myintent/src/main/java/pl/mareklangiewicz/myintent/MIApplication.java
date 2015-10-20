package pl.mareklangiewicz.myintent;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Marek Langiewicz on 20.10.15.
 *
 */
public class MIApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
