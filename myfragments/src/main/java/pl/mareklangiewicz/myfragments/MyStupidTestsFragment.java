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

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import pl.mareklangiewicz.myloggers.MyAndroLogView;

/**
 * Created by Marek Langiewicz on 10.09.15.
 * A Place to test some stupid ideas fast.
 * A kind of scratchpad.
 */


public class MyStupidTestsFragment extends MyFragment implements DrawerLayout.DrawerListener {

    CardView mWarningCardView;
    MyAndroLogView mMyAndroLogView;

    Function1<Unit, Unit> sub = null;

    public MyStupidTestsFragment() { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState); //just for logging

        View root = inflater.inflate(R.layout.mf_my_stupid_tests_fragment, container, false);

        mWarningCardView = (CardView) root.findViewById(R.id.stupid_warning);

        mMyAndroLogView = (MyAndroLogView) root.findViewById(R.id.my_stupid_log_simple_view);

        mMyAndroLogView.setArray(log.getHistory());

        sub = log.getHistory().getChanges().invoke(new Function1<Unit, Unit>() {
            @Override public Unit invoke(Unit unit) {
                mMyAndroLogView.invalidate();
                return null;
            }
        });

        final NavigationView nv = (NavigationView) root.findViewById(R.id.stupid_navigation_view);

        nv.inflateMenu(R.menu.mf_my_stupid_tests);

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                log.i(String.format("[SNACK]%s", menuItem.getTitle()));
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
        if(sub != null) {
            sub.invoke(Unit.INSTANCE);
            sub = null;
        }
        mWarningCardView = null;
        super.onDestroyView();
    }
}
