package pl.mareklangiewicz.myviews;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.View;

/**
 * Created by marek on 02.09.15.
 */
public interface IMyNavigation extends  NavigationView.OnNavigationItemSelectedListener {

    @Nullable Menu getMenu();
    @Nullable View getHeader();

    void clearMenu();
    void clearHeader();

    void inflateMenu(@MenuRes int id);
    void inflateHeader(@LayoutRes int id);

    boolean selectMenuItem(@IdRes int id);

}
