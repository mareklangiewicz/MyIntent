package pl.mareklangiewicz.myactivities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.noveogroup.android.log.MyLogger;

import pl.mareklangiewicz.myviews.MyNavigationView;

public class MyBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Default logger for use in UI thread
     * TODO: use annotations to force UI only usage
     */
    public MyLogger log = MyLogger.sMyDefaultUILogger;

    static public final String PREFIX = "fragment:";

    protected DrawerLayout mGlobalDrawerLayout;
    protected CoordinatorLayout mCoordinatorLayout;
    protected AppBarLayout mAppBarLayout;
    protected Toolbar mToolbar;
    protected DrawerLayout mLocalDrawerLayout;
    protected FrameLayout mLocalFrameLayout;
    protected MyNavigationView mLocalNavigationView;
    protected MyNavigationView mGlobalNavigationView;

    @MenuRes protected int mGlobalMenuId;
    @LayoutRes protected int mGlobalHeaderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.v("%s.%s", this.getClass().getSimpleName(), "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_base_activity);

        mGlobalDrawerLayout = (DrawerLayout) findViewById(R.id.mga_global_drawer_layout);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.mba_coordinator_layout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.mba_app_bar_layout);
        mToolbar = (Toolbar) findViewById(R.id.mba_toolbar);
        mLocalDrawerLayout = (DrawerLayout) findViewById(R.id.mba_local_drawer_layout);
        mLocalFrameLayout = (FrameLayout) findViewById(R.id.mba_local_frame_layout);
        mLocalNavigationView = (MyNavigationView) findViewById(R.id.mba_local_navigation_view);
        mGlobalNavigationView = (MyNavigationView) findViewById(R.id.mba_global_navigation_view);

        mGlobalNavigationView.setNavigationItemSelectedListener(this);

        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp); //FIXME: better animated icon (and for local navigation too..)
        mToolbar.setNavigationContentDescription(R.string.mba_navigation_content_description);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGlobalDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        setGlobalMenu(mGlobalMenuId); //either jus clears it (if null),
        // or it synchronizes mGlobalNavigationView with mGlobalMenuId set earlier
        // (if user called setGlobalMenu before onCreate...)

        log.setSnackView(mCoordinatorLayout);
    }

    @Override
    protected void onDestroy() {
        log.v("%s.%s", this.getClass().getSimpleName(), "onDestroy");
        log.setSnackView(null);
        super.onDestroy();
    }

    public void setGlobalMenu(@MenuRes int id) {
        mGlobalMenuId = id;
        if(mGlobalNavigationView != null)
            mGlobalNavigationView.setMenu(id);
    }

    public void setGlobalHeader(@LayoutRes int id) {
        mGlobalHeaderId = id;
        if(mGlobalNavigationView != null)
            mGlobalNavigationView.setHeader(id);
    }



    public boolean selectGlobalMenuItem(@IdRes int id) {
        return mGlobalNavigationView.selectMenuItem(id);
    }
    public boolean selectLocalMenuItem(@IdRes int id) {
        return mLocalNavigationView.selectMenuItem(id);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if(mGlobalDrawerLayout != null)
            mGlobalDrawerLayout.closeDrawers();
        String ctitle = item.getTitleCondensed().toString();
        if(ctitle.startsWith(PREFIX)) {
            ctitle = ctitle.substring(PREFIX.length());
            Fragment f = Fragment.instantiate(this, ctitle);
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.mba_local_frame_layout, f).commit(); //TODO: some animation?
            return true;
        }
        return false;
    }
}
