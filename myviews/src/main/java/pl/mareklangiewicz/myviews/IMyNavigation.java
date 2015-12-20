package pl.mareklangiewicz.myviews;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by Marek Langiewicz on 02.09.15.
 * An interface usually implemented by our navigation views and used by fragments.
 */
public interface IMyNavigation {

    @Nullable Menu getMenu();

    @Nullable View getHeader();

    void clearMenu();

    void clearHeader();

    void inflateMenu(@MenuRes int id);

    void inflateHeader(@LayoutRes int id);

    void setCheckedItem(@IdRes int id);

    boolean overlaps(@Nullable View view);

    /**
     * returns first checked item
     * WARNING: see MyNavigationView.getFirstCheckedItem warning!
     */
    @Nullable MenuItem getFirstCheckedItem();

    boolean isEmpty();

    @Nullable Listener getListener();

    void setListener(@Nullable Listener listener);

    interface Listener {
        boolean onItemSelected(IMyNavigation nav, MenuItem item);

        void onClearHeader(IMyNavigation nav);

        void onClearMenu(IMyNavigation nav);

        void onInflateHeader(IMyNavigation nav);

        void onInflateMenu(IMyNavigation nav);
    }

}
