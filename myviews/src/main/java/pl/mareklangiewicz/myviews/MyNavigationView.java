package pl.mareklangiewicz.myviews;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.noveogroup.android.log.MyLogger;

import java.util.ArrayList;
import java.util.List;

public final class MyNavigationView extends NavigationView implements IMyNavigation {

    protected final MyLogger log = MyLogger.sMyDefaultUILogger;

    private @Nullable View mHeader;

    public MyNavigationView(Context context) {
        super(context);
        init(null, 0);
    }

    public MyNavigationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MyNavigationView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(@Nullable AttributeSet attrs, int defStyle) {
    }


    public @Nullable View getHeader() {
        return mHeader;
    }

    public void inflateHeader(@LayoutRes int id) {
        mHeader = inflateHeaderView(id);
    }


    /**
     * Finds (recursively: DFS) first checked item in navigation view menu.
     * WARNING: do not call it too soon!
     * it works correctly from Fragment.onResume.
     * it does NOT work correctly from Fragment.onViewStateRestored!!!
     * @return
     */
    @Override
    public @Nullable MenuItem getFirstCheckedItem() {
        Menu menu = getMenu();
        if(menu == null) {
            log.w("Menu is null");
            return null;
        }
        return getFirstCheckedItem(menu);
    }


    static private @Nullable MenuItem getFirstCheckedItem(@NonNull Menu menu) {
        int size = menu.size();
        for(int i = 0; i < size; ++i) {
            MenuItem item = menu.getItem(i);
            if(item.isChecked())
                return item;
            if(item.hasSubMenu()) {
                Menu submenu = item.getSubMenu();
                if(submenu != null) {
                    MenuItem fci = getFirstCheckedItem(submenu);
                    if(fci != null)
                        return fci;
                }
            }
        }
        return null;
    }


    @Override
    public void clearMenu() {
        getMenu().clear();
    }

    @Override
    public void clearHeader() {
        if(mHeader == null)
            return;
        removeHeaderView(mHeader);
        mHeader = null;
    }

}
