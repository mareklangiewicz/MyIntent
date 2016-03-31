package pl.mareklangiewicz.myactivities;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CallSuper;
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
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import pl.mareklangiewicz.mydrawables.MyArrowDrawable;
import pl.mareklangiewicz.mydrawables.MyLivingDrawable;
import pl.mareklangiewicz.myfragments.MyFragment;
import pl.mareklangiewicz.myloggers.MyAndroLogger;
import pl.mareklangiewicz.myloggers.MyAndroLoggerKt;
import pl.mareklangiewicz.myutils.MyCommand;
import pl.mareklangiewicz.myutils.MyCommandsKt;
import pl.mareklangiewicz.myutils.MyMathUtilsKt;
import pl.mareklangiewicz.myviews.IMyUIManager;
import pl.mareklangiewicz.myviews.IMyUINavigation;
import pl.mareklangiewicz.myviews.MyNavigationView;

import static pl.mareklangiewicz.myutils.MyTextUtilsKt.getStr;

public class MyActivity extends AppCompatActivity implements IMyUIManager, IMyUINavigation.Listener, DrawerLayout.DrawerListener {


    static public final String COMMAND_PREFIX = "cmd:";
    static public final String TAG_LOCAL_FRAGMENT = "tag_local_fragment";
    /*
        private static final boolean V = BuildConfig.VERBOSE;
        private static final boolean VV = BuildConfig.VERY_VERBOSE;

        FIXME SOMEDAY: enable version with BuildConfig when Google fix issue with propagating build types to libraries.
        Now it is always 'release' in libraries.. see:
        https://code.google.com/p/android/issues/detail?id=52962
        http://stackoverflow.com/questions/20176284/buildconfig-debug-always-false-when-building-library-projects-with-gradle
        http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Library-Publication
    */
    private static final boolean V = true;
    private static final boolean VV = false;
    private static final String DEFAULT_COMMAND_NAME = MyCommandsKt.getCMD_ACTIVITY();
    private static final String DEFAULT_INTENT_ACTION = Intent.ACTION_VIEW;
    protected final MyLivingDrawable mGlobalArrowDrawable = new MyArrowDrawable();
    protected final MyLivingDrawable mLocalArrowDrawable = new MyArrowDrawable();
    /**
     * Default logger for use in UI thread
     */
    protected @NonNull final MyAndroLogger log = MyAndroLoggerKt.getMY_DEFAULT_ANDRO_LOGGER();

    protected @Nullable DisplayMetrics mDisplayMetrics;
    protected @Nullable DrawerLayout mGlobalDrawerLayout;
    protected @Nullable LinearLayout mGlobalLinearLayout; // either this or mGlobalDrawerLayout will remain null
    protected @Nullable CoordinatorLayout mCoordinatorLayout;
    protected @Nullable AppBarLayout mAppBarLayout;
    protected @Nullable Toolbar mToolbar;
    protected @Nullable DrawerLayout mLocalDrawerLayout;
    protected @Nullable LinearLayout mLocalLinearLayout; // either this or mLocalDrawerLayout will remain null
    protected @Nullable FrameLayout mLocalFrameLayout;
    protected @Nullable MyNavigationView mLocalNavigationView;
    protected @Nullable MyNavigationView mGlobalNavigationView;
    protected @Nullable FloatingActionButton mFAB;
    protected @Nullable View mLocalArrowView;

    protected @Nullable Fragment mLocalFragment;
    protected @Nullable MyFragment mMyLocalFragment; // the same as mLocalFragment - if mLocalFragment instanceof MyFragment - or null
    // otherwise..

    protected Handler handler;

    @CallSuper @Override protected void onCreate(Bundle savedInstanceState) {

        handler = new Handler();

        mDisplayMetrics = getResources().getDisplayMetrics();
        if(V) {
            log.d(String.format("%s.%s state=%s", this.getClass().getSimpleName(), "onCreate", getStr(savedInstanceState)));
            log.v(getStr(mDisplayMetrics));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ma_my_activity);

        mGlobalDrawerLayout = (DrawerLayout) findViewById(R.id.ma_global_drawer_layout);
        mGlobalLinearLayout = (LinearLayout) findViewById(R.id.ma_global_linear_layout);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.ma_coordinator_layout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.ma_app_bar_layout);
        mToolbar = (Toolbar) findViewById(R.id.ma_toolbar);
        mLocalDrawerLayout = (DrawerLayout) findViewById(R.id.ma_local_drawer_layout);
        mLocalLinearLayout = (LinearLayout) findViewById(R.id.ma_local_linear_layout);
        mLocalFrameLayout = (FrameLayout) findViewById(R.id.ma_local_frame_layout);
        mGlobalNavigationView = (MyNavigationView) findViewById(R.id.ma_global_navigation_view);
        mLocalNavigationView = (MyNavigationView) findViewById(R.id.ma_local_navigation_view);

        if(mGlobalDrawerLayout != null)
            mGlobalDrawerLayout.addDrawerListener(this);

        if(mLocalDrawerLayout != null)
            mLocalDrawerLayout.addDrawerListener(this);

        //noinspection ConstantConditions
        mGlobalNavigationView.setListener(this);
        //noinspection ConstantConditions
        mLocalNavigationView.setListener(this);

        setSupportActionBar(mToolbar);

        mGlobalArrowDrawable.setStrokeWidth(5);
        mGlobalArrowDrawable.setRotateTo(360f + 180f);
        mGlobalArrowDrawable.setAlpha(0xa0);

        mLocalArrowDrawable.setStrokeWidth(5);
        mLocalArrowDrawable.setRotateFrom(180f);
        mLocalArrowDrawable.setAlpha(0xa0);

        //noinspection ConstantConditions
        mToolbar.setNavigationIcon(mGlobalArrowDrawable);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                //noinspection ConstantConditions
                boolean empty = getGnav().getEmpty();
                if(!empty)
                    toggleGlobalNavigation();
                else
                    log.d("Global navigation is empty.");
            }
        });

        mLocalArrowView = new View(this);
        mLocalArrowView.setBackground(mLocalArrowDrawable);
        mLocalArrowView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                //noinspection ConstantConditions
                boolean empty = getLnav().getEmpty();
                if(!empty)
                    toggleLocalNavigation();
                else
                    log.d("Local navigation is empty.");
            }
        });
        int h = mToolbar.getMinimumHeight() * 3 / 4;
        mLocalArrowView.setLayoutParams(new Toolbar.LayoutParams(h, h, GravityCompat.END));
        mToolbar.addView(mLocalArrowView);

        mFAB = (FloatingActionButton) findViewById(R.id.ma_fab);

        if(savedInstanceState != null) {
            FragmentManager fm = getFragmentManager();
            Fragment f = fm.findFragmentByTag(TAG_LOCAL_FRAGMENT);
            updateLocalFragment(f); // can be null.
        }
    }

    private boolean checkFirstCheckableItemWithCommand(Menu menu, String command) {
        int size = menu.size();
        for(int i = 0; i < size; ++i) {
            MenuItem item = menu.getItem(i);
            if(item.isCheckable() && item.getTitleCondensed().equals("cmd:" + command)) {
                item.setChecked(true);
                return true;
            }
            if(item.hasSubMenu()) {
                Menu submenu = item.getSubMenu();
                if(submenu != null) {
                    boolean done = checkFirstCheckableItemWithCommand(submenu, command);
                    if(done)
                        return true;
                }
            }
        }
        return false;
    }

    private void toggleGlobalNavigation() {
        //TODO NOW: uncomment after kotlinize (and test it..) (and create extension function for it..)
//        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        if(mGlobalDrawerLayout != null)
            toggleDrawer(mGlobalDrawerLayout, GravityCompat.START);
        else if(mGlobalLinearLayout != null)
            toggleMNVAndArrow(mGlobalNavigationView, mGlobalArrowDrawable);
        else
            log.a("No global drawer or linear layout with global navigation.");
    }

    private void toggleLocalNavigation() {
        //TODO NOW: uncomment after kotlinize (and test it..) (and create extension function for it..)
//        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        if(mLocalDrawerLayout != null)
            toggleDrawer(mLocalDrawerLayout, GravityCompat.END);
        else if(mLocalLinearLayout != null)
            toggleMNVAndArrow(mLocalNavigationView, mLocalArrowDrawable);
        else
            log.a("No local drawer or linear layout with local navigation.");
    }

    private void toggleDrawer(@NonNull DrawerLayout drawerLayout, int gravity) {
        if(drawerLayout.getDrawerLockMode(gravity) == DrawerLayout.LOCK_MODE_UNLOCKED) {
            if(drawerLayout.isDrawerVisible(gravity))
                drawerLayout.closeDrawer(gravity);
            else
                drawerLayout.openDrawer(gravity);
        }
        else
            log.e("Drawer is locked.");
    }

    private void toggleMNVAndArrow(MyNavigationView mnv, MyLivingDrawable arrow) {
        setMNVAndArrow(arrow.getLevel() < 10000, mnv, arrow);
    }

    void setMNVAndArrow(boolean open, MyNavigationView mnv, MyLivingDrawable arrow) {
/*  Scene transitions disabled - flickering issue..
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionManager.beginDelayedTransition((ViewGroup)mnv.getParent(), new Fade());
        }
*/
        mnv.setVisibility(open ? View.VISIBLE : View.GONE);
        arrow.setLevel(open ? 10000 : 0);
    }

    @CallSuper @Override protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(mGlobalDrawerLayout != null)
            onDrawerSlide(mGlobalNavigationView, mGlobalDrawerLayout.isDrawerOpen(GravityCompat.START) ? 1f : 0f);
        if(mLocalDrawerLayout != null)
            onDrawerSlide(mLocalNavigationView, mLocalDrawerLayout.isDrawerOpen(GravityCompat.END) ? 1f : 0f);
        // ensure that drawers and menu icons are updated:
        //noinspection ConstantConditions
        onNavigationChanged(getGnav());
        //noinspection ConstantConditions
        onNavigationChanged(getLnav());
        if(savedInstanceState == null)
            onIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // update saved intent in case we need to call getIntent() later..
        onIntent(intent);
    }


    @CallSuper
    public void onIntent(@Nullable Intent intent) {
        if(VV) {
            log.v(String.format("%s.%s: %s", this.getClass().getSimpleName(), "onIntent", getStr(intent)));
        }
    }

    @Override protected void onStart() {
        if(VV) {
            log.v(String.format("%s.%s", this.getClass().getSimpleName(), "onStart"));
        }
        log.setView(mCoordinatorLayout);
        super.onStart();
    }

    @Override protected void onResume() {
        if(VV) {
            log.v(String.format("%s.%s", this.getClass().getSimpleName(), "onResume"));
        }
        log.setView(mCoordinatorLayout);
        super.onResume();
    }

    @Override protected void onPause() {
        if(VV) {
            log.v(String.format("%s.%s", this.getClass().getSimpleName(), "onPause"));
        }
        if(log.getView() == mCoordinatorLayout)
            log.setView(null);
        super.onPause();
    }

    @CallSuper @Override protected void onStop() {
        if(VV) {
            log.v(String.format("%s.%s", this.getClass().getSimpleName(), "onStop"));
        }
        if(log.getView() == mCoordinatorLayout)
            log.setView(null);

        super.onStop();
    }

    @CallSuper @Override protected void onDestroy() {
        if(V)
            log.d(String.format("%s.%s", this.getClass().getSimpleName(), "onDestroy"));

        if(log.getView() == mCoordinatorLayout)
            log.setView(null);

        if(mGlobalDrawerLayout != null) {
            mGlobalDrawerLayout.removeDrawerListener(this);
            mGlobalDrawerLayout = null;
        }
        if(mLocalDrawerLayout != null) {
            mLocalDrawerLayout.removeDrawerListener(this);
            mLocalDrawerLayout = null;
        }
        mGlobalLinearLayout = null;
        mCoordinatorLayout = null;
        mAppBarLayout = null;
        mToolbar = null;
        mLocalLinearLayout = null;
        mLocalFrameLayout = null;
        mLocalNavigationView = null;
        mGlobalNavigationView = null;
        mFAB = null;
        mLocalArrowView = null;
        mDisplayMetrics = null;

        updateLocalFragment(null);

        super.onDestroy();
    }

    protected void updateLocalFragment(@Nullable Fragment fragment) {
        if(VV)
            log.v(String.format("%s.%s fragment=%s", this.getClass().getSimpleName(), "updateLocalFragment", getStr(fragment)));

        mLocalFragment = fragment;
        mMyLocalFragment = null;

        if(fragment == null)
            return;

        if(fragment instanceof MyFragment)
            mMyLocalFragment = (MyFragment) fragment;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.setEnterTransition(new Fade());
            fragment.setExitTransition(new Fade());
            fragment.setSharedElementEnterTransition(new AutoTransition());
            fragment.setSharedElementReturnTransition(new AutoTransition());
        }

    }


    /**
     * @param command A command to perform
     */
    protected void onCommand(@NonNull String command) {

        IMyUINavigation gnav = getGnav();
        if(gnav != null) {
            Menu menu = getGnav().getMenuObj();
            if(menu != null)
                checkFirstCheckableItemWithCommand(menu, command);
        }

        try {
            MyCommand cmd = new MyCommand(command, MyCommandsKt.getRE_RULES(), log);
            String start = cmd.get("start");
            String component = cmd.get("component");
            String action = cmd.get("action");

            String pkg = getPackageName();

            if(start == null) {
                start = DEFAULT_COMMAND_NAME;
                cmd.put("start", start);
            }

            if(start.equals(MyCommandsKt.getCMD_ACTIVITY())) {
                if(component == null && action == null) {
                    action = DEFAULT_INTENT_ACTION;
                    cmd.put("action", action);
                }
                if(component != null && !component.contains("/")) {
                    component = pkg + "/" + component;
                    cmd.put("component", component);
                }
            }
            else if(cmd.get("start").equals(MyCommandsKt.getCMD_FRAGMENT())) {
                if(component == null) {
                    log.e("Fragment component is null.");
                    return;
                }
                if(component.startsWith(".")) {
                    component = pkg + component;
                    cmd.put("component", component);
                }
            }

            onCommand(cmd);
        }
        catch(RuntimeException e) {
            log.e(String.format("Invalid command: %s", command), e);
        }

    }

    /**
     * @param command A parsed command
     */
    protected void onCommand(@NonNull MyCommand command) {

        //TODO NOW: use constants from MyCommands in switch cases (after moving to Kotlin)
        switch(command.get("start")) {
            case "activity":
                onCommandStartActivity(command);
                return;
            case "service":
                onCommandStartService(command);
                return;
            case "broadcast":
                onCommandStartBroadcast(command);
                return;
            case "fragment":
                onCommandStartFragment(command);
                return;
            case "custom":
                onCommandCustom(command);
                return;
            case "nothing":
                return; // just for dry testing purposes
            default:
                log.e(String.format("Unsupported command: %s", getStr(command)));
        }
    }

    protected void onCommandStartActivity(@NonNull MyCommand command) {

        try {

            Intent intent = MyCommandsKt.toIntent(command);

            if(intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            else {
                log.e(String.format("No activity found for this intent: %s", getStr(intent)));
            }
        }
        catch(IllegalArgumentException e) {
            log.e("Illegal command: " + getStr(command), e);
        }
        catch(ActivityNotFoundException e) {
            log.e("Activity not found.", e);
        }
        catch(SecurityException e) {
            log.a("Security exception.", e);
        }
    }

    protected void onCommandStartService(@NonNull MyCommand command) {

        try {

            Intent intent = MyCommandsKt.toIntent(command);

            if(startService(intent) == null) {
                log.e(String.format("Service not found for this intent: %s", getStr(intent)));
            }
        }
        catch(IllegalArgumentException e) {
            log.e("Illegal command: " + getStr(command), e);
        }
        catch(SecurityException e) {
            log.a("Security exception.", e);
        }
    }

    protected void onCommandStartBroadcast(@NonNull MyCommand command) {

        try {

            Intent intent = MyCommandsKt.toIntent(command);
            sendBroadcast(intent);
        }
        catch(IllegalArgumentException e) {
            log.e("Illegal command: " + getStr(command), e);
        }
    }

    /**
     * Extras in fragment command are delivered as fragment arguments bundle.
     */
    protected void onCommandStartFragment(@NonNull MyCommand command) {

        FragmentManager fm = getFragmentManager();
        Fragment f;

        try {
            f = Fragment.instantiate(MyActivity.this, command.get("component"));
            Bundle args = MyCommandsKt.toExtrasBundle(command);
            if(args.size() > 0)
                f.setArguments(args);
            updateLocalFragment(f);
            @SuppressLint("CommitTransaction")
            FragmentTransaction ft = fm.beginTransaction().replace(R.id.ma_local_frame_layout, f, TAG_LOCAL_FRAGMENT);
            addAllSharedElementsToFragmentTransaction(findViewById(R.id.ma_local_frame_layout), ft);
            ft.commit();
        }
        catch(Fragment.InstantiationException e) {
            log.e(String.format("Fragment class: %s not found.", command.get("component")), e);
        }
        catch(IllegalArgumentException e) {
            log.e("Illegal command: " + getStr(command), e);
        }
    }

    protected void onCommandCustom(@NonNull MyCommand command) {
        log.e(String.format("Unsupported custom command: %s", getStr(command)));
    }

    public void closeDrawers() {
        if(mGlobalDrawerLayout != null)
            mGlobalDrawerLayout.closeDrawers();
        if(mLocalDrawerLayout != null)
            mLocalDrawerLayout.closeDrawers();
    }

    public boolean areDrawersVisible() {
        return (mGlobalDrawerLayout != null && mGlobalDrawerLayout.isDrawerVisible(GravityCompat.START)) ||
                (mLocalDrawerLayout != null && mLocalDrawerLayout.isDrawerVisible(GravityCompat.END));

    }

    protected void closeDrawersAnd(Runnable runnable) {
        if(areDrawersVisible()) {
            closeDrawers();
            handler.postDelayed(runnable, 400);
        }
        else
            handler.post(runnable);
    }

    public void execute(final String cmd) {
        closeDrawersAnd(new Runnable() {
            @Override public void run() {
                onCommand(cmd);
            }
        });
    }


    /**
     * You can override it, but you should call super version first and do your custom logic only if it returns false.
     */
    @CallSuper @Override public boolean onItemSelected(@NonNull IMyUINavigation nav, @NonNull MenuItem item) {
        final String ctitle = item.getTitleCondensed().toString();
        if(ctitle.startsWith(COMMAND_PREFIX)) {
            final String cmd = ctitle.substring(COMMAND_PREFIX.length());
            execute(cmd);
            return true;
        }

        closeDrawers();
        // maybe our local fragment will handle this item:
        boolean done;
        if(mMyLocalFragment != null) {
            done = mMyLocalFragment.onItemSelected(nav, item);
            if(done)
                return true;
        }
        return false;
    }

    @Override @CallSuper public void onNavigationChanged(@NonNull IMyUINavigation nav) {
        boolean empty = nav.getEmpty();
        if(nav == getGnav()) {
            mGlobalArrowDrawable.setAlpha(empty ? 0 : 0xa0);
            if(mGlobalDrawerLayout != null)
                mGlobalDrawerLayout.setDrawerLockMode(empty ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
            else if(mGlobalLinearLayout != null)
                setMNVAndArrow(!empty, mGlobalNavigationView, mGlobalArrowDrawable);
            else
                log.a("No global drawer or linear layout with global navigation.");
        }
        else if(nav == getLnav()) {
            mLocalArrowDrawable.setAlpha(empty ? 0 : 0xa0);
            if(mLocalDrawerLayout != null)
                mLocalDrawerLayout.setDrawerLockMode(empty ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
            else if(mLocalLinearLayout != null)
                setMNVAndArrow(!empty, mLocalNavigationView, mLocalArrowDrawable);
            else
                log.a("No local drawer or linear layout with local navigation.");
        }
        else
            log.a("Unknown IMyUINavigation object.");

        if(mMyLocalFragment != null)
            mMyLocalFragment.onNavigationChanged(nav);
    }

    private void addAllSharedElementsToFragmentTransaction(View root, FragmentTransaction ft) {
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
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
            log.d("Can not add shared elements to fragment transaction. API < 21");
        }
    }


    @Override public @Nullable FloatingActionButton getFab() {
        return mFAB;
    }

    @NonNull @Override public CharSequence getMytitle() {
        return getTitle();
    }

    @Override public void setMytitle(@NonNull CharSequence charSequence) {
        setTitle(charSequence);
    }

    @Override public IMyUINavigation getGnav() {
        return mGlobalNavigationView;
    }

    @Override public IMyUINavigation getLnav() {
        return mLocalNavigationView;
    }

    @CallSuper @Override public void onDrawerSlide(View drawerView, float slideOffset) {
        if(drawerView == mGlobalNavigationView) {
            mGlobalArrowDrawable.setLevel((int) MyMathUtilsKt.scale0d(slideOffset, 1f, 10000f));
        }
        else if(drawerView == mLocalNavigationView) {
            mLocalArrowDrawable.setLevel((int) MyMathUtilsKt.scale0d(slideOffset, 1f, 10000f));
        }
        if(mMyLocalFragment != null)
            mMyLocalFragment.onDrawerSlide(drawerView, slideOffset);
    }

    @CallSuper @Override public void onDrawerOpened(View drawerView) {
        if(mMyLocalFragment != null)
            mMyLocalFragment.onDrawerOpened(drawerView);
    }

    @CallSuper @Override public void onDrawerClosed(View drawerView) {
        if(mMyLocalFragment != null)
            mMyLocalFragment.onDrawerClosed(drawerView);
    }

    @CallSuper @Override public void onDrawerStateChanged(int newState) {
        if(mMyLocalFragment != null)
            mMyLocalFragment.onDrawerStateChanged(newState);
    }


    @CallSuper
    @Override public void onBackPressed() {
        if(mGlobalDrawerLayout != null && mGlobalDrawerLayout.isDrawerOpen(GravityCompat.START))
            mGlobalDrawerLayout.closeDrawer(GravityCompat.START);
        else if(mLocalDrawerLayout != null && mLocalDrawerLayout.isDrawerOpen(GravityCompat.END))
            mLocalDrawerLayout.closeDrawer(GravityCompat.END);
        else
            super.onBackPressed();
    }

    public float dp2px(float dp) {
        if(mDisplayMetrics == null)
            throw new IllegalStateException("display metrics not ready");
        return dp * mDisplayMetrics.density;
    }

    @SuppressWarnings("unused")
    public float px2dp(float px) {
        if(mDisplayMetrics == null)
            throw new IllegalStateException("display metrics not ready");
        return px / mDisplayMetrics.density;
    }

}
