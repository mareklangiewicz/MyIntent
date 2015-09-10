package pl.mareklangiewicz.myfragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

/**
 * Created by marek on 10.09.15.
 */
public class MyStupidTestsFragment extends MyFragment {

    CardView mWarningCardView;

    public MyStupidTestsFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.my_stupid_tests_fragment, container, false);

        mWarningCardView = (CardView) root.findViewById(R.id.stupid_warning);

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
        ObjectAnimator animator = ObjectAnimator.ofFloat(mWarningCardView, "cardElevation", 4f, 16f, 4f, 16f, 4f, 16f, 4f);
        animator.setDuration(2000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();

    }
}
