package pl.mareklangiewicz.myfragments;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.noveogroup.android.log.MyLogger;

import pl.mareklangiewicz.myviews.IMyCommander;
import pl.mareklangiewicz.myviews.IMyNavigation;

public class MyFragment extends Fragment implements IMyNavigation {

    protected MyLogger log = MyLogger.sMyDefaultUILogger;

    public MyFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        log.v("%s.%s", this.getClass().getSimpleName(), "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        log.v("%s.%s", this.getClass().getSimpleName(), "onDestroy");
        super.onDestroy();
    }

    /**
     * Override it to your needs
     * @param item Selected menu item from global or local menu.
     * @return True if item was successfully selected.
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Nullable
    @Override
    public final Menu getMenu() {
        IMyNavigation nav = ((IMyCommander) getActivity()).getLocalNavigation();
        return nav.getMenu();
    }

    @Nullable
    @Override
    public final View getHeader() {
        IMyNavigation nav = ((IMyCommander) getActivity()).getLocalNavigation();
        return nav.getHeader();
    }

    @Override
    public void clearMenu() {
        IMyNavigation nav = ((IMyCommander) getActivity()).getLocalNavigation();
        nav.clearMenu();
    }

    @Override
    public void clearHeader() {
        IMyNavigation nav = ((IMyCommander) getActivity()).getLocalNavigation();
        nav.clearHeader();
    }

    @Override
    public final void inflateMenu(@MenuRes int id) {
        IMyNavigation nav = ((IMyCommander) getActivity()).getLocalNavigation();
        nav.inflateMenu(id);
    }

    @Override
    public final void inflateHeader(@LayoutRes int id) {
        IMyNavigation nav = ((IMyCommander) getActivity()).getLocalNavigation();
        nav.inflateHeader(id);
    }

    @Override
    public final boolean selectMenuItem(@IdRes int id) {
        IMyNavigation nav = ((IMyCommander) getActivity()).getLocalNavigation();
        return nav.selectMenuItem(id);
    }

}
