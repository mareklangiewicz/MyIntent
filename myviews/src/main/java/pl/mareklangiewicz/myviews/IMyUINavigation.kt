package pl.mareklangiewicz.myviews

import android.support.annotation.IdRes
import android.view.Menu
import android.view.MenuItem
import android.view.View
import pl.mareklangiewicz.myutils.Relay

/**
 * Created by Marek Langiewicz on 02.09.15.
 * An interface usually implemented by our navigation views and used by fragments.
 */
interface IMyUINavigation {

    var menuId: Int // inflates menu with given id; set to -1 to clear menu
    val menuObj: Menu? // returns null if menu is empty
    var headerId: Int // inflates header layout with given id; set to -1 to clear header
    val headerObj: View?

    fun setCheckedItem(@IdRes id: Int, callback: Boolean)
    fun overlaps(view: View?): Boolean

    /**
     * returns first checked item
     * WARNING: see MyNavigationView.getFirstCheckedItem warning!
     */
    val firstCheckedItem: MenuItem?
    // TODO NOW: combine with setCheckedItem - make it a property (carefully describe semantics) (can we loose callback:bool)
    // TODO NOW: setting new property should NOT trigger "items" relay automatically - user can always do it himself

    val empty: Boolean

    val changes: Relay<Unit> // triggerd when navigation view content has changed

    val items: Relay<Int> // emits ids of selected items
}
