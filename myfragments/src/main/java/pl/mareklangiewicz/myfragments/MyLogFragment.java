package pl.mareklangiewicz.myfragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import pl.mareklangiewicz.myloggers.MyAndroLogAdapter;
import pl.mareklangiewicz.myutils.MyLogLevel;
import pl.mareklangiewicz.myviews.IMyNavigation;

/**
 * MyFragment showing MyAndroidLogger messages.
 */
public final class MyLogFragment extends MyFragment {

    private final @NonNull MyAndroLogAdapter adapter = new MyAndroLogAdapter(log.getHistory());

    Function1<Unit, Unit> sub = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState); //just for logging

        sub = log.getHistory().getChanges().invoke(new Function1<Unit, Unit>() {
            @Override public Unit invoke(Unit unit) {
                adapter.notifyDataSetChanged();
                return null;
            }
        });
        adapter.notifyDataSetChanged(); // to make sure we are up to date

        View rootView = inflater.inflate(R.layout.mf_my_log_fragment, container, false);
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.my_log_recycler_view);
        rv.setAdapter(adapter);
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
        if(sub != null) {
            sub.invoke(Unit.INSTANCE);
            sub = null;
        }
        super.onDestroyView();
    }

    @Override
    public boolean onItemSelected(IMyNavigation nav, MenuItem item) {
        @IdRes int id = item.getItemId();
        if(id == R.id.log_level_error) {
            log.getHistory().setLevel(MyLogLevel.ERROR);
            return true;
        }
        else if(id == R.id.log_level_warning) {
            log.getHistory().setLevel(MyLogLevel.WARN);
            return true;
        }
        else if(id == R.id.log_level_info) {
            log.getHistory().setLevel(MyLogLevel.INFO);
            return true;
        }
        else if(id == R.id.log_level_debug) {
            log.getHistory().setLevel(MyLogLevel.DEBUG);
            return true;
        }
        else if(id == R.id.log_level_verbose) {
            log.getHistory().setLevel(MyLogLevel.VERBOSE);
            return true;
        }
        else if(id == R.id.clear_log_history) {
            log.getHistory().clear();
            return true;
        }
        else if(id == R.id.log_some_assert) {
            log.a("some assert");
            return true;
        }
        else if(id == R.id.log_some_error) {
            log.e("some error");
            return true;
        }
        else if(id == R.id.log_some_warning) {
            log.w("some warning");
            return true;
        }
        else if(id == R.id.log_some_info) {
            log.i("some info");
            return true;
        }
        else if(id == R.id.log_some_debug) {
            log.d("some debug");
            return true;
        }
        else if(id == R.id.log_some_verbose) {
            log.v("some verbose");
            return true;
        }
        return super.onItemSelected(nav, item);
    }

    private void updateCheckedItem() {
        switch(log.getHistory().getLevel()) {
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
