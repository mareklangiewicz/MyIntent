package pl.mareklangiewicz.myactivities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.noveogroup.android.log.MyLogger;

import pl.mareklangiewicz.mydrawables.MyArrowDrawable;
import pl.mareklangiewicz.mydrawables.MyLivingDrawable;
import pl.mareklangiewicz.myfragments.MyFragment;
import pl.mareklangiewicz.myviews.IMyCommander;
import pl.mareklangiewicz.myviews.IMyNavigation;
import pl.mareklangiewicz.myviews.MyNavigationView;

import static pl.mareklangiewicz.myutils.MyMathUtils.scale0d;
import static pl.mareklangiewicz.myutils.MyTextUtils.toStr;
// TODO: Hide left menu icon and block left drawer if global menu is empty
// TODO: Hide right menu icon and block right drawer if local menu is empty
// TODO LATER: use Leak Canary: https://github.com/square/leakcanary

// TODO: check menu/arrow icons state after changing orientation (with drawer open)

public class MyActivity extends AppCompatActivity implements IMyCommander, IMyNavigation.Listener, DrawerLayout.DrawerListener {

    static final boolean VERBOSE = true;
    //TODO LATER: implement it as a build time switch for user
    static final boolean VERY_VERBOSE = false;
    //TODO LATER: implement it as a build time switch for user

    /**
     * Default logger for use in UI thread
     */
    protected @NonNull MyLogger log = MyLogger.sMyDefaultUILogger;

    static public final String PREFIX_FRAGMENT = "fragment:";
    static public final String PREFIX_ACTIVITY = "activity:";

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

    protected final MyLivingDrawable mGlobalArrowDrawable = new MyArrowDrawable();
    protected final MyLivingDrawable mLocalArrowDrawable = new MyArrowDrawable();

    protected @Nullable View mLocalArrowView;

    protected @Nullable Fragment mLocalFragment;
    protected @Nullable MyFragment mMyLocalFragment; // the same as mLocalFragment - if mLocalFragment instanceof MyFragment - or null otherwise..

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(VERBOSE) log.v("%s.%s state=%s", this.getClass().getSimpleName(), "onCreate", toStr(savedInstanceState));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity);

        mGlobalDrawerLayout = (DrawerLayout) findViewById(R.id.ma_global_drawer_layout);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.ma_coordinator_layout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.ma_app_bar_layout);
        mToolbar = (Toolbar) findViewById(R.id.ma_toolbar);
        mLocalDrawerLayout = (DrawerLayout) findViewById(R.id.ma_local_drawer_layout);
        mLocalFrameLayout = (FrameLayout) findViewById(R.id.ma_local_frame_layout);
        mGlobalNavigationView = (MyNavigationView) findViewById(R.id.ma_global_navigation_view);
        mLocalNavigationView = (MyNavigationView) findViewById(R.id.ma_local_navigation_view);

        if(mGlobalDrawerLayout != null)
            mGlobalDrawerLayout.setDrawerListener(this);

        if(mLocalDrawerLayout != null)
            mLocalDrawerLayout.setDrawerListener(this);


        //noinspection ConstantConditions
        mGlobalNavigationView.setListener(this);
        //noinspection ConstantConditions
        mLocalNavigationView.setListener(this);

        setSupportActionBar(mToolbar);

        mGlobalArrowDrawable.setStrokeWidth(6).setRotateTo(360f + 180f).setAlpha(0xa0);
        mLocalArrowDrawable.setStrokeWidth(6).setRotateFrom(180f).setAlpha(0xa0);

        //noinspection ConstantConditions
        mToolbar.setNavigationIcon(mGlobalArrowDrawable);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDrawerIfUnlocked(mGlobalDrawerLayout, GravityCompat.START);
            }
        });

        mLocalArrowView = new View(this);
        mLocalArrowView.setBackground(mLocalArrowDrawable);
        mLocalArrowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDrawerIfUnlocked(mLocalDrawerLayout, GravityCompat.END);
            }
        });
        int h = mToolbar.getMinimumHeight();
        mLocalArrowView.setLayoutParams(new Toolbar.LayoutParams(h, h, GravityCompat.END));
        mToolbar.addView(mLocalArrowView);

        mFAB = (FloatingActionButton) findViewById(R.id.ma_fab);

        if(savedInstanceState != null) {
            FragmentManager fm = getFragmentManager();
            Fragment f = fm.findFragmentByTag(TAG_LOCAL_FRAGMENT);
            // TODO LATER: isn't it too soon? What if it is not MyFragment (and retaininstance is false)
            // TODO LATER: analyze if findFragmentByTag will always have our fragment ready here.
            updateLocalFragment(f); // can be null.
        }
    }

    private void toggleDrawerIfUnlocked(@Nullable DrawerLayout drawerLayout, int gravity) {
        if (drawerLayout == null)
            log.e("No drawer found!");
        else if(drawerLayout.getDrawerLockMode(gravity) == DrawerLayout.LOCK_MODE_UNLOCKED) {
            if (drawerLayout.isDrawerVisible(gravity))
                drawerLayout.closeDrawer(gravity);
            else
                drawerLayout.openDrawer(gravity);
        }
        else
            log.d("Drawer is locked.");
    }

    @CallSuper
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(mGlobalDrawerLayout != null)
            onDrawerSlide(mGlobalNavigationView, mGlobalDrawerLayout.isDrawerOpen(GravityCompat.START) ? 1f : 0f);
        if(mLocalDrawerLayout != null)
            onDrawerSlide(mLocalNavigationView, mLocalDrawerLayout.isDrawerOpen(GravityCompat.END) ? 1f : 0f);
        // ensure that drawers and menu icons are updated:
        onNavigationContentChanged(getGlobalNavigation());
        onNavigationContentChanged(getLocalNavigation());
    }

    @Override
    protected void onResume() {
        super.onResume();
        log.setSnackView(mCoordinatorLayout);
    }

    @Override
    protected void onPause() {
        log.setSnackView(null);
        super.onPause();
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        if(VERBOSE) log.v("%s.%s", this.getClass().getSimpleName(), "onDestroy");

        mGlobalDrawerLayout = null;
        mCoordinatorLayout = null;
        mAppBarLayout = null;
        mToolbar = null;
        mLocalDrawerLayout = null;
        mLocalFrameLayout = null;
        mLocalNavigationView = null;
        mGlobalNavigationView = null;
        mFAB = null;
        mLocalArrowView = null;

        updateLocalFragment(null);

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
        getLocalNavigation().clearHeader();
        getLocalNavigation().clearMenu();
        return true;
    }

    protected void updateLocalFragment(@Nullable Fragment fragment) {
        if(VERY_VERBOSE) log.v("%s.%s fragment=%s", this.getClass().getSimpleName(), "updateLocalFragment", toStr(fragment));

        mLocalFragment = fragment;
        mMyLocalFragment = null;

        if(fragment == null)
            return;

        if(fragment instanceof MyFragment)
            mMyLocalFragment = (MyFragment) fragment;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(fragment.getSharedElementEnterTransition() == null)
                fragment.setSharedElementEnterTransition(new AutoTransition());
            if(fragment.getEnterTransition() == null)
                fragment.setEnterTransition(new Fade());
            if(fragment.getSharedElementReturnTransition() == null)
                fragment.setSharedElementReturnTransition(new AutoTransition());
            if(fragment.getExitTransition() == null)
                fragment.setExitTransition(new Fade());
        }

    }

    /**
     * You can override it, but you should call super version first and do your custom logic only if it returns false.
     */
    @CallSuper
    @Override
    public boolean onItemSelected(IMyNavigation nav, MenuItem item) {
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

            f = Fragment.instantiate(this, ctitle);

            //TODO: allow to get some string arguments from titleCondensed

            updateLocalFragment(f);

            FragmentTransaction ft = fm.beginTransaction().replace(R.id.ma_local_frame_layout, f, TAG_LOCAL_FRAGMENT);
            addAllSharedElementsToFragmentTransaction(findViewById(R.id.ma_local_frame_layout), ft);
            ft.commit();

            return true;
        }
        if(ctitle.startsWith(PREFIX_ACTIVITY)) {
            ctitle = ctitle.substring(PREFIX_ACTIVITY.length());
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(this, ctitle));
            try {
                startActivity(intent);
            }
            catch (ActivityNotFoundException e) {
                log.e(e);
                return false;
            }
            return true;
        }
        //TODO LATER: better support for other prefixes (like starting activities/services) - but first lets make MyIntent work with new MyBlocks
        //TODO LATER: if we want to have an engine for starting activities/services here - we should put almost all logic from MyIntent to MyBlocks...
        //TODO LATER: and MyIntent would be only a thin wrapper.. and.. that's a great idea!
        //TODO LATER: menu api already has some api for launching intents (MenuItem.setIntent) - but our MyIntent engine is better!

        if(mMyLocalFragment != null) {
            boolean done = mMyLocalFragment.onItemSelected(nav, item);
            if(done) return true;
        }
        return false;
    }

    @CallSuper
    protected void onNavigationContentChanged(IMyNavigation nav) {
        boolean empty = nav.isEmpty();
        if(nav == getGlobalNavigation()) {
            mGlobalArrowDrawable.setAlpha(empty ? 0 : 0xa0);
            if(mGlobalDrawerLayout != null)
                mGlobalDrawerLayout.setDrawerLockMode(empty ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        else if(nav == getLocalNavigation()) {
            //noinspection ConstantConditions
            mLocalArrowView.setVisibility(empty ? View.GONE : View.VISIBLE);
            if(mLocalDrawerLayout != null)
                mLocalDrawerLayout.setDrawerLockMode(empty ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        else
            log.e("Unknown IMyNavigation object.");
    }

    @Override
    public void onClearHeader(IMyNavigation nav) {
        onNavigationContentChanged(nav);
        if(mMyLocalFragment != null)
            mMyLocalFragment.onClearHeader(nav);
    }

    @Override
    public void onClearMenu(IMyNavigation nav) {
        onNavigationContentChanged(nav);
        if(mMyLocalFragment != null)
            mMyLocalFragment.onClearMenu(nav);
    }

    @Override
    public void onInflateHeader(IMyNavigation nav) {
        onNavigationContentChanged(nav);
        if(mMyLocalFragment != null)
            mMyLocalFragment.onInflateHeader(nav);
    }

    @Override
    public void onInflateMenu(IMyNavigation nav) {
        onNavigationContentChanged(nav);
        if(mMyLocalFragment != null)
            mMyLocalFragment.onInflateMenu(nav);
    }

    private void addAllSharedElementsToFragmentTransaction(View root, FragmentTransaction ft) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            String name = root.getTransitionName();
            if(name != null)
                ft.addSharedElement(root, name);
            else if(root instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) root;
                for(int i = 0; i < group.getChildCount(); ++i)
                    addAllSharedElementsToFragmentTransaction(group.getChildAt(i), ft);
            }
        }
        else {
            log.w("Can not add shared elements to fragment transaction. API < 21");
        }
    }

    @Override public @Nullable FloatingActionButton getFAB() { return mFAB; }
    @Override public IMyNavigation getGlobalNavigation() {
        return mGlobalNavigationView;
    }
    @Override public IMyNavigation getLocalNavigation() {
        return mLocalNavigationView;
    }

    private void selectItem(IMyNavigation nav, @IdRes int id) {
        Menu menu = nav.getMenu();
        if(menu == null) {
            log.e("menu is null!");
            return;
        }
        nav.setCheckedItem(id);
        onItemSelected(nav, menu.findItem(id));
    }

    public void selectGlobalItem(@IdRes int id) { selectItem(getGlobalNavigation(), id); }
    public void selectLocalItem(@IdRes int id) { selectItem(getLocalNavigation(), id); }

    @CallSuper
    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        if(drawerView == mGlobalNavigationView) {
            mGlobalArrowDrawable.setLevel((int) scale0d(slideOffset, 1f, 10000f));
        }
        else if(drawerView == mLocalNavigationView) {
            mLocalArrowDrawable.setLevel((int) scale0d(slideOffset, 1f, 10000f));
        }
        if(mMyLocalFragment != null)
            mMyLocalFragment.onDrawerSlide(drawerView, slideOffset);
    }

    @CallSuper
    @Override public void onDrawerOpened(View drawerView) {
        if(mMyLocalFragment != null)
            mMyLocalFragment.onDrawerOpened(drawerView);
    }

    @CallSuper
    @Override public void onDrawerClosed(View drawerView) {
        if(mMyLocalFragment != null)
            mMyLocalFragment.onDrawerClosed(drawerView);
    }

    @CallSuper
    @Override public void onDrawerStateChanged(int newState) {
        if(mMyLocalFragment != null)
            mMyLocalFragment.onDrawerStateChanged(newState);
    }


}
