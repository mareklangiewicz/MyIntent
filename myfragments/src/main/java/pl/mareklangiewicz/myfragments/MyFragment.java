package pl.mareklangiewicz.myfragments;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.noveogroup.android.log.MyLogger;

import pl.mareklangiewicz.myviews.IMyCommander;
import pl.mareklangiewicz.myviews.IMyNavigation;

import static pl.mareklangiewicz.myutils.MyTextUtils.*;

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
public class MyFragment extends Fragment implements IMyNavigation, NavigationView.OnNavigationItemSelectedListener {

    static final boolean VERBOSE = true;
        //TODO LATER: implement it as a build time switch for user
    static final boolean VERY_VERBOSE = false;
        //TODO LATER: implement it as a build time switch for user

    protected MyLogger log = MyLogger.sMyDefaultUILogger;

    public MyFragment() { }


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
    public final void clearMenu() {
        IMyNavigation nav = ((IMyCommander) getActivity()).getLocalNavigation();
        nav.clearMenu();
    }

    @Override
    public final void clearHeader() {
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






    // Below I only add some verbose logging to lifecycle fragment methods.

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        if(VERY_VERBOSE) log.v("%s.%s(%s, %s, %s)", this.getClass().getSimpleName(), "onInflate", toStr(context), toStr(attrs), toStr(savedInstanceState));
        super.onInflate(context, attrs, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(VERBOSE) log.v("%s.%s(%s)", this.getClass().getSimpleName(), "onCreate", toStr(savedInstanceState));
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        if(VERBOSE) log.v("%s.%s(%s)", this.getClass().getSimpleName(), "onAttach", toStr(context));
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if(VERBOSE) log.v("%s.%s(%s)", this.getClass().getSimpleName(), "onActivityCreated", toStr(savedInstanceState));
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(VERBOSE) log.v("%s.%s(%s, %s, %s)", this.getClass().getSimpleName(), "onCreateView", toStr(inflater), toStr(container), toStr(savedInstanceState));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if(VERY_VERBOSE) log.v("%s.%s(%s, %s)", this.getClass().getSimpleName(), "onViewCreated", toStr(view), toStr(savedInstanceState));
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if(VERY_VERBOSE) log.v("%s.%s(%s)", this.getClass().getSimpleName(), "onViewStateRestored", toStr(savedInstanceState));
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        if(VERBOSE) log.v("%s.%s()", this.getClass().getSimpleName(), "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        if(VERBOSE) log.v("%s.%s()", this.getClass().getSimpleName(), "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        if(VERBOSE) log.v("%s.%s()", this.getClass().getSimpleName(), "onPause");
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(VERBOSE) log.v("%s.%s(%s)", this.getClass().getSimpleName(), "onSaveInstanceState", toStr(outState));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        if(VERBOSE) log.v("%s.%s()", this.getClass().getSimpleName(), "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if(VERBOSE) log.v("%s.%s()", this.getClass().getSimpleName(), "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if(VERBOSE) log.v("%s.%s()", this.getClass().getSimpleName(), "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        if(VERBOSE) log.v("%s.%s()", this.getClass().getSimpleName(), "onDetach");
        super.onDetach();
    }



}
