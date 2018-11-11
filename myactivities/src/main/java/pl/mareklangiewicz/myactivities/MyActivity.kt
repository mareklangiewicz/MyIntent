package pl.mareklangiewicz.myactivities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction






import android.transition.AutoTransition
import android.transition.Fade
import android.util.DisplayMetrics
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.ma_app_bar_layout.*
import kotlinx.android.synthetic.main.ma_fab.*
import kotlinx.android.synthetic.main.ma_local_drawer_layout.*
import kotlinx.android.synthetic.main.ma_my_activity.*
import pl.mareklangiewicz.mydrawables.MyArrowDrawable
import pl.mareklangiewicz.mydrawables.MyLivingDrawable
import pl.mareklangiewicz.myfragments.MyFragment
import pl.mareklangiewicz.myloggers.MY_DEFAULT_ANDRO_LOGGER
import pl.mareklangiewicz.myutils.*
import pl.mareklangiewicz.myviews.IMyUIManager
import pl.mareklangiewicz.myviews.IMyUINavigation
import pl.mareklangiewicz.myviews.MyNavigationView
import kotlin.collections.set

open class MyActivity : AppCompatActivity(), IMyUIManager, DrawerLayout.DrawerListener {


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

    val SRC_PKG = this.javaClass.name.substringBeforeLast('.', "")
    lateinit var APP_ID: String

    // it will be changed to true in onCreate (after invoking setContentView) and back to false in onDestroy
    // kotlin extensions are working correctly when it is true.
    protected var isViewAvailable = false

    protected val garrow: MyLivingDrawable = MyArrowDrawable().apply {
        strokeWidth = 4f
        rotateTo = 360f + 180f
        alpha = 0xa0
    }

    protected val larrow: MyLivingDrawable = MyArrowDrawable().apply {
        strokeWidth = 4f
        rotateFrom = 180f
        alpha = 0xa0
    }

    /**
     * Default logger for use in UI thread
     */
    protected val log = MY_DEFAULT_ANDRO_LOGGER

    private lateinit var metrics: DisplayMetrics

    override val gnav: IMyUINavigation? get() = if(isViewAvailable) ma_global_navigation_view else null
    override val lnav: IMyUINavigation? get() = if(isViewAvailable) ma_local_navigation_view else null

    protected var fgmt: Fragment? = null

    lateinit var handler: Handler

    override var name: String
        get() = title.toString()
        set(value) { title = value }

    override val fab: FloatingActionButton? get() = if(isViewAvailable) ma_fab else null

    private var gdraw = false
    private var ldraw = false

    @CallSuper override fun onCreate(savedInstanceState: Bundle?) {

        APP_ID = packageName

        handler = Handler()

        metrics = resources.displayMetrics
        if (V) {
            log.d("${javaClass.simpleName}.onCreate state=${savedInstanceState.str}")
            log.v(metrics.str)
        }

        super.onCreate(savedInstanceState)

        setContentView(R.layout.ma_my_activity)

        isViewAvailable = true

        gdraw = ma_coordinator_layout.tag != "w1120dp"
        ldraw = ma_coordinator_layout.tag == ""

        if(gdraw) ma_global_drawer_layout!!.addDrawerListener(this)
        if(ldraw) ma_local_drawer_layout.addDrawerListener(this)



        gnav!!.changes { // we ignore returned subscription - navigation will live as long as activity
            val empty = gnav!!.empty
            garrow.alpha = if (empty) 0 else 0xa0
            if(gdraw) ma_global_drawer_layout!!.setDrawerLockMode(if (empty) LOCK_MODE_LOCKED_CLOSED else LOCK_MODE_UNLOCKED)
            else setMNVAndArrow(!empty, ma_global_navigation_view, garrow)
        }
        lnav!!.changes { // we ignore returned subscription - navigation will live as long as activity
            val empty = lnav!!.empty
            larrow.alpha = if (empty) 0 else 0xa0
            if(ldraw) ma_local_drawer_layout.setDrawerLockMode(if (empty) LOCK_MODE_LOCKED_CLOSED else LOCK_MODE_UNLOCKED)
            else setMNVAndArrow(!empty, ma_local_navigation_view, larrow)
        }
        // TODO SOMEDAY: we should merge these two "streams" and subscribe with one common lambda - I'll do it when Pue is more mature


        gnav!!.items { // we ignore returned subscription - navigation will live as long as activity
            val title = gnav?.menuObj?.findItem(it)?.titleCondensed?.toString()
            if(title !== null && title.startsWith(COMMAND_PREFIX))
                execute(title.substring(COMMAND_PREFIX.length))
            else
                closeDrawers()
        }
        lnav!!.items { // we ignore returned subscription - navigation will live as long as activity
            val title = lnav?.menuObj?.findItem(it)?.titleCondensed?.toString()
            if(title !== null && title.startsWith(COMMAND_PREFIX))
                execute(title.substring(COMMAND_PREFIX.length))
            else
                closeDrawers()
        }
        // TODO SOMEDAY: we should merge these two "streams" and subscribe with one common lambda - I'll do it when Pue is more mature


        setSupportActionBar(ma_toolbar)

        ma_toolbar.navigationIcon = garrow
        ma_toolbar.setNavigationOnClickListener {
            if (!gnav!!.empty)
                toggleGlobalNavigation()
            else
                log.d("Global navigation is empty.")
        }

        val laview = View(this).apply {
            background = larrow
            setOnClickListener {
                if (!lnav!!.empty)
                    toggleLocalNavigation()
                else
                    log.d("Local navigation is empty.")
            }
        }

        val h = ma_toolbar.minimumHeight * 3 / 4
        laview.layoutParams = Toolbar.LayoutParams(h, h, GravityCompat.END)
        ma_toolbar.addView(laview)

        if (savedInstanceState != null) {
            val fm = supportFragmentManager
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
        if (gdraw) toggleDrawer(ma_global_drawer_layout!!, GravityCompat.START)
        else toggleMNVAndArrow(ma_global_navigation_view, garrow)
    }

    private fun toggleLocalNavigation() {
        hideKeyboard()
        if (ldraw) toggleDrawer(ma_local_drawer_layout, GravityCompat.END)
        else toggleMNVAndArrow(ma_local_navigation_view, larrow)
    }

    private fun toggleDrawer(drawerLayout: DrawerLayout, gravity: Int) {
        if (drawerLayout.getDrawerLockMode(gravity) == LOCK_MODE_UNLOCKED) {
            if (drawerLayout.isDrawerVisible(gravity))
                drawerLayout.closeDrawer(gravity)
            else
                drawerLayout.openDrawer(gravity)
        } else
            log.e("Drawer is locked.")
    }

    private fun toggleMNVAndArrow(mnv: MyNavigationView, arrow: MyLivingDrawable) = setMNVAndArrow(arrow.level < 10000, mnv, arrow)

    private fun setMNVAndArrow(open: Boolean, mnv: MyNavigationView, arrow: MyLivingDrawable) {
        // Scene transitions disabled - flickering issue..
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) TransitionManager.beginDelayedTransition(mnv.parent as ViewGroup, Fade())
        mnv.visibility = if (open) View.VISIBLE else View.GONE
        arrow.level = if (open) 10000 else 0
    }

    @CallSuper override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if(gdraw) onDrawerSlide(ma_global_navigation_view, if (ma_global_drawer_layout!!.isDrawerOpen(GravityCompat.START)) 1f else 0f)
        if(ldraw) onDrawerSlide(ma_local_navigation_view, if (ma_local_drawer_layout.isDrawerOpen(GravityCompat.END)) 1f else 0f)
        // ensure that drawers and menu icons are updated:
        gnav!!.changes.push(Unit)
        lnav!!.changes.push(Unit)
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
        log.view = ma_coordinator_layout
        super.onStart()
    }

    @CallSuper override fun onResume() {
        if (VV) log.v("${javaClass.simpleName}.onResume")
        log.view = ma_coordinator_layout
        super.onResume()
    }

    @CallSuper override fun onPause() {
        if (VV) log.v("${javaClass.simpleName}.onPause")
        if (log.view === ma_coordinator_layout) log.view = null
        super.onPause()
    }

    @CallSuper override fun onStop() {
        if (VV) log.v("${javaClass.simpleName}.onStop")
        if (log.view === ma_coordinator_layout) log.view = null
        super.onStop()
    }

    @CallSuper override fun onDestroy() {

        if (V) log.v("${javaClass.simpleName}.onDestroy")

        if (log.view === ma_coordinator_layout) log.view = null

        if(gdraw) ma_global_drawer_layout!!.removeDrawerListener(this)
        if(ldraw) ma_local_drawer_layout.removeDrawerListener(this)

        updateLocalFragment(null)

        isViewAvailable = false

        super.onDestroy()
    }


    private fun updateLocalFragment(fragment: Fragment?) {

        if (VV) log.v("${javaClass.simpleName}.updateLocalFragment fragment=${fragment.str}")

        fgmt = fragment

        fragment?.apply {
            enterTransition = Fade()
            exitTransition = Fade()
            sharedElementEnterTransition = AutoTransition()
            sharedElementReturnTransition = AutoTransition()
        }
    }

    private fun String.addPkgIfAbsent() = if(startsWith(".")) SRC_PKG + this else this
    private fun String.addAppIdIfAbsent() = if(!contains("/")) APP_ID + "/" + this else this

    /**
     * @param cmd A command to perform
     */
    private fun onCommand(cmd: String) {

        gnav?.menuObj?.let { checkFirstCheckableItemWithCommand(it, cmd) }

        try {
            val mycmd = MyCommand(cmd, RE_RULES, log)
            mycmd["start"] = mycmd["start"] ?: DEFAULT_COMMAND_NAME

            when(mycmd["start"]) {
                CMD_ACTIVITY -> {
                    if (mycmd["component"] === null) // implicit intent
                        mycmd["action"] = mycmd["action"] ?: DEFAULT_INTENT_ACTION
                    else // explicit intent
                        mycmd["component"] = mycmd["component"]!!.addPkgIfAbsent().addAppIdIfAbsent()
                }
                CMD_FRAGMENT -> {
                    if (mycmd["component"] === null) {
                        log.e("Fragment component is null.")
                        return
                    }
                    mycmd["component"] = mycmd["component"]!!.addPkgIfAbsent()
                }
            }
            onCommand(mycmd)
        } catch (e: RuntimeException) {
            log.e("Invalid command: $cmd", "ML", e)
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
        catch (e: IllegalArgumentException) { log.e("Illegal command: ${command.str}", "ML", e) }
        catch (e: ActivityNotFoundException) { log.e("Activity not found.", "ML", e) }
        catch (e: SecurityException) { log.a("Security exception.", "ML", e) }
    }

    protected open fun onCommandStartService(command: MyCommand) {
        try {
            val intent = command.toIntent()
            if (startService(intent) === null) log.e("Service not found for this intent: ${intent.str}")
        }
        catch (e: IllegalArgumentException) { log.e("Illegal command: ${command.str}", "ML", e) }
        catch (e: SecurityException) { log.a("Security exception.", "ML", e) }
    }

    protected open fun onCommandStartBroadcast(command: MyCommand) {
        try { sendBroadcast(command.toIntent()) }
        catch (e: IllegalArgumentException) { log.e("Illegal command: ${command.str}", "ML", e) }
    }

    /**
     * Extras in fragment command are delivered as fragment arguments bundle.
     */
    protected open fun onCommandStartFragment(command: MyCommand) {
        try {
            val f = Fragment.instantiate(this@MyActivity, command["component"])
            val args = command.toExtrasBundle()
            if (args.size() > 0) f.arguments = args
            updateLocalFragment(f)
            val ft = supportFragmentManager.beginTransaction().replace(R.id.ma_local_frame_layout, f, TAG_LOCAL_FRAGMENT)
            addAllSharedElementsToFragmentTransaction(ma_local_frame_layout, ft)
            ft.commit()
        }
        catch (e: Fragment.InstantiationException) { log.e("Fragment class: ${command["component"]} not found.", "ML", e) }
        catch (e: IllegalArgumentException) { log.e("Illegal command: ${(command).str}", "ML", e) }

    }

    protected open fun onCommandCustom(command: MyCommand) = log.e("Unsupported custom command: ${command.str}")

    fun closeDrawers() {
        if(gdraw) ma_global_drawer_layout!!.closeDrawers()
        if(ldraw) ma_local_drawer_layout.closeDrawers()
    }

    fun areDrawersVisible(): Boolean
            = (gdraw && ma_global_drawer_layout!!.isDrawerVisible(GravityCompat.START))
            || (ldraw && ma_local_drawer_layout.isDrawerVisible(GravityCompat.END))

    inline protected fun closeDrawersAnd(crossinline task: () -> Unit) {
        if (areDrawersVisible()) {
            closeDrawers()
            handler.postDelayed({ task() }, 400)
        } else
            handler.post { task() }
    }

    fun execute(cmd: String) = closeDrawersAnd { onCommand(cmd) }


    private fun addAllSharedElementsToFragmentTransaction(root: View, ft: FragmentTransaction) {
        val name = root.transitionName
        if (name != null)
            ft.addSharedElement(root, name)
        else if (root is ViewGroup) {
            for (i in 0..root.childCount - 1)
                addAllSharedElementsToFragmentTransaction(root.getChildAt(i), ft)
        }
    }

    @CallSuper override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        if (drawerView === ma_global_navigation_view) garrow.level = slideOffset.scale0d(1f, 10000f).toInt()
        else if (drawerView === ma_local_navigation_view) larrow.level = slideOffset.scale0d(1f, 10000f).toInt()
        (fgmt as? MyFragment)?.onDrawerSlide(drawerView, slideOffset)
    }

    @CallSuper override fun onDrawerOpened(drawerView: View) {
        hideKeyboard()
        (fgmt as? MyFragment)?.onDrawerOpened(drawerView)
    }

    @CallSuper override fun onDrawerClosed(drawerView: View) {
        (fgmt as? MyFragment)?.onDrawerClosed(drawerView)
    }

    @CallSuper override fun onDrawerStateChanged(newState: Int) {
        (fgmt as? MyFragment)?.onDrawerStateChanged(newState)
    }


    @CallSuper
    override fun onBackPressed() {
        if(gdraw) if(ma_global_drawer_layout!!.isDrawerOpen(GravityCompat.START)) { ma_global_drawer_layout!!.closeDrawer(GravityCompat.START); return }
        if(ldraw) if(ma_local_drawer_layout.isDrawerOpen(GravityCompat.END)) { ma_local_drawer_layout.closeDrawer(GravityCompat.END); return }
        super.onBackPressed()
    }

    fun dp2px(dp: Float): Float = dp * metrics.density

    @Suppress("unused")
    fun px2dp(px: Float): Float = px / metrics.density

}
