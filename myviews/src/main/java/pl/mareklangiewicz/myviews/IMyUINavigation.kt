package pl.mareklangiewicz.myviews

import android.support.annotation.IdRes
import android.view.Menu
import android.view.MenuItem
import android.view.View

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
    val empty: Boolean
    var listener: Listener?

//    val changes: IPusher<Unit, (Unit) -> Unit>

    interface Listener {
        fun onItemSelected(nav: IMyUINavigation, item: MenuItem): Boolean  // TODO NOW: change to items: IPusher<Int>
        fun onNavigationChanged(nav: IMyUINavigation)
        // TODO NOW: change to changes: IPusher<Unit> (we can later use map to attach nav; then merge? to merge global and local navigation if needed..
    }

}
