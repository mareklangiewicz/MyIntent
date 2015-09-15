package pl.mareklangiewicz.myactivities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Test activity presenting most of the MyBlocks functionality
 */

// TODO: set lint to fatal and remove all problems before putting MyBlocks on github
    
public final class MyTestActivity extends pl.mareklangiewicz.myactivities.MyActivity implements DrawerLayout.DrawerListener {

    private @Nullable ObjectAnimator mHeaderAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getGlobalNavigation().inflateMenu(R.menu.my_test_global_menu);
        getGlobalNavigation().inflateHeader(R.layout.my_test_global_header);

        //noinspection ConstantConditions
        View target = getGlobalNavigation().getHeader().findViewById(R.id.my_home_page_text_view);

        PropertyValuesHolder pvha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 0f, 1f);
        PropertyValuesHolder pvhy = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, -50f, -50f, 0f);

        mHeaderAnimator = ObjectAnimator.ofPropertyValuesHolder(target, pvha, pvhy);
        mHeaderAnimator.setInterpolator(new LinearInterpolator());

        if(savedInstanceState == null)
            selectGlobalItem(R.id.section_my_pie_tests);
    }


    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if(mHeaderAnimator != null)
                mHeaderAnimator.setCurrentFraction(slideOffset);
        }
    }

    @Override public void onDrawerOpened(View drawerView) { }
    @Override public void onDrawerClosed(View drawerView) { }
    @Override public void onDrawerStateChanged(int newState) { }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        boolean done = super.onNavigationItemSelected(item);

        if(done)
            return true;

        @IdRes int id = item.getItemId();

        if     (id == R.id.action_whats_up         ) { log.i("[SNACK][SHORT]What's up mate?"); return true; }
        else if(id == R.id.action_settings         ) { log.w("[SNACK]TODO: some settings (or not).."); return true; }
        else if(id == R.id.action_destroy_something) { log.a("BUM!"); throw new InternalError(); }

        return false;
    }
}
