package pl.mareklangiewicz.myviews

import androidx.annotation.IdRes
import android.view.Menu
import android.view.MenuItem
import android.view.View
import pl.mareklangiewicz.myutils.IChanges
import pl.mareklangiewicz.myutils.Relay

/**
 * Created by Marek Langiewicz on 02.09.15.
 * An interface usually implemented by our navigation views and used by fragments.
 */
interface IMyUINavigation : IChanges<Unit> {

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

    val empty: Boolean

    override val changes: Relay<Unit> // triggerd when navigation view content has changed

    val items: Relay<Int> // emits ids of selected items
}
