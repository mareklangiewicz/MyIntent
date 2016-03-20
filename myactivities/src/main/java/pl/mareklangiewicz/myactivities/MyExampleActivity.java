package pl.mareklangiewicz.myactivities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.MenuItem;

import pl.mareklangiewicz.myviews.IMyUINavigation;

/**
 * Created by Marek Langiewicz on 22.09.15.
 * Simple example activity using MyActivity features...
 */
@SuppressLint("Registered")
public class MyExampleActivity extends MyActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.i("Hi, I am the example activity..");
        //noinspection ConstantConditions
        getGnav().inflateMenu(R.menu.ma_my_example_global);
        getGnav().inflateHeader(R.layout.ma_my_example_global_header);
        if(savedInstanceState == null)
            getGnav().setCheckedItem(R.id.ma_meg_i_my_example_fragment_1, true);
    }

    @Override public boolean onItemSelected(IMyUINavigation nav, MenuItem item) {
        boolean done = super.onItemSelected(nav, item);
        if(done)
            return true;
        @IdRes int id = item.getItemId();
        if(id == R.id.ma_meg_i_my_example_action) {
            log.i("[SNACK][SHORT]Example: ACTION!");
            return true;
        }
        return false;
    }
}
