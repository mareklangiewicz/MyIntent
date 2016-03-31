package pl.mareklangiewicz.myactivities

import android.app.Fragment
import android.app.FragmentTransaction
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.CallSuper
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.transition.AutoTransition
import android.transition.Fade
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.ma_fab.*
import pl.mareklangiewicz.mydrawables.MyArrowDrawable
import pl.mareklangiewicz.mydrawables.MyLivingDrawable
import pl.mareklangiewicz.myfragments.MyFragment
import pl.mareklangiewicz.myloggers.MY_DEFAULT_ANDRO_LOGGER
import pl.mareklangiewicz.myutils.*
import pl.mareklangiewicz.myviews.IMyUIManager
import pl.mareklangiewicz.myviews.IMyUINavigation
import pl.mareklangiewicz.myviews.MyNavigationView

open class MyActivity : AppCompatActivity(), IMyUIManager, IMyUINavigation.Listener, DrawerLayout.DrawerListener {


    val COMMAND_PREFIX = "cmd:"
    val TAG_LOCAL_FRAGMENT = "tag_local_fragment"

//    private val V = BuildConfig.VERBOSE;
//    private val VV = BuildConfig.VERY_VERBOSE;
//
////    FIXME SOMEDAY: enable version with BuildConfig when Google fix issue with propagating build types to libraries.
////    Now it is always 'release' in libraries.. see:
////    https://code.google.com/p/android/issues/detail?id=52962
////    http://stackoverflow.com/questions/20176284/buildconfig-debug-always-false-when-building-library-projects-with-gradle
////    http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Library-Publication

    private val V = true
    private val VV = false
    private val DEFAULT_COMMAND_NAME = CMD_ACTIVITY
    private val DEFAULT_INTENT_ACTION = Intent.ACTION_VIEW

    // it will be changed to true in onCreate (after invoking setContentView) and back to false in onDestroy
    // kotlin extensions are working correctly when it is true.
    protected var isViewAvailable = false

    protected val mGlobalArrowDrawable: MyLivingDrawable = MyArrowDrawable().apply {
        strokeWidth = 5f
        rotateTo = 360f + 180f
        alpha = 0xa0
    }

    protected val mLocalArrowDrawable: MyLivingDrawable = MyArrowDrawable().apply {
        strokeWidth = 5f
        rotateFrom = 180f
        alpha = 0xa0
    }

    /**
     * Default logger for use in UI thread
     */
    protected val log = MY_DEFAULT_ANDRO_LOGGER

    // TODO NOW: use lateinit where possible

    private lateinit var metrics: DisplayMetrics
    private var mGlobalDrawerLayout: DrawerLayout? = null
    private var mGlobalLinearLayout: LinearLayout? = null // either this or mGlobalDrawerLayout will remain null
    private var mLocalDrawerLayout: DrawerLayout? = null
    private var mLocalLinearLayout: LinearLayout? = null // either this or mLocalDrawerLayout will remain null
    private var mCoordinatorLayout: CoordinatorLayout? = null
    private var mAppBarLayout: AppBarLayout? = null
    private var mToolbar: Toolbar? = null
    private var mLocalFrameLayout: FrameLayout? = null
    private var mLocalNavigationView: MyNavigationView? = null
    private var mGlobalNavigationView: MyNavigationView? = null

    override val gnav: IMyUINavigation? get() = mGlobalNavigationView
    override val lnav: IMyUINavigation? get() = mLocalNavigationView

    protected var mLocalArrowView: View? = null

    protected var mLocalFragment: Fragment? = null
    protected var mMyLocalFragment: MyFragment? = null // the same as mLocalFragment - if mLocalFragment instanceof MyFragment - or null otherwise..

    lateinit var handler: Handler

    override var name: String
        get() = title.toString()
        set(value) { title = value }

    override val fab: FloatingActionButton? get() = if(isViewAvailable) ma_fab else null

    @CallSuper override fun onCreate(savedInstanceState: Bundle?) {

        handler = Handler()

        metrics = resources.displayMetrics
        if (V) {
            log.d("${javaClass.simpleName}.onCreate state=${savedInstanceState.str}")
            log.v(metrics.str)
        }

        super.onCreate(savedInstanceState)

        setContentView(R.layout.ma_my_activity)

        isViewAvailable = true


        // TODO NOW: use lateinit and kotlin extensions where possible

        mGlobalDrawerLayout = findViewById(R.id.ma_global_drawer_layout) as DrawerLayout?
        mGlobalLinearLayout = findViewById(R.id.ma_global_linear_layout) as LinearLayout?
        mCoordinatorLayout = findViewById(R.id.ma_coordinator_layout) as CoordinatorLayout?
        mAppBarLayout = findViewById(R.id.ma_app_bar_layout) as AppBarLayout?
        mToolbar = findViewById(R.id.ma_toolbar) as Toolbar?
        mLocalDrawerLayout = findViewById(R.id.ma_local_drawer_layout) as DrawerLayout?
        mLocalLinearLayout = findViewById(R.id.ma_local_linear_layout) as LinearLayout?
        mLocalFrameLayout = findViewById(R.id.ma_local_frame_layout) as FrameLayout?
        mGlobalNavigationView = findViewById(R.id.ma_global_navigation_view) as MyNavigationView?
        mLocalNavigationView = findViewById(R.id.ma_local_navigation_view) as MyNavigationView?

        mGlobalDrawerLayout?.let { it.addDrawerListener(this) }
        mLocalDrawerLayout?.let { it.addDrawerListener(this) }

        mGlobalNavigationView!!.listener = this
        mLocalNavigationView!!.listener = this

        setSupportActionBar(mToolbar)

        mToolbar!!.navigationIcon = mGlobalArrowDrawable
        mToolbar!!.setNavigationOnClickListener {
            if (!gnav!!.empty)
                toggleGlobalNavigation()
            else
                log.d("Global navigation is empty.")
        }

        mLocalArrowView = View(this).apply {
            background = mLocalArrowDrawable
            setOnClickListener {
                if (!lnav!!.empty)
                    toggleLocalNavigation()
                else
                    log.d("Local navigation is empty.")
            }
        }

        val h = mToolbar!!.minimumHeight * 3 / 4
        mLocalArrowView!!.layoutParams = Toolbar.LayoutParams(h, h, GravityCompat.END)
        mToolbar!!.addView(mLocalArrowView)

        if (savedInstanceState != null) {
            val fm = fragmentManager
            val f = fm.findFragmentByTag(TAG_LOCAL_FRAGMENT)
            updateLocalFragment(f) // can be null.
        }
    }

    private fun checkFirstCheckableItemWithCommand(menu: Menu, command: String): Boolean {
        for (i in 0..menu.size() - 1) {
            val item = menu.getItem(i)
            if (item.isCheckable && item.titleCondensed == "cmd:" + command) {
                item.isChecked = true
                return true
            }
            if (item.hasSubMenu()) {
                val submenu = item.subMenu
                if (submenu != null) {
                    val done = checkFirstCheckableItemWithCommand(submenu, command)
                    if (done) return true
                }
            }
        }
        return false
    }

    private fun toggleGlobalNavigation() {
        hideKeyboard()
        if (mGlobalDrawerLayout != null)
            toggleDrawer(mGlobalDrawerLayout!!, GravityCompat.START)
        else if (mGlobalLinearLayout != null)
            toggleMNVAndArrow(mGlobalNavigationView!!, mGlobalArrowDrawable)
        else
            log.a("No global drawer or linear layout with global navigation.")
    }

    private fun toggleLocalNavigation() {
        hideKeyboard()
        if (mLocalDrawerLayout != null)
            toggleDrawer(mLocalDrawerLayout!!, GravityCompat.END)
        else if (mLocalLinearLayout != null)
            toggleMNVAndArrow(mLocalNavigationView!!, mLocalArrowDrawable)
        else
            log.a("No local drawer or linear layout with local navigation.")
    }

    private fun toggleDrawer(drawerLayout: DrawerLayout, gravity: Int) {
        if (drawerLayout.getDrawerLockMode(gravity) == DrawerLayout.LOCK_MODE_UNLOCKED) {
            if (drawerLayout.isDrawerVisible(gravity))
                drawerLayout.closeDrawer(gravity)
            else
                drawerLayout.openDrawer(gravity)
        } else
            log.e("Drawer is locked.")
    }

    private fun toggleMNVAndArrow(mnv: MyNavigationView, arrow: MyLivingDrawable) = setMNVAndArrow(arrow.level < 10000, mnv, arrow)

    private fun setMNVAndArrow(open: Boolean, mnv: MyNavigationView, arrow: MyLivingDrawable) {
        /*  Scene transitions disabled - flickering issue..
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    TransitionManager.beginDelayedTransition((ViewGroup)mnv.getParent(), new Fade());
                }
        */
        mnv.visibility = if (open) View.VISIBLE else View.GONE
        arrow.level = if (open) 10000 else 0
    }

    @CallSuper override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mGlobalDrawerLayout?.let { onDrawerSlide(mGlobalNavigationView!!, if (it.isDrawerOpen(GravityCompat.START)) 1f else 0f) }
        mLocalDrawerLayout?.let { onDrawerSlide(mLocalNavigationView!!, if (it.isDrawerOpen(GravityCompat.END)) 1f else 0f) }
        // ensure that drawers and menu icons are updated:
        onNavigationChanged(gnav!!)
        onNavigationChanged(lnav!!)
        if (savedInstanceState == null)
            onIntent(intent)
    }

    @CallSuper override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // update saved intent in case we need to call getIntent() later..
        onIntent(intent)
    }


    @CallSuper open fun onIntent(intent: Intent?) {
        if (VV) log.v("${javaClass.simpleName}.onIntent: ${intent.str}")
    }

    @CallSuper override fun onStart() {
        if (VV) log.v("${javaClass.simpleName}.onStart")
        log.view = mCoordinatorLayout
        super.onStart()
    }

    @CallSuper override fun onResume() {
        if (VV) log.v("${javaClass.simpleName}.onResume")
        log.view = mCoordinatorLayout
        super.onResume()
    }

    @CallSuper override fun onPause() {
        if (VV) log.v("${javaClass.simpleName}.onPause")
        if (log.view === mCoordinatorLayout) log.view = null
        super.onPause()
    }

    @CallSuper override fun onStop() {
        if (VV) log.v("${javaClass.simpleName}.onStop")
        if (log.view === mCoordinatorLayout) log.view = null
        super.onStop()
    }

    @CallSuper override fun onDestroy() {

        if (V) log.v("${javaClass.simpleName}.onDestroy")

        if (log.view === mCoordinatorLayout) log.view = null

        mGlobalDrawerLayout?.removeDrawerListener(this)
        mLocalDrawerLayout?.removeDrawerListener(this)

        mGlobalDrawerLayout = null
        mLocalDrawerLayout = null
        mGlobalLinearLayout = null
        mCoordinatorLayout = null
        mAppBarLayout = null
        mToolbar = null
        mLocalLinearLayout = null
        mLocalFrameLayout = null
        mLocalNavigationView = null
        mGlobalNavigationView = null
        mLocalArrowView = null

        updateLocalFragment(null)

        isViewAvailable = false

        super.onDestroy()
    }


    protected fun updateLocalFragment(fragment: Fragment?) {

        if (VV) log.v("${javaClass.simpleName}.updateLocalFragment fragment=${fragment.str}")

        mLocalFragment = fragment
        mMyLocalFragment = null

        fragment?.apply {
            mMyLocalFragment = this as? MyFragment
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                enterTransition = Fade()
                exitTransition = Fade()
                sharedElementEnterTransition = AutoTransition()
                sharedElementReturnTransition = AutoTransition()
            }
        }
    }


    /**
     * @param cmd A command to perform
     */
    private fun onCommand(cmd: String) {

        gnav?.menuObj?.let { checkFirstCheckableItemWithCommand(it, cmd) }

        try {
            val mycmd = MyCommand(cmd, RE_RULES, log)
            mycmd["start"] = mycmd["start"] ?: DEFAULT_COMMAND_NAME

            val pkg = packageName

            when(mycmd["start"]) {
                CMD_ACTIVITY -> {
                    if (mycmd["component"] === null) mycmd["action"] = mycmd["action"] ?: DEFAULT_INTENT_ACTION
                    else if (!mycmd["component"]!!.contains("/")) mycmd["component"] = pkg + "/" + mycmd["component"]
                }
                CMD_FRAGMENT -> {
                    if (mycmd["component"] === null) {
                        log.e("Fragment component is null.")
                        return
                    }
                    if (mycmd["component"]!!.startsWith(".")) mycmd["component"] = pkg + mycmd["component"]!!
                }
            }
            onCommand(mycmd)
        } catch (e: RuntimeException) {
            log.e("Invalid command: $cmd", e)
        }
    }


    /**
     * @param command A parsed command
     */
    protected open fun onCommand(command: MyCommand) = when (command["start"]) {
        CMD_ACTIVITY -> onCommandStartActivity(command)
        CMD_SERVICE -> onCommandStartService(command)
        CMD_BROADCAST -> onCommandStartBroadcast(command)
        CMD_FRAGMENT -> onCommandStartFragment(command)
        CMD_CUSTOM -> onCommandCustom(command)
        CMD_NOTHING -> {}  // just for dry testing purposes
        else -> log.e("Unsupported command: ${command.str}")
    }

    protected open fun onCommandStartActivity(command: MyCommand) {
        try {
            val intent = command.toIntent()
            if (intent.resolveActivity(packageManager) != null) startActivity(intent)
            else log.e("No activity found for this intent: ${intent.str}")
        }
        catch (e: IllegalArgumentException) { log.e("Illegal command: ${command.str}", e) }
        catch (e: ActivityNotFoundException) { log.e("Activity not found.", e) }
        catch (e: SecurityException) { log.a("Security exception.", e) }
    }

    protected open fun onCommandStartService(command: MyCommand) {
        try {
            val intent = command.toIntent()
            if (startService(intent) === null) log.e("Service not found for this intent: ${intent.str}")
        }
        catch (e: IllegalArgumentException) { log.e("Illegal command: ${command.str}", e) }
        catch (e: SecurityException) { log.a("Security exception.", e) }
    }

    protected open fun onCommandStartBroadcast(command: MyCommand) {
        try { sendBroadcast(command.toIntent()) }
        catch (e: IllegalArgumentException) { log.e("Illegal command: ${command.str}", e) }
    }

    /**
     * Extras in fragment command are delivered as fragment arguments bundle.
     */
    protected open fun onCommandStartFragment(command: MyCommand) {
        try {
            val fm = fragmentManager
            val f = Fragment.instantiate(this@MyActivity, command["component"])
            val args = command.toExtrasBundle()
            if (args.size() > 0) f.arguments = args
            updateLocalFragment(f)
            val ft = fm.beginTransaction().replace(R.id.ma_local_frame_layout, f, TAG_LOCAL_FRAGMENT)
            addAllSharedElementsToFragmentTransaction(findViewById(R.id.ma_local_frame_layout)!!, ft)
            ft.commit()
        }
        catch (e: Fragment.InstantiationException) { log.e("Fragment class: ${command["component"]} not found.", e) }
        catch (e: IllegalArgumentException) { log.e("Illegal command: " + (command).str, e) }

    }

    protected open fun onCommandCustom(command: MyCommand) = log.e("Unsupported custom command: ${command.str}")

    fun closeDrawers() {
        mGlobalDrawerLayout?.closeDrawers()
        mLocalDrawerLayout?.closeDrawers()
    }

    fun areDrawersVisible(): Boolean
            = (mGlobalDrawerLayout?.isDrawerVisible(GravityCompat.START) ?: false)
            || (mLocalDrawerLayout?.isDrawerVisible(GravityCompat.END) ?: false)

    inline protected fun closeDrawersAnd(crossinline task: () -> Unit) {
        if (areDrawersVisible()) {
            closeDrawers()
            handler.postDelayed({ task() }, 400)
        } else
            handler.post { task() }
    }

    fun execute(cmd: String) = closeDrawersAnd { onCommand(cmd) }


    /**
     * You can override it, but you should call super version first and do your custom logic only if it returns false.
     */
    @CallSuper override fun onItemSelected(nav: IMyUINavigation, item: MenuItem): Boolean {
        val ctitle = item.titleCondensed.toString()
        if (ctitle.startsWith(COMMAND_PREFIX)) {
            val cmd = ctitle.substring(COMMAND_PREFIX.length)
            execute(cmd)
            return true
        }

        closeDrawers()
        // maybe our local fragment will handle this item:
        return mMyLocalFragment?.run { onItemSelected(nav, item) } ?: false
    }


    @CallSuper override fun onNavigationChanged(nav: IMyUINavigation) {
        val empty = nav.empty
        when (nav) {
            gnav -> {
                mGlobalArrowDrawable.alpha = if (empty) 0 else 0xa0
                mGlobalDrawerLayout?.setDrawerLockMode(if (empty) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED)
                if (mGlobalLinearLayout !== null) setMNVAndArrow(!empty, mGlobalNavigationView!!, mGlobalArrowDrawable)
            }
            lnav -> {
                mLocalArrowDrawable.alpha = if (empty) 0 else 0xa0
                mLocalDrawerLayout?.setDrawerLockMode(if (empty) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED)
                if (mLocalLinearLayout !== null) setMNVAndArrow(!empty, mLocalNavigationView!!, mLocalArrowDrawable)
            }
            else -> log.a("Unknown IMyUINavigation object.")
        }
        mMyLocalFragment?.onNavigationChanged(nav)
    }

    private fun addAllSharedElementsToFragmentTransaction(root: View, ft: FragmentTransaction) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val name = root.transitionName
            if (name != null)
                ft.addSharedElement(root, name)
            else if (root is ViewGroup) {
                for (i in 0..root.childCount - 1)
                    addAllSharedElementsToFragmentTransaction(root.getChildAt(i), ft)
            }
        } else {
            log.d("Can not add shared elements to fragment transaction. API < 21")
        }
    }

    @CallSuper override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        if (drawerView === mGlobalNavigationView) mGlobalArrowDrawable.level = scale0d(slideOffset, 1f, 10000f).toInt()
        else if (drawerView === mLocalNavigationView) mLocalArrowDrawable.level = scale0d(slideOffset, 1f, 10000f).toInt()
        mMyLocalFragment?.onDrawerSlide(drawerView, slideOffset)
    }

    @CallSuper override fun onDrawerOpened(drawerView: View) {
        hideKeyboard()
        mMyLocalFragment?.onDrawerOpened(drawerView)
    }

    @CallSuper override fun onDrawerClosed(drawerView: View) {
        mMyLocalFragment?.onDrawerClosed(drawerView)
    }

    @CallSuper override fun onDrawerStateChanged(newState: Int) {
        mMyLocalFragment?.onDrawerStateChanged(newState)
    }


    @CallSuper
    override fun onBackPressed() {
        mGlobalDrawerLayout?.run { if(isDrawerOpen(GravityCompat.START)) { closeDrawer(GravityCompat.START); return } }
        mLocalDrawerLayout?.run { if(isDrawerOpen(GravityCompat.END)) { closeDrawer(GravityCompat.END); return } }
        super.onBackPressed()
    }

    fun dp2px(dp: Float): Float = dp * metrics.density

    @Suppress("unused")
    fun px2dp(px: Float): Float = px / metrics.density

}
