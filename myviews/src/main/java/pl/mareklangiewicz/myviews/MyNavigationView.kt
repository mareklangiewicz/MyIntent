package pl.mareklangiewicz.myviews

import android.content.Context
import androidx.annotation.IdRes
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.material.navigation.NavigationView
import pl.mareklangiewicz.myloggers.MY_DEFAULT_ANDRO_LOGGER
import pl.mareklangiewicz.myutils.*

class MyNavigationView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : NavigationView(context, attrs, defStyle), IMyUINavigation, NavigationView.OnNavigationItemSelectedListener {

    init { super.setNavigationItemSelectedListener(this) }

    val log = MY_DEFAULT_ANDRO_LOGGER

    override var menuId = -1
        set(id) {
            if (id == field) return
            if (field != -1) super.getMenu().clear()
            field = id
            if (id != -1) super.inflateMenu(id)
            changes.push(Unit)
        }

    override val menuObj: Menu? get() = if (super.getMenu().size() == 0) null else super.getMenu()


    override var headerId = -1
        set(id) {
            if (id == field) return
            if (field != -1) removeHeaderView(getHeaderView(0))
            field = id
            if (id != -1) inflateHeaderView(id)
            changes.push(Unit)
        }

    override val headerObj: View? get() = if (headerCount == 0) null else getHeaderView(0)

    override val empty: Boolean get() = menuId == -1 && headerId == -1

    override val changes = Relay<Unit>()
    override val items = Relay<Int>()

    override fun overlaps(view: View?) = (this as View).overlaps(view)

    /**
     * Finds (recursively: DFS) first checked item in navigation view menu.
     * WARNING: do not call it too soon!
     * it works correctly from Fragment.onResume.
     * it does NOT work correctly from Fragment.onViewStateRestored!!!
     * @return A first checked menu item or null if there is none...
     */
    override val firstCheckedItem: MenuItem?
        get() {
            val menu = super.getMenu()
            return getFirstCheckedItem(menu)
        }

    private fun getFirstCheckedItem(menu: Menu): MenuItem? {
        val size = menu.size()
        for (i in 0..size - 1) {
            val item = menu.getItem(i)
            if (item.isChecked)
                return item
            if (item.hasSubMenu()) {
                val submenu = item.subMenu
                if (submenu != null) {
                    val fci = getFirstCheckedItem(submenu)
                    if (fci != null)
                        return fci
                }
            }
        }
        return null
    }


    /**
     * @param id The ID of checked item.
     * @param callback - if true: it additionally pushes item id to "items" relay
     */
    override fun setCheckedItem(@IdRes id: Int, callback: Boolean) {
        val item: MenuItem? = super.getMenu().findItem(id)
        if(item === null) {
            log.e("Item not found.")
            return
        }
        super.setCheckedItem(id)
        if (callback)
            onNavigationItemSelected(item)
    }

    override fun setNavigationItemSelectedListener(listener: OnNavigationItemSelectedListener?) {
        throw IllegalAccessError("This method is blocked. Use setListener.")
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val id = menuItem.itemId
        if(id == 0)
            log.d("Menu item without ID selected. Ignoring.")
        else
            items.push(id)
        return true
    }
}
