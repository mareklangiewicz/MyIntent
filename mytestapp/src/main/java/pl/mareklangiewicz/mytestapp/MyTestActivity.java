package pl.mareklangiewicz.mytestapp;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by marek on 23.07.15.
 */

// TODO: set lint to fatal and remove all problems before putting MyBlocks on github
    
public final class MyTestActivity extends pl.mareklangiewicz.myactivities.MyActivity {
    private ObjectAnimator mHeaderAnimator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGlobalNavigation.inflateMenu(R.menu.my_test_global_menu);
        mGlobalNavigation.inflateHeader(R.layout.my_test_global_header);

        View target = mGlobalNavigation.getHeader().findViewById(R.id.my_home_page_text_view);

        PropertyValuesHolder pvha = PropertyValuesHolder.ofFloat("alpha", 0f, 0f, 1f);
        PropertyValuesHolder pvhy = PropertyValuesHolder.ofFloat("translationY", -50f, -50f, 0f);

        mHeaderAnimator = ObjectAnimator.ofPropertyValuesHolder(target, pvha, pvhy);
        mHeaderAnimator.setInterpolator(new LinearInterpolator());

        mGlobalDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    mHeaderAnimator.setCurrentFraction(slideOffset);
                }
            }
        });

        if(savedInstanceState == null)
            mGlobalNavigation.selectMenuItem(R.id.section_my_pie_tests);
    }
}
