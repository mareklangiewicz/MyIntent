package pl.mareklangiewicz.myactivities

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.IdRes
import android.view.MenuItem

import pl.mareklangiewicz.myviews.IMyUINavigation

/**
 * Created by Marek Langiewicz on 22.09.15.
 * Simple example activity using MyActivity features...
 */
class MyExampleActivity : MyActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log.i("Hi, I am the example activity..")
        gnav?.menuId = R.menu.ma_my_example_global
        gnav?.headerId = R.layout.ma_my_example_global_header
        if (savedInstanceState == null)
            gnav?.setCheckedItem(R.id.ma_meg_i_my_example_fragment_1, true)
    }

    override fun onItemSelected(nav: IMyUINavigation, item: MenuItem): Boolean {
        if (super.onItemSelected(nav, item))
            return true
        if (item.itemId == R.id.ma_meg_i_my_example_action) {
            log.i("[SNACK][SHORT]Example: ACTION!")
            return true
        }
        return false
    }
}
