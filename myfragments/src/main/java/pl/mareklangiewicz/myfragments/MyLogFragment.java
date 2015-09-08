package pl.mareklangiewicz.myfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.noveogroup.android.log.Logger;

import pl.mareklangiewicz.myloggers.MyLogRecyclerView;

/**
 * Created by Marek Langiewicz on 22.07.15.
 */

//TODO LATER: set local menu with log levels using xml (after implementing this feature in MyFragment)

public final class MyLogFragment extends MyFragment {

    private MyLogRecyclerView mMyLogRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_log_fragment, container, false);
        mMyLogRecyclerView = (MyLogRecyclerView) rootView.findViewById(R.id.my_log_recycler_view);
        mMyLogRecyclerView.setLog(log);
        inflateMenu(R.menu.my_log_options_menu);
        updateCheckedItem();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        mMyLogRecyclerView.setLog(null);
        super.onDestroyView();
    }

    /**
     * Override it to your needs
     *
     * @param item Selected menu item from global or local menu.
     * @return True if item was successfully selected.
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.log_level_error) {
            log.setHistoryFilterLevel(Logger.Level.ERROR);
            item.setChecked(true);
            return true;
        }
        else if(id == R.id.log_level_warning) {
            log.setHistoryFilterLevel(Logger.Level.WARN);
            item.setChecked(true);
            return true;
        }
        else if(id == R.id.log_level_info) {
            log.setHistoryFilterLevel(Logger.Level.INFO);
            item.setChecked(true);
            return true;
        }
        else if(id == R.id.log_level_debug) {
            log.setHistoryFilterLevel(Logger.Level.DEBUG);
            item.setChecked(true);
            return true;
        }
        else if(id == R.id.log_level_verbose) {
            log.setHistoryFilterLevel(Logger.Level.VERBOSE);
            item.setChecked(true);
            return true;
        }
        else if(id == R.id.log_blabla) {
            log.e("bla bla");
        }
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

    /*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.my_log_options_menu, menu);
        if(log == null)
            return;
        switch (log.getLogHistory().getFilterLevel()) {
            case ERROR  : menu.findItem(R.id.log_level_error  ).setChecked(true); break;
            case WARN   : menu.findItem(R.id.log_level_warning).setChecked(true); break;
            case INFO   : menu.findItem(R.id.log_level_info   ).setChecked(true); break;
            case DEBUG  : menu.findItem(R.id.log_level_debug  ).setChecked(true); break;
            case VERBOSE: menu.findItem(R.id.log_level_verbose).setChecked(true); break;
        }

    }
*/



/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.log_level_error) {
            log.setHistoryFilterLevel(Logger.Level.ERROR);
            item.setChecked(true);
            return true;
        }
        else if(id == R.id.log_level_warning) {
            log.setHistoryFilterLevel(Logger.Level.WARN);
            item.setChecked(true);
            return true;
        }
        else if(id == R.id.log_level_info) {
            log.setHistoryFilterLevel(Logger.Level.INFO);
            item.setChecked(true);
            return true;
        }
        else if(id == R.id.log_level_debug) {
            log.setHistoryFilterLevel(Logger.Level.DEBUG);
            item.setChecked(true);
            return true;
        }
        else if(id == R.id.log_level_verbose) {
            log.setHistoryFilterLevel(Logger.Level.VERBOSE);
            item.setChecked(true);
            return true;
        }
        else if(id == R.id.log_blabla) {
            log.e("bla bla");
        }
        return super.onOptionsItemSelected(item);
    }
*/
}
