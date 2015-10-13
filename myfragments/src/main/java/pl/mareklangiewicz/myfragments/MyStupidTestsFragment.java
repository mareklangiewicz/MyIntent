package pl.mareklangiewicz.myfragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import pl.mareklangiewicz.myloggers.MyLogSimpleView;

/**
 * Created by Marek Langiewicz on 10.09.15.
 * A Place to test some stupid ideas fast.
 * A kind of scratchpad.
 */


public class MyStupidTestsFragment extends MyFragment implements DrawerLayout.DrawerListener {

    CardView mWarningCardView;
    MyLogSimpleView mMyLogSimpleView;

    public MyStupidTestsFragment() { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState); //just for logging

        View root = inflater.inflate(R.layout.my_stupid_tests_fragment, container, false);

        mWarningCardView = (CardView) root.findViewById(R.id.stupid_warning);

        mMyLogSimpleView = (MyLogSimpleView) root.findViewById(R.id.my_stupid_log_simple_view);

        mMyLogSimpleView.setLog(log);

        final NavigationView nv = (NavigationView) root.findViewById(R.id.stupid_navigation_view);

        nv.inflateMenu(R.menu.my_stupid_tests_menu);

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                log.i("[SNACK]%s", menuItem.getTitle());
                return true;
            }
        });

        if(savedInstanceState == null)
            nv.setCheckedItem(R.id.stupid_item_2);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        ObjectAnimator animator = ObjectAnimator.ofFloat(mWarningCardView, View.TRANSLATION_Y, 8f);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(5);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();

    }

    @Override public void onDestroyView() {
        mWarningCardView = null;
        mMyLogSimpleView.setLog(null);
        mMyLogSimpleView = null;
        super.onDestroyView();
    }
}
