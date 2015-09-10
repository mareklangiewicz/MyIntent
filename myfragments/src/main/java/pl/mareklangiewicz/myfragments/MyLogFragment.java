package pl.mareklangiewicz.myfragments;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.noveogroup.android.log.Logger;

import pl.mareklangiewicz.myloggers.MyLogRecyclerView;

/**
 * MyFragment showing MyLogger messages.
 */
public final class MyLogFragment extends MyFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_log_fragment, container, false);
        ((MyLogRecyclerView) rootView.findViewById(R.id.my_log_recycler_view)).setLog(log);
        //TODO LATER: some nice simple header with fragment title
        inflateMenu(R.menu.my_log_menu);
        updateCheckedItem();
        return rootView;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        @IdRes int id = item.getItemId();
        if     (id == R.id.log_level_error  ) { log.setHistoryFilterLevel(Logger.Level.ERROR);   return true; }
        else if(id == R.id.log_level_warning) { log.setHistoryFilterLevel(Logger.Level.WARN);    return true; }
        else if(id == R.id.log_level_info   ) { log.setHistoryFilterLevel(Logger.Level.INFO);    return true; }
        else if(id == R.id.log_level_debug  ) { log.setHistoryFilterLevel(Logger.Level.DEBUG);   return true; }
        else if(id == R.id.log_level_verbose) { log.setHistoryFilterLevel(Logger.Level.VERBOSE); return true; }
        else if(id == R.id.log_some_assert  ) { log.a("some assert" ); return true; }
        else if(id == R.id.log_some_error   ) { log.e("some error"  ); return true; }
        else if(id == R.id.log_some_warning ) { log.w("some warning"); return true; }
        else if(id == R.id.log_some_info    ) { log.i("some info"   ); return true; }
        else if(id == R.id.log_some_debug   ) { log.d("some debug"  ); return true; }
        else if(id == R.id.log_some_verbose ) { log.v("some verbose"); return true; }
        return super.onNavigationItemSelected(item);
    }

    private void updateCheckedItem() {
        Menu menu = getMenu();
        if(menu == null) {
            log.d("Menu is null!");
            return;
        }
        switch (log.getLogHistory().getFilterLevel()) {
            case ERROR  : menu.findItem(R.id.log_level_error  ).setChecked(true); break;
            case WARN   : menu.findItem(R.id.log_level_warning).setChecked(true); break;
            case INFO   : menu.findItem(R.id.log_level_info   ).setChecked(true); break;
            case DEBUG  : menu.findItem(R.id.log_level_debug  ).setChecked(true); break;
            case VERBOSE: menu.findItem(R.id.log_level_verbose).setChecked(true); break;
        }
    }

}
