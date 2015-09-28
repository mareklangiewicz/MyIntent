package pl.mareklangiewicz.myfragments;


import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import pl.mareklangiewicz.myutils.MyMathUtils;
import pl.mareklangiewicz.myviews.IMyNavigation;
import pl.mareklangiewicz.myviews.MyPie;

//TODO LATER: create UI to change animation speed (in local menu header)

public final class MyPieTestsFragment extends MyFragment implements View.OnClickListener {

    private @NonNull String mRandomize = "to";

    private @Nullable ObjectAnimator mHeaderAnimator;

    public MyPieTestsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.my_pie_tests_fragment, container, false);

        inflateHeader(R.layout.my_pie_tests_header);
        inflateMenu(R.menu.my_pie_tests_menu);

        root.findViewById(R.id.pie1).setOnClickListener(this);
        root.findViewById(R.id.pie2).setOnClickListener(this);
        root.findViewById(R.id.pie3).setOnClickListener(this);
        root.findViewById(R.id.pie4).setOnClickListener(this);

        //noinspection ConstantConditions
        MyPie hpie = (MyPie) getHeader().findViewById(R.id.header_pie);

        float min = hpie.getMinimum();
        float max = hpie.getMaximum();

        PropertyValuesHolder pvh1 = PropertyValuesHolder.ofFloat("to", min, max);
        PropertyValuesHolder pvh2 = PropertyValuesHolder.ofFloat("from", min, max - (max - min) / 2);

        mHeaderAnimator = ObjectAnimator.ofPropertyValuesHolder(hpie, pvh1, pvh2);

        mHeaderAnimator.setInterpolator(new AccelerateInterpolator());

        if(savedInstanceState == null)
            selectItem(R.id.mpt_randomize_to);

        return root;
    }


    @Override
    public void onDestroyView() {
        mHeaderAnimator = null;
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        MenuItem item = getFirstCheckedItem(); //see MyNavigationView.getFirstCheckedItem warning..

        if(item == null) {
            log.e("No item selected");
            mRandomize = "to";
        }
        else
            mRandomize = item.getTitle().toString();
    }

    @Override
    public boolean onItemSelected(IMyNavigation nav, MenuItem item) {
        if(nav != getLocalNavigation()) {
            log.v("This item is not from our local menu.");
            return false;
        }
        mRandomize = item.getTitle().toString();
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v instanceof MyPie) {
            MyPie pie = (MyPie) v;
            if(mRandomize.equals("pieColor") || mRandomize.equals("ovalColor")) {
                @ColorInt int value = MyMathUtils.getRandomColor(Color.rgb(0, 0, 0), Color.rgb(255, 255, 255));
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ObjectAnimator.ofArgb(pie, mRandomize, value).start();
                    log.i("[SNACK]MyPie:%s: random color is ... %X %X %X", mRandomize, Color.red(value), Color.green(value), Color.blue(value));
                }
                else
                    log.w("[SNACK]Color animation is not supported on platforms before Lollipop");
            }
            else {
                float min = 0;
                float max = 100;
                switch(mRandomize) {
                    case "from":
                        min = pie.getMinimum();
                        max = pie.getTo();
                        break;
                    case "to":
                        min = pie.getFrom();
                        max = pie.getMaximum();
                        break;
                    case "minimum":
                        max = pie.getFrom();
                        break;
                    case "maximum":
                        min = pie.getTo();
                        break;
                }

                float value = MyMathUtils.getRandomFloat(min, max);
                ObjectAnimator.ofFloat(pie, mRandomize, value).start();
                log.i("[SNACK]MyPie:%s: random %.2f..%.2f is ... %.2f", mRandomize, min, max, value);
            }
        }
    }


    @Override
    public void onDrawerSlide(View view, float slideOffset) {
        if(view != getLocalNavigation())
            return;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if(mHeaderAnimator != null)
                mHeaderAnimator.setCurrentFraction(slideOffset);
        }
    }

    @Override public void onDrawerOpened(View view) { }

    @Override public void onDrawerClosed(View view) { }

    @Override public void onDrawerStateChanged(int i) { }
}
