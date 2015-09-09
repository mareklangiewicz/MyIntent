package pl.mareklangiewicz.myfragments;


import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import pl.mareklangiewicz.myutils.MyMathUtils;
import pl.mareklangiewicz.myviews.MyPie;


public final class MyPieTestsFragment extends MyFragment implements View.OnClickListener, DrawerLayout.DrawerListener {

    private MyPie mMyPie1;
    private MyPie mMyPie2;
    private MyPie mMyPie3;
    private MyPie mMyPie4;
    private MyPie mMyHeaderPie;
    private ObjectAnimator mHeaderAnimator;

    public MyPieTestsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.my_pie_tests_fragment, container, false);


        mMyPie1 = (MyPie) root.findViewById(R.id.pie1);
        mMyPie2 = (MyPie) root.findViewById(R.id.pie2);
        mMyPie3 = (MyPie) root.findViewById(R.id.pie3);
        mMyPie4 = (MyPie) root.findViewById(R.id.pie4);

        mMyPie1.setOnClickListener(this);
        mMyPie2.setOnClickListener(this);
        mMyPie3.setOnClickListener(this);
        mMyPie4.setOnClickListener(this);

        inflateHeader(R.layout.my_pie_tests_header);

        mMyHeaderPie = (MyPie) getHeader().findViewById(R.id.header_pie);

        float min = mMyHeaderPie.getMinimum();
        float max = mMyHeaderPie.getMaximum();

        PropertyValuesHolder pvh1 = PropertyValuesHolder.ofFloat("to", min, max);
        PropertyValuesHolder pvh2 = PropertyValuesHolder.ofFloat("from", min, max - (max-min)/2);

        mHeaderAnimator = ObjectAnimator.ofPropertyValuesHolder(mMyHeaderPie, pvh1, pvh2);

        mHeaderAnimator.setInterpolator(new AccelerateInterpolator());

        return root;
    }


    @Override
    public void onClick(View v) {
        if( v instanceof MyPie) {
            MyPie pie = (MyPie) v;
            float to = MyMathUtils.getRandomFloat(pie.getFrom(), pie.getMaximum());
            ObjectAnimator.ofFloat(pie, "to", to).start();
            log.i("[SNACK]MyPie:to = %f", to);
        }
    }

    @Override
    public void onDrawerSlide(View view, float slideOffset) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            mHeaderAnimator.setCurrentFraction(slideOffset);
        }
    }

    @Override public void onDrawerOpened(View view) { }
    @Override public void onDrawerClosed(View view) { }
    @Override public void onDrawerStateChanged(int i) { }
}
