package pl.mareklangiewicz.myfragments;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.noveogroup.android.log.MyLogger;

public class MyBaseFragment extends Fragment {

    protected final MyLogger log = MyLogger.sMyDefaultUILogger;

    public MyBaseFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        log.v("%s.%s", this.getClass().getSimpleName(), "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        log.v("%s.%s", this.getClass().getSimpleName(), "onDestroy");
        super.onDestroy();
    }

    public void setLocalMenu(int id) {
        //TODO implement it
        //TODO also case with 'null' menu - so it doesn't show up at all (and no icon in action bar)
    }

}
