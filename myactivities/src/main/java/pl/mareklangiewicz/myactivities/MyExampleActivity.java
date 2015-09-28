package pl.mareklangiewicz.myactivities;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.MenuItem;

import pl.mareklangiewicz.myviews.IMyNavigation;

/**
 * Created by Marek Langiewicz on 22.09.15.
 * Simple example activity using MyBlock stuff...
 */
public class MyExampleActivity extends MyActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.i("Hi, I am the example activity..");
        //noinspection ConstantConditions
        getGlobalNavigation().inflateMenu(R.menu.my_example_global_menu);
        getGlobalNavigation().inflateHeader(R.layout.my_example_global_header);
        if(savedInstanceState == null) {
            selectGlobalItem(R.id.my_example_fragment_1);
        }
    }

    @Override public boolean onItemSelected(IMyNavigation nav, MenuItem item) {
        boolean done = super.onItemSelected(nav, item);
        if(done)
            return true;
        @IdRes int id = item.getItemId();
        if(id == R.id.my_example_action) {
            log.i("[SNACK][SHORT]Example: ACTION!");
            return true;
        }
        return false;
    }
}
