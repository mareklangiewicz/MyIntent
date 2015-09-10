package pl.mareklangiewicz.myfragments;


import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import pl.mareklangiewicz.myutils.MyMathUtils;
import pl.mareklangiewicz.myviews.MyPie;


public final class MyPieTestsFragment extends MyFragment implements View.OnClickListener, DrawerLayout.DrawerListener {

    private @NonNull String mRandomize = "to";

    private @Nullable ObjectAnimator mHeaderAnimator;

    public MyPieTestsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.my_pie_tests_fragment, container, false);


        ((MyPie) root.findViewById(R.id.pie1)).setOnClickListener(this);
        ((MyPie) root.findViewById(R.id.pie2)).setOnClickListener(this);
        ((MyPie) root.findViewById(R.id.pie3)).setOnClickListener(this);
        ((MyPie) root.findViewById(R.id.pie4)).setOnClickListener(this);

        inflateHeader(R.layout.my_pie_tests_header);
        inflateMenu(R.menu.my_pie_tests_menu);


        MyPie hpie = (MyPie) getHeader().findViewById(R.id.header_pie);

        float min = hpie.getMinimum();
        float max = hpie.getMaximum();

        PropertyValuesHolder pvh1 = PropertyValuesHolder.ofFloat("to", min, max);
        PropertyValuesHolder pvh2 = PropertyValuesHolder.ofFloat("from", min, max - (max-min)/2);

        mHeaderAnimator = ObjectAnimator.ofPropertyValuesHolder(hpie, pvh1, pvh2);

        mHeaderAnimator.setInterpolator(new AccelerateInterpolator());

        if(savedInstanceState == null)
            selectMenuItem(R.id.mpt_randomize_to);

        return root;
    }

    @Override
    public void onDestroyView() {
        mHeaderAnimator = null;
        super.onDestroyView();
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        @IdRes int id = item.getItemId();
        if (getMenu().findItem(id) == null)
            return false; // it is item from global menu - we don't handle it.
        mRandomize = item.getTitle().toString();
        return true;
    }

    @Override
    public void onClick(View v) {
        if( v instanceof MyPie) {
            MyPie pie = (MyPie) v;
            if(mRandomize.equals("pieColor") || mRandomize.equals("ovalColor")) {
                @ColorInt int value = MyMathUtils.getRandomColor(Color.rgb(0,0,0), Color.rgb(255, 255, 255));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ObjectAnimator.ofArgb(pie, mRandomize, value).start();
                    log.i("[SNACK]MyPie:%s = rgb(%x,%x,%x)", mRandomize, Color.red(value), Color.green(value), Color.blue(value));
                }
                else
                    log.w("[SNACK]Color animation is not supported on platforms before Lollipop");
            }
            else {
                float min = 0;
                float max = 100;
                if(mRandomize.equals("from")) {
                    min = pie.getMinimum();
                    max = pie.getTo();
                }
                else if(mRandomize.equals("to")) {
                    min = pie.getFrom();
                    max = pie.getMaximum();
                }
                else if(mRandomize.equals("minimum")) {
                    max = pie.getFrom();
                }
                else if(mRandomize.equals("maximum")) {
                    min = pie.getTo();
                }

                float value = MyMathUtils.getRandomFloat(min, max);
                ObjectAnimator.ofFloat(pie, mRandomize, value).start();
                log.i("[SNACK]MyPie:%s   random(%.2f,%.2f) is ..... %.2f", mRandomize, min, max, value);
            }
        }
    }








    @Override
    public void onDrawerSlide(View view, float slideOffset) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if(mHeaderAnimator != null)
                mHeaderAnimator.setCurrentFraction(slideOffset);
        }
    }

    @Override public void onDrawerOpened(View view) { }
    @Override public void onDrawerClosed(View view) { }
    @Override public void onDrawerStateChanged(int i) { }
}
