package pl.mareklangiewicz.myactivities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
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
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.noveogroup.android.log.MyLogger;

import java.util.HashMap;
import java.util.Map;

import pl.mareklangiewicz.mydrawables.MyArrowDrawable;
import pl.mareklangiewicz.mydrawables.MyLivingDrawable;
import pl.mareklangiewicz.myfragments.MyFragment;
import pl.mareklangiewicz.myutils.MyCommands;
import pl.mareklangiewicz.myviews.IMyManager;
import pl.mareklangiewicz.myviews.IMyNavigation;
import pl.mareklangiewicz.myviews.MyNavigationView;

import static pl.mareklangiewicz.myutils.MyMathUtils.scale0d;
import static pl.mareklangiewicz.myutils.MyTextUtils.str;
// TODO LATER: use Leak Canary: https://github.com/square/leakcanary (check U+2020 example...)

public class MyActivity extends AppCompatActivity implements IMyManager, IMyNavigation.Listener, DrawerLayout.DrawerListener {


    static public final String COMMAND_PREFIX = "cmd:";
    static public final String TAG_LOCAL_FRAGMENT = "tag_local_fragment";
    static final boolean VERBOSE = true; //TODO LATER: implement it as a build time switch for user
    static final boolean VERY_VERBOSE = false; //TODO LATER: implement it as a build time switch for user
    private static final String DEFAULT_COMMAND_NAME = "activity";
    private static final String DEFAULT_INTENT_ACTION = Intent.ACTION_VIEW;
    protected final MyLivingDrawable mGlobalArrowDrawable = new MyArrowDrawable();
    protected final MyLivingDrawable mLocalArrowDrawable = new MyArrowDrawable();
    /**
     * Default logger for use in UI thread
     */
    protected @NonNull MyLogger log = MyLogger.sMyDefaultUILogger;
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

    protected @Nullable String mMyPendingCommand;

    @CallSuper @Override protected void onCreate(Bundle savedInstanceState) {
        mDisplayMetrics = getResources().getDisplayMetrics();
        if(VERBOSE) {
            log.d("%s.%s state=%s", this.getClass().getSimpleName(), "onCreate", str(savedInstanceState));
            log.v(str(mDisplayMetrics));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity);

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
            mGlobalDrawerLayout.setDrawerListener(this);

        if(mLocalDrawerLayout != null)
            mLocalDrawerLayout.setDrawerListener(this);

        //noinspection ConstantConditions
        mGlobalNavigationView.setListener(this);
        //noinspection ConstantConditions
        mLocalNavigationView.setListener(this);

        setSupportActionBar(mToolbar);

        mGlobalArrowDrawable.setStrokeWidth(5).setRotateTo(360f + 180f).setAlpha(0xa0);
        mLocalArrowDrawable.setStrokeWidth(5).setRotateFrom(180f).setAlpha(0xa0);

        //noinspection ConstantConditions
        mToolbar.setNavigationIcon(mGlobalArrowDrawable);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                //noinspection ConstantConditions
                boolean empty = getGlobalNavigation().isEmpty();
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
                boolean empty = getLocalNavigation().isEmpty();
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
            // TODO LATER: isn't it too soon? What if it is not MyFragment (and retaininstance is false)
            // TODO LATER: analyze if findFragmentByTag will always have our fragment ready here.
            updateLocalFragment(f); // can be null.
        }
    }

    private void toggleGlobalNavigation() {
        if(mGlobalDrawerLayout != null)
            toggleDrawer(mGlobalDrawerLayout, GravityCompat.START);
        else if(mGlobalLinearLayout != null)
            toggleMNVAndArrow(mGlobalNavigationView, mGlobalArrowDrawable);
        else
            log.e("No global drawer or linear layout with global navigation.");
    }

    private void toggleLocalNavigation() {
        if(mLocalDrawerLayout != null)
            toggleDrawer(mLocalDrawerLayout, GravityCompat.END);
        else if(mLocalLinearLayout != null)
            toggleMNVAndArrow(mLocalNavigationView, mLocalArrowDrawable);
        else
            log.e("No local drawer or linear layout with local navigation.");
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
        onNavigationContentChanged(getGlobalNavigation());
        onNavigationContentChanged(getLocalNavigation());
    }

    @Override protected void onResume() {
        super.onResume();
        log.setSnackView(mCoordinatorLayout);
    }

    @Override protected void onPause() {
        log.setSnackView(null);
        super.onPause();
    }

    @CallSuper @Override protected void onDestroy() {
        if(VERBOSE)
            log.d("%s.%s", this.getClass().getSimpleName(), "onDestroy");

        mGlobalDrawerLayout = null;
        mGlobalLinearLayout = null;
        mCoordinatorLayout = null;
        mAppBarLayout = null;
        mToolbar = null;
        mLocalDrawerLayout = null;
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

    protected void startPendingCommand() {

        if(mMyPendingCommand == null)
            return;

        onCommand(mMyPendingCommand);

        mMyPendingCommand = null;

    }


    protected void updateLocalFragment(@Nullable Fragment fragment) {
        if(VERY_VERBOSE)
            log.v("%s.%s fragment=%s", this.getClass().getSimpleName(), "updateLocalFragment", str(fragment));

        mLocalFragment = fragment;
        mMyLocalFragment = null;

        if(fragment == null)
            return;

        if(fragment instanceof MyFragment)
            mMyLocalFragment = (MyFragment) fragment;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
     * Override it if you want to manage commands by yourself
     * @param command A command to perform
     */
    public void onCommand(@NonNull String command) {

        command = MyCommands.applyRERulesLists(command, MyCommands.RE_RULES, log);

        Map<String, String> map = new HashMap<>(20);

        MyCommands.parseCommand(command, map);

        String start = map.get("start");
        String component = map.get("component");
        String action = map.get("action");

        String pkg = getPackageName();

        if(start == null) {
            start = DEFAULT_COMMAND_NAME;
            map.put("start", start);
        }

        if(start.equals("activity")) {
            if(component == null && action == null) {
                action = DEFAULT_INTENT_ACTION;
                map.put("action", action);
            }
            if(component != null && !component.contains("/")) {
                component = pkg + "/" + component;
                map.put("component", component);
            }
        }
        else if(map.get("start").equals("fragment")) {
            if(component == null) {
                log.e("Fragment component is null.");
                return;
            }
            if(component.startsWith(".")) { //TODO LATER: test this shortcut later when I have fragments in the same package as app itself..
                component = pkg + component;
                map.put("component", component);
            }

        }

        onCommand(map);
    }

    /**
     * Override it if you want to manage all (parsed) commands by yourself
     * @param command A parsed command
     */
    public void onCommand(@NonNull Map<String, String> command) {

        switch(command.get("start")) {
            case "activity":
                onCommandStartActivity(command);
                break;
            case "service":
                onCommandStartService(command);
                break;
            case "broadcast":
                onCommandStartBroadcast(command);
                break;
            case "fragment":
                onCommandStartFragment(command);
                break;
            //TODO NOW: other commands
            default:
                log.e("Unsupported command: %s", str(command));
        }
    }

    public void onCommandStartActivity(@NonNull Map<String, String> command) {

        Intent intent = new Intent();

        MyCommands.setIntentFromCommand(intent, command, log);

        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                startActivity(intent);
            }
            catch(ActivityNotFoundException e) {
                log.e(e); //FIXME LATER: error message on device shows only time in this case..
                // but still we have full exception details in logcat.
            }
            catch(SecurityException e) {
                log.e(e); //FIXME LATER: error message on device shows only time in this case..
                // but still we have full exception details in logcat.
            }
        }
        else
            log.e("No activity found for this intent: %s", str(intent));
    }

    public void onCommandStartService(@NonNull Map<String, String> command) {

        Intent intent = new Intent();

        MyCommands.setIntentFromCommand(intent, command, log);

        if(startService(intent) == null)
            log.e("Service not found for this intent: %s", str(intent));
    }

    public void onCommandStartBroadcast(@NonNull Map<String, String> command) {

        Intent intent = new Intent();
        MyCommands.setIntentFromCommand(intent, command, log);
        sendBroadcast(intent);
    }

    public void onCommandStartFragment(@NonNull Map<String, String> command) {

        FragmentManager fm = getFragmentManager();
        Fragment f;

        f = Fragment.instantiate(MyActivity.this, command.get("component"));

        //TODO: allow to get some arguments

        updateLocalFragment(f);

        FragmentTransaction ft = fm.beginTransaction().replace(R.id.ma_local_frame_layout, f, TAG_LOCAL_FRAGMENT);
        addAllSharedElementsToFragmentTransaction(findViewById(R.id.ma_local_frame_layout), ft);
        ft.commit();

    }

    /**
     * You can override it, but you should call super version first and do your custom logic only if it returns false.
     */
    @CallSuper @Override public boolean onItemSelected(IMyNavigation nav, MenuItem item) {
        boolean done;
        if(mGlobalDrawerLayout != null)
            mGlobalDrawerLayout.closeDrawers();
        if(mLocalDrawerLayout != null)
            mLocalDrawerLayout.closeDrawers();
        String ctitle = item.getTitleCondensed().toString();
        if(ctitle.startsWith(COMMAND_PREFIX)) {
            mMyPendingCommand = ctitle.substring(COMMAND_PREFIX.length());
            if( (mGlobalDrawerLayout != null && mGlobalDrawerLayout.isDrawerVisible(GravityCompat.START)) ||
                    (mLocalDrawerLayout != null && mLocalDrawerLayout .isDrawerVisible(GravityCompat.END)) )
                return true; // we will start pending command after drawers are closed.
            startPendingCommand();
            return true;
        }

        // maybe our local fragment will handle this item:
        if(mMyLocalFragment != null) {
            done = mMyLocalFragment.onItemSelected(nav, item);
            if(done)
                return true;
        }
        return false;
    }

    @CallSuper protected void onNavigationContentChanged(IMyNavigation nav) {
        boolean empty = nav.isEmpty();
        if(nav == getGlobalNavigation()) {
            mGlobalArrowDrawable.setAlpha(empty ? 0 : 0xa0);
            if(mGlobalDrawerLayout != null)
                mGlobalDrawerLayout.setDrawerLockMode(empty ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
            else if(mGlobalLinearLayout != null)
                setMNVAndArrow(!empty, mGlobalNavigationView, mGlobalArrowDrawable);
            else
                log.e("No global drawer or linear layout with global navigation.");
        }
        else if(nav == getLocalNavigation()) {
            mLocalArrowDrawable.setAlpha(empty ? 0 : 0xa0);
            if(mLocalDrawerLayout != null)
                mLocalDrawerLayout.setDrawerLockMode(empty ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
            else if(mLocalLinearLayout != null)
                setMNVAndArrow(!empty, mLocalNavigationView, mLocalArrowDrawable);
            else
                log.e("No local drawer or linear layout with local navigation.");
        }
        else
            log.e("Unknown IMyNavigation object.");
    }

    @Override public void onClearHeader(IMyNavigation nav) {
        onNavigationContentChanged(nav);
        if(mMyLocalFragment != null)
            mMyLocalFragment.onClearHeader(nav);
    }

    @Override public void onClearMenu(IMyNavigation nav) {
        onNavigationContentChanged(nav);
        if(mMyLocalFragment != null)
            mMyLocalFragment.onClearMenu(nav);
    }

    @Override public void onInflateHeader(IMyNavigation nav) {
        onNavigationContentChanged(nav);
        if(mMyLocalFragment != null)
            mMyLocalFragment.onInflateHeader(nav);
    }

    @Override public void onInflateMenu(IMyNavigation nav) {
        onNavigationContentChanged(nav);
        if(mMyLocalFragment != null)
            mMyLocalFragment.onInflateMenu(nav);
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

    @Override public @Nullable FloatingActionButton getFAB() {
        return mFAB;
    }

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

    public void selectGlobalItem(@IdRes int id) {
        selectItem(getGlobalNavigation(), id);
    }

    public void selectLocalItem(@IdRes int id) {
        selectItem(getLocalNavigation(), id);
    }


    @CallSuper @Override public void onDrawerSlide(View drawerView, float slideOffset) {
        if(drawerView == mGlobalNavigationView) {
            mGlobalArrowDrawable.setLevel((int) scale0d(slideOffset, 1f, 10000f));
        }
        else if(drawerView == mLocalNavigationView) {
            mLocalArrowDrawable.setLevel((int) scale0d(slideOffset, 1f, 10000f));
        }
        if(mMyLocalFragment != null)
            mMyLocalFragment.onDrawerSlide(drawerView, slideOffset);
    }

    @CallSuper @Override public void onDrawerOpened(View drawerView) {
        if(mMyLocalFragment != null)
            mMyLocalFragment.onDrawerOpened(drawerView);
    }

    @CallSuper @Override public void onDrawerClosed(View drawerView) {
        startPendingCommand();
        if(mMyLocalFragment != null)
            mMyLocalFragment.onDrawerClosed(drawerView);
    }

    @CallSuper @Override public void onDrawerStateChanged(int newState) {
        if(mMyLocalFragment != null)
            mMyLocalFragment.onDrawerStateChanged(newState);
    }

    public float dp2px(float dp) {
        if(mDisplayMetrics == null)
            throw new IllegalStateException("display metrics not ready");
        return dp * mDisplayMetrics.density; }
    public float px2dp(float px) {
        if(mDisplayMetrics == null)
            throw new IllegalStateException("display metrics not ready");
        return px / mDisplayMetrics.density;
    }

}
