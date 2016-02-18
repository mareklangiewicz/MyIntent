package pl.mareklangiewicz.myfragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.MyAndroidLogger;

import pl.mareklangiewicz.myloggers.MyLogRecyclerView;
import pl.mareklangiewicz.myviews.IMyNavigation;

/**
 * MyFragment showing MyAndroidLogger messages.
 */
public final class MyLogFragment extends MyFragment {

    private @NonNull final MyAndroidLogger malog = (MyAndroidLogger) log;

    private @Nullable MyLogRecyclerView mMLRView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState); //just for logging

        View rootView = inflater.inflate(R.layout.mf_my_log_fragment, container, false);
        mMLRView = (MyLogRecyclerView) rootView.findViewById(R.id.my_log_recycler_view);
        mMLRView.setLog(malog);
        //TODO SOMEDAY: some nice simple header with fragment title
        inflateMenu(R.menu.mf_my_log);
        updateCheckedItem();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setExitTransition(new Slide(Gravity.START));
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        //noinspection ConstantConditions
        mMLRView.setLog(null);
        mMLRView = null;
        super.onDestroyView();
    }

    @Override
    public boolean onItemSelected(IMyNavigation nav, MenuItem item) {
        @IdRes int id = item.getItemId();
        if(id == R.id.log_level_error) {
            malog.setHistoryFilterLevel(Logger.Level.ERROR);
            return true;
        }
        else if(id == R.id.log_level_warning) {
            malog.setHistoryFilterLevel(Logger.Level.WARN);
            return true;
        }
        else if(id == R.id.log_level_info) {
            malog.setHistoryFilterLevel(Logger.Level.INFO);
            return true;
        }
        else if(id == R.id.log_level_debug) {
            malog.setHistoryFilterLevel(Logger.Level.DEBUG);
            return true;
        }
        else if(id == R.id.log_level_verbose) {
            malog.setHistoryFilterLevel(Logger.Level.VERBOSE);
            return true;
        }
        else if(id == R.id.clear_log_history) {
            malog.getLogHistory().clear();
            if(mMLRView != null) {
                mMLRView.getAdapter().notifyDataSetChanged();
            }
            return true;
        }
        else if(id == R.id.log_some_assert) {
            log.a("some assert", null);
            return true;
        }
        else if(id == R.id.log_some_error) {
            log.e("some error", null);
            return true;
        }
        else if(id == R.id.log_some_warning) {
            log.w("some warning", null);
            return true;
        }
        else if(id == R.id.log_some_info) {
            log.i("some info", null);
            return true;
        }
        else if(id == R.id.log_some_debug) {
            log.d("some debug", null);
            return true;
        }
        else if(id == R.id.log_some_verbose) {
            log.v("some verbose", null);
            return true;
        }
        return super.onItemSelected(nav, item);
    }

    private void updateCheckedItem() {
        switch(malog.getLogHistory().getFilterLevel()) {
            case ERROR:
                setCheckedItem(R.id.log_level_error);
                break;
            case WARN:
                setCheckedItem(R.id.log_level_warning);
                break;
            case INFO:
                setCheckedItem(R.id.log_level_info);
                break;
            case DEBUG:
                setCheckedItem(R.id.log_level_debug);
                break;
            case VERBOSE:
                setCheckedItem(R.id.log_level_verbose);
                break;
        }
    }

}
