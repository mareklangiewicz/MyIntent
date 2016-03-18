package pl.mareklangiewicz.myfragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import pl.mareklangiewicz.myloggers.MyAndroLogger;
import pl.mareklangiewicz.myloggers.MyAndroLoggerKt;
import pl.mareklangiewicz.myviews.IMyManager;
import pl.mareklangiewicz.myviews.IMyNavigation;

import static pl.mareklangiewicz.myutils.MyTextUtilsKt.*;

/**
 * This is my base class for common fragments.
 * I make some decisions here so I don't have to take care of those details
 * in every fragment class. If you need to make other choices - don't use it.
 * WARNING: I invoke: setRetainInstance(true) here in onCreate.
 * So remember to deal with this type of fragment lifecycle correctly, especially:
 * - nullify any reference tied to the activity/context and views (to avoid mem leaks)
 * - don't add this fragment transactions to back stack
 * or invoke setRetainInstance(false) after MyFragment.onCreate.
 */
public class MyFragment extends Fragment implements IMyManager, IMyNavigation, IMyNavigation.Listener, DrawerLayout.DrawerListener {

    /*
        private static final boolean V = BuildConfig.VERBOSE;
        private static final boolean VV = BuildConfig.VERY_VERBOSE;

        FIXME SOMEDAY: enable version with BuildConfig when Google fix issue with propagating build types to libraries.
        Now it is always 'release' in libraries.. see:
        https://code.google.com/p/android/issues/detail?id=52962
        http://stackoverflow.com/questions/20176284/buildconfig-debug-always-false-when-building-library-projects-with-gradle
        http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Library-Publication
    */
    private static final boolean V = true;
    private static final boolean VV = false;

    protected @NonNull final MyAndroLogger log = MyAndroLoggerKt.getMY_DEFAULT_ANDRO_LOGGER();


    // it will be changed to true at 'onViewCreated' callback.
    // kotlin extensions are working from this point on.
    protected boolean isViewCreated = false;


    // it will be changed to true at 'onDestroyView' callback.
    protected boolean isViewDestroyed = false;

    public MyFragment() { }


    /**
     * Override it to your needs
     *
     * @param nav  Local or Global navigation.
     * @param item Selected menu item from global or local menu.
     * @return True if item was successfully selected.
     */
    @Override
    public boolean onItemSelected(IMyNavigation nav, MenuItem item) {
        return false;
    }

    @Override public void onClearHeader(IMyNavigation nav) { }

    @Override public void onClearMenu(IMyNavigation nav) { }

    @Override public void onInflateHeader(IMyNavigation nav) { }

    @Override public void onInflateMenu(IMyNavigation nav) { }

    public @Nullable IMyNavigation getLocalNavigation() {
        Activity a = getActivity();
        if(!(a instanceof IMyManager))
            return null;
        return ((IMyManager) a).getLocalNavigation();
    }

    @NonNull
    @Override
    public CharSequence getTitle() {
        Activity a = getActivity();
        return a.getTitle();
    }

    @Override
    public void setTitle(@NonNull CharSequence title) {
        Activity a = getActivity();
        a.setTitle(title);
    }


    @Override
    public @Nullable FloatingActionButton getFAB() {
        Activity a = getActivity();
        if(!(a instanceof IMyManager))
            return null;
        return ((IMyManager) a).getFAB();
    }

    public @Nullable IMyNavigation getGlobalNavigation() {
        Activity a = getActivity();
        if(!(a instanceof IMyManager))
            return null;
        return ((IMyManager) a).getGlobalNavigation();
    }

    private @NonNull IMyNavigation gln() {
        IMyNavigation n = getLocalNavigation();
        if(n == null)
            throw new IllegalStateException("No local navigation available.");
        return n;
    }

    @Override public @Nullable Menu getMenu() { return gln().getMenu(); }

    @Override public @Nullable View getHeader() { return gln().getHeader(); }

    @Override public void clearMenu() { gln().clearMenu(); }

    @Override public void clearHeader() { gln().clearHeader(); }

    @Override public void inflateMenu(@MenuRes int id) { gln().inflateMenu(id); }

    @Override public void inflateHeader(@LayoutRes int id) { gln().inflateHeader(id); }

    @Override public void setCheckedItem(@IdRes int id) { gln().setCheckedItem(id); }

    @Override public boolean overlaps(@Nullable View view) { return gln().overlaps(view); }

    /**
     * WARNING: see MyNavigationView.getFirstCheckedItem warning!
     */
    @Override public @Nullable MenuItem getFirstCheckedItem() { return gln().getFirstCheckedItem(); }

    @Override public boolean isEmpty() { return gln().isEmpty(); }

    @Override public Listener getListener() { return gln().getListener(); }

    @Override public void setListener(@Nullable Listener listener) {
        throw new IllegalStateException("MyFragments can not change navigation listeners.");
    }


    public void selectItem(@IdRes int id) {
        Menu menu = getMenu();
        if(menu == null) {
            log.e("menu is null!");
            return;
        }
        setCheckedItem(id);
        onItemSelected(gln(), menu.findItem(id));
    }

    // These will be called by MyActivity for both global and local drawer events...

    @Override public void onDrawerSlide(View drawerView, float slideOffset) { }

    @Override public void onDrawerOpened(View drawerView) { }

    @Override public void onDrawerClosed(View drawerView) { }

    @Override public void onDrawerStateChanged(int newState) { }


    @CallSuper
    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        if(VV)
            log.v(String.format("%s.%s context=%s attrs=%s  state=%s)", this.getClass().getSimpleName(), "onInflate", getStr(context), getStr(attrs),
                    getStr(savedInstanceState)));
        super.onInflate(context, attrs, savedInstanceState);
    }

    @CallSuper
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(V)
            log.v(String.format("%s.%s state=%s args=%s", this.getClass().getSimpleName(), "onCreate", getStr(savedInstanceState), getStr(getArguments())));
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @CallSuper
    @Override
    public void onAttach(Context context) {
        if(VV)
            log.v(String.format("%s.%s context=%s", this.getClass().getSimpleName(), "onAttach", getStr(context)));
        super.onAttach(context);
    }

    @CallSuper
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if(VV)
            log.v(String.format("%s.%s state=%s", this.getClass().getSimpleName(), "onActivityCreated", getStr(savedInstanceState)));
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(VV)
            log.v(String.format("%s.%s inflater=%s container=%s state=%s", this.getClass().getSimpleName(), "onCreateView", getStr(inflater), getStr(container),
                    getStr(savedInstanceState)));
        return null;
    }

    @CallSuper
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        isViewCreated = true;
        if(VV)
            log.v(String.format("%s.%s view=%s state=%s", this.getClass().getSimpleName(), "onViewCreated", getStr(view), getStr(savedInstanceState)));
        super.onViewCreated(view, savedInstanceState);
    }

    @CallSuper
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if(VV)
            log.v(String.format("%s.%s state=%s", this.getClass().getSimpleName(), "onViewStateRestored", getStr(savedInstanceState)));
        super.onViewStateRestored(savedInstanceState);
    }

    @CallSuper
    @Override
    public void onStart() {
        if(V)
            log.v(String.format("%s.%s", this.getClass().getSimpleName(), "onStart"));
        super.onStart();
    }

    @CallSuper
    @Override
    public void onResume() {
        if(V)
            log.v(String.format("%s.%s", this.getClass().getSimpleName(), "onResume"));
        super.onResume();
    }

    @CallSuper
    @Override
    public void onPause() {
        if(V)
            log.v(String.format("%s.%s", this.getClass().getSimpleName(), "onPause"));
        super.onPause();
    }

    @CallSuper
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(VV)
            log.v(String.format("%s.%s outState=%s", this.getClass().getSimpleName(), "onSaveInstanceState", getStr(outState)));
        super.onSaveInstanceState(outState);
    }

    @CallSuper
    @Override
    public void onStop() {
        if(V)
            log.v(String.format("%s.%s", this.getClass().getSimpleName(), "onStop"));
        super.onStop();
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        if(VV)
            log.v(String.format("%s.%s", this.getClass().getSimpleName(), "onDestroyView"));
        super.onDestroyView();
        clearMenu();
        clearHeader();
        isViewDestroyed = true;
    }

    @CallSuper
    @Override
    public void onDestroy() {
        if(V)
            log.v(String.format("%s.%s", this.getClass().getSimpleName(), "onDestroy"));
        super.onDestroy();
    }

    @CallSuper
    @Override
    public void onDetach() {
        if(VV)
            log.v(String.format("%s.%s", this.getClass().getSimpleName(), "onDetach"));
        super.onDetach();
    }

}
