package pl.mareklangiewicz.myfragments;


import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import pl.mareklangiewicz.myviews.MyViewDecorator;


public final class MyDecoratorsTestsFragment extends MyFragment {

    public MyDecoratorsTestsFragment() {
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState); //just for logging

        inflateHeader(R.layout.mf_my_basic_header);
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.mf_my_decorators_tests_fragment, container, false);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mf_my_decorators, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        @IdRes int id = item.getItemId();
        if(id == R.id.action_decorate_views) {
            View view = getView();
            if(view == null) {
                log.e("The root view of this fragment is null.");
                return false;
            }
            MyViewDecorator.decorateTree(view, "decorate", R.layout.mv_example_decoration, null);
            return true;
        }
        return false;
    }
}
