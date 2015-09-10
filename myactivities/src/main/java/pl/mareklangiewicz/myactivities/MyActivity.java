package pl.mareklangiewicz.myactivities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.noveogroup.android.log.MyLogger;

import pl.mareklangiewicz.myviews.IMyCommander;
import pl.mareklangiewicz.myviews.IMyNavigation;
import pl.mareklangiewicz.myviews.MyNavigationView;

import static pl.mareklangiewicz.myutils.MyTextUtils.toStr;
// TODO: Hide left menu icon and block left drawer if global menu is empty
// TODO: Hide right menu icon and block right drawer if local menu is empty
// TODO LATER: use Leak Canary: https://github.com/square/leakcanary

public class MyActivity extends AppCompatActivity implements IMyCommander, NavigationView.OnNavigationItemSelectedListener {

    static final boolean VERBOSE = true;
    //TODO LATER: implement it as a build time switch for user
    static final boolean VERY_VERBOSE = false;
    //TODO LATER: implement it as a build time switch for user

    /**
     * Default logger for use in UI thread
     */
    protected @NonNull MyLogger log = MyLogger.sMyDefaultUILogger;

    static public final String PREFIX_FRAGMENT = "fragment:";

    static public final String TAG_LOCAL_FRAGMENT = "tag_local_fragment";

    protected @Nullable DrawerLayout mGlobalDrawerLayout;
    protected @Nullable CoordinatorLayout mCoordinatorLayout;
    protected @Nullable AppBarLayout mAppBarLayout;
    protected @Nullable Toolbar mToolbar;
    protected @Nullable DrawerLayout mLocalDrawerLayout;
    protected @Nullable FrameLayout mLocalFrameLayout;
    protected @Nullable MyNavigationView mLocalNavigationView;
    protected @Nullable MyNavigationView mGlobalNavigationView;

    protected @Nullable FloatingActionButton mFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(VERBOSE) log.v("%s.%s state=%s", this.getClass().getSimpleName(), "onCreate", toStr(savedInstanceState));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity);

        mGlobalDrawerLayout = (DrawerLayout) findViewById(R.id.ma_global_drawer_layout);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.ma_global_coordinator_layout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.ma_app_bar_layout);
        mToolbar = (Toolbar) findViewById(R.id.ma_toolbar);
        mLocalDrawerLayout = (DrawerLayout) findViewById(R.id.ma_local_drawer_layout);
        mLocalFrameLayout = (FrameLayout) findViewById(R.id.ma_local_frame_layout);
        mGlobalNavigationView = (MyNavigationView) findViewById(R.id.ma_global_navigation_view);
        mLocalNavigationView = (MyNavigationView) findViewById(R.id.ma_local_navigation_view);

        if(mGlobalDrawerLayout != null && this instanceof DrawerLayout.DrawerListener)
            mGlobalDrawerLayout.setDrawerListener((DrawerLayout.DrawerListener) this);


        //noinspection ConstantConditions
        mGlobalNavigationView.setNavigationItemSelectedListener(this);
        //noinspection ConstantConditions
        mLocalNavigationView.setNavigationItemSelectedListener(this);

        setSupportActionBar(mToolbar);

        //noinspection ConstantConditions
        mToolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp); //FIXME later: better animated icon (and for local navigation too..)
        mToolbar.setNavigationContentDescription(R.string.ma_global_navigation_description);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mGlobalDrawerLayout == null)
                    log.e("No global drawer found!");
                else
                    mGlobalDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //TODO: implement icon for right menu (local menu)

        mFAB = (FloatingActionButton) findViewById(R.id.ma_fab);
        //noinspection ConstantConditions
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.w("[SNACK]FAB Clicked!");
            }
        });

        log.setSnackView(mCoordinatorLayout);

        if(savedInstanceState != null) {
            FragmentManager fm = getFragmentManager();
            Fragment f = fm.findFragmentByTag(TAG_LOCAL_FRAGMENT);
            if(f != null) setupLocalFragment(f);
        }
    }

    @Override
    protected void onDestroy() {
        if(VERBOSE) log.v("%s.%s", this.getClass().getSimpleName(), "onDestroy");

        log.setSnackView(null);

        mGlobalDrawerLayout = null;
        mCoordinatorLayout = null;
        mAppBarLayout = null;
        mToolbar = null;
        mLocalDrawerLayout = null;
        mLocalFrameLayout = null;
        mLocalNavigationView = null;
        mGlobalNavigationView = null;
        mFAB = null;

        super.onDestroy();
    }

    /**
     * Override if you want to do something different that clearing local navigation menu and header
     * before new local fragment creation, or if you want to cancel it (by returning false)
     * @param name Class name of new fragment to instantiate
     * @return  False will cancel new fragment creation.
     */
    protected boolean onNewLocalFragment(String name) {
        if(VERY_VERBOSE) log.v("%s.%s name=%s", this.getClass().getSimpleName(), "onNewLocalFragment", name);
        //noinspection ConstantConditions
        mLocalNavigationView.clearHeader();
        mLocalNavigationView.clearMenu();
        return true;
    }

    protected void setupLocalFragment(Fragment fragment) {
        if(VERY_VERBOSE) log.v("%s.%s fragment=%s", this.getClass().getSimpleName(), "setupLocalFragment", toStr(fragment));

        if(mLocalDrawerLayout != null && fragment instanceof DrawerLayout.DrawerListener)
            mLocalDrawerLayout.setDrawerListener((DrawerLayout.DrawerListener) fragment);

    }

    /**
     * You can override it, but you should call super version first and do your custom logic only if it returns false.
     */
    @CallSuper
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if(mGlobalDrawerLayout != null)
            mGlobalDrawerLayout.closeDrawers();
        if(mLocalDrawerLayout != null)
            mLocalDrawerLayout.closeDrawers();
        String ctitle = item.getTitleCondensed().toString();
        FragmentManager fm = getFragmentManager();
        Fragment f;
        if(ctitle.startsWith(PREFIX_FRAGMENT)) {
            ctitle = ctitle.substring(PREFIX_FRAGMENT.length());
            boolean ok = onNewLocalFragment(ctitle);
            if(!ok)
                return false;

            f = Fragment.instantiate(MyActivity.this, ctitle);

            setupLocalFragment(f);

            fm.beginTransaction().replace(R.id.ma_local_frame_layout, f, TAG_LOCAL_FRAGMENT).commit(); //TODO LATER: some animation?

            return true;
        }
        //TODO LATER: support for other prefixes (like starting activities/services) - but first lets make MyIntent work with new MyBlocks
        //TODO LATER: if we want to have an engine for starting activities/services here - we should put almost all logic from MyIntent to MyBlocks...
        //TODO LATER: and MyIntent would be only a thin wrapper.. and.. that's a great idea!
        //TODO LATER: menu api already has some api for launching intents (MenuItem.setIntent) - but our MyIntent engine is better!

        f = fm.findFragmentByTag(TAG_LOCAL_FRAGMENT);
        if(f instanceof NavigationView.OnNavigationItemSelectedListener) {
            if(((NavigationView.OnNavigationItemSelectedListener) f).onNavigationItemSelected(item))
                return true;
        }
        return false;
    }

    @Override
    public IMyNavigation getGlobalNavigation() {
        return mGlobalNavigationView;
    }

    @Override
    public IMyNavigation getLocalNavigation() {
        return mLocalNavigationView;
    }


}
