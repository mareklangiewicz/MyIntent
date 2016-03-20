package pl.mareklangiewicz.myviews;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import pl.mareklangiewicz.myloggers.MyAndroLogger;
import pl.mareklangiewicz.myloggers.MyAndroLoggerKt;

public final class MyNavigationView extends NavigationView implements IMyUINavigation, NavigationView.OnNavigationItemSelectedListener {

    protected @NonNull final MyAndroLogger log = MyAndroLoggerKt.getMY_DEFAULT_ANDRO_LOGGER();

    private @Nullable View mHeader;

    private @Nullable Listener mListener;

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

    private void init(@Nullable AttributeSet attrs, int defStyle) {
        super.setNavigationItemSelectedListener(this);
    }

    public @Nullable View getHeader() {
        return mHeader;
    }

    public void inflateHeader(@LayoutRes int id) {
        mHeader = inflateHeaderView(id);
        if(mListener != null)
            mListener.onNavigationChanged(this);
    }

    @Override public boolean overlaps(@Nullable View view) {
        return MyViews.overlaps(this, view);
    }

    @Override
    public void inflateMenu(int resId) {
        super.inflateMenu(resId);
        if(mListener != null)
            mListener.onNavigationChanged(this);
    }

    /**
     * Finds (recursively: DFS) first checked item in navigation view menu.
     * WARNING: do not call it too soon!
     * it works correctly from Fragment.onResume.
     * it does NOT work correctly from Fragment.onViewStateRestored!!!
     *
     * @return A first checked menu item or null if there is none...
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

    @Override
    public void clearMenu() {
        getMenu().clear();
        if(mListener != null)
            mListener.onNavigationChanged(this);
    }

    @Override
    public void clearHeader() {
        if(mHeader == null)
            return;
        removeHeaderView(mHeader);
        mHeader = null;
        if(mListener != null)
            mListener.onNavigationChanged(this);
    }

    /**
     *
     * @param id The ID of checked item.
     * @param callback - if true: it additionally calls listener.onItemSelected (if any listener is provided)
     */
    @Override public void setCheckedItem(@IdRes int id, boolean callback) {
        super.setCheckedItem(id);
        if(callback)
            onNavigationItemSelected(getMenu().findItem(id));
    }

    public boolean isEmpty() {
        return getMenu().size() == 0 && mHeader == null;
    }

    @Override
    public @Nullable Listener getListener() {
        return mListener;
    }

    @Override
    public void setListener(@Nullable Listener listener) {
        mListener = listener;
    }

    @Override
    public void setNavigationItemSelectedListener(OnNavigationItemSelectedListener listener) {
        throw new IllegalAccessError("This method is blocked. Use setListener.");
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        return mListener != null && mListener.onItemSelected(this, menuItem);
    }

}
