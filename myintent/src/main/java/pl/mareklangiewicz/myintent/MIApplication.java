package pl.mareklangiewicz.myintent;

import android.app.Application;

//import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Marek Langiewicz on 20.10.15.
 *
 */
public class MIApplication extends Application {

    public String[] FUNNY_QUOTES;
    public String[] SMART_QUOTES;

    @Override public void onCreate() {
        FUNNY_QUOTES = getResources().getStringArray(R.array.mr_funny_quotes);
        SMART_QUOTES = getResources().getStringArray(R.array.mr_smart_quotes);
        super.onCreate();
//        LeakCanary.install(this);
    }
}
