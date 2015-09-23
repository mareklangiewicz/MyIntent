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

import com.noveogroup.android.log.MyLogger;

import pl.mareklangiewicz.myviews.IMyCommander;
import pl.mareklangiewicz.myviews.IMyNavigation;
import pl.mareklangiewicz.myviews.MyNavigationView;

import static pl.mareklangiewicz.myutils.MyTextUtils.toStr;

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
public class MyFragment extends Fragment implements IMyCommander, IMyNavigation, MyNavigationView.Listener, DrawerLayout.DrawerListener {

    static final boolean VERBOSE = true;
        //TODO LATER: implement it as a build time switch for user
    static final boolean VERY_VERBOSE = false;
        //TODO LATER: implement it as a build time switch for user

    protected @NonNull MyLogger log = MyLogger.sMyDefaultUILogger;

    public MyFragment() { }


    /**
     * Override it to your needs
     * @param nav Local or Global navigation.
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
        if(!(a instanceof IMyCommander))
            return null;
        return ((IMyCommander)a).getLocalNavigation();
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
        if(!(a instanceof IMyCommander))
            return null;
        return ((IMyCommander)a).getFAB();
    }

    public @Nullable IMyNavigation getGlobalNavigation() {
        Activity a = getActivity();
        if(!(a instanceof IMyCommander))
            return null;
        return ((IMyCommander)a).getGlobalNavigation();
    }

    private @NonNull IMyNavigation gln() {
        IMyNavigation n = getLocalNavigation();
        if(n == null) throw new IllegalStateException("No local navigation available.");
        return n;
    }

    @Override public @Nullable Menu     getMenu       (                 ) { return gln().getMenu(); }
    @Override public @Nullable View     getHeader     (                 ) { return gln().getHeader(); }
    @Override public           void     clearMenu     (                 ) {        gln().clearMenu(); }
    @Override public           void     clearHeader   (                 ) {        gln().clearHeader(); }
    @Override public           void     inflateMenu   (@MenuRes   int id) {        gln().inflateMenu(id); }
    @Override public           void     inflateHeader (@LayoutRes int id) {        gln().inflateHeader(id); }
    @Override public           void     setCheckedItem(@IdRes     int id) {        gln().setCheckedItem(id); }
    /** WARNING: see MyNavigationView.getFirstCheckedItem warning! */
    @Override public @Nullable MenuItem getFirstCheckedItem(            ) { return gln().getFirstCheckedItem(); }
    @Override public        boolean     isEmpty       (                 ) { return gln().isEmpty(); }
    @Override public       Listener     getListener   (                 ) { return gln().getListener(); }

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
        if(VERY_VERBOSE) log.v("%s.%s context=%s attrs=%s  state=%s)", this.getClass().getSimpleName(), "onInflate", toStr(context), toStr(attrs), toStr(savedInstanceState));
        super.onInflate(context, attrs, savedInstanceState);
    }

    @CallSuper
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(VERBOSE) log.v("%s.%s state=%s", this.getClass().getSimpleName(), "onCreate", toStr(savedInstanceState));
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @CallSuper
    @Override
    public void onAttach(Context context) {
        if(VERY_VERBOSE) log.v("%s.%s context=%s", this.getClass().getSimpleName(), "onAttach", toStr(context));
        super.onAttach(context);
    }

    @CallSuper
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if(VERY_VERBOSE) log.v("%s.%s state=%s", this.getClass().getSimpleName(), "onActivityCreated", toStr(savedInstanceState));
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(VERY_VERBOSE) log.v("%s.%s inflater=%s container=%s state=%s", this.getClass().getSimpleName(), "onCreateView", toStr(inflater), toStr(container), toStr(savedInstanceState));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @CallSuper
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if(VERY_VERBOSE) log.v("%s.%s view=%s state=%s", this.getClass().getSimpleName(), "onViewCreated", toStr(view), toStr(savedInstanceState));
        super.onViewCreated(view, savedInstanceState);
    }

    @CallSuper
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if(VERY_VERBOSE) log.v("%s.%s state=%s", this.getClass().getSimpleName(), "onViewStateRestored", toStr(savedInstanceState));
        super.onViewStateRestored(savedInstanceState);
    }

    @CallSuper
    @Override
    public void onStart() {
        if(VERBOSE) log.v("%s.%s", this.getClass().getSimpleName(), "onStart");
        super.onStart();
    }

    @CallSuper
    @Override
    public void onResume() {
        if(VERBOSE) log.v("%s.%s", this.getClass().getSimpleName(), "onResume");
        super.onResume();
    }

    @CallSuper
    @Override
    public void onPause() {
        if(VERBOSE) log.v("%s.%s", this.getClass().getSimpleName(), "onPause");
        super.onPause();
    }

    @CallSuper
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(VERY_VERBOSE) log.v("%s.%s outState=%s", this.getClass().getSimpleName(), "onSaveInstanceState", toStr(outState));
        super.onSaveInstanceState(outState);
    }

    @CallSuper
    @Override
    public void onStop() {
        if(VERBOSE) log.v("%s.%s", this.getClass().getSimpleName(), "onStop");
        super.onStop();
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        if(VERY_VERBOSE) log.v("%s.%s", this.getClass().getSimpleName(), "onDestroyView");
        super.onDestroyView();
        clearMenu();
        clearHeader();
    }

    @CallSuper
    @Override
    public void onDestroy() {
        if(VERBOSE) log.v("%s.%s", this.getClass().getSimpleName(), "onDestroy");
        super.onDestroy();
    }

    @CallSuper
    @Override
    public void onDetach() {
        if(VERY_VERBOSE) log.v("%s.%s", this.getClass().getSimpleName(), "onDetach");
        super.onDetach();
    }


}
