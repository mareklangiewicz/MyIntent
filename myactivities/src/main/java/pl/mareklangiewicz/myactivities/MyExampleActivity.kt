package pl.mareklangiewicz.myactivities

import android.os.Bundle
import pl.mareklangiewicz.myutils.i

/**
 * Created by Marek Langiewicz on 22.09.15.
 * Simple example activity using MyActivity features...
 */
class MyExampleActivity : MyActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log.i("Hi, I am the example activity..")
        gnav!!.menuId = R.menu.ma_my_example_global
        gnav!!.headerId = R.layout.ma_my_example_global_header
        gnav!!.items { // we ignore returned subscription - navigation will live as long as activity
            if (it == R.id.ma_meg_i_my_example_action) log.i("[SNACK][SHORT]Example: ACTION!")
        }
        if (savedInstanceState == null)
            gnav!!.setCheckedItem(R.id.ma_meg_i_my_example_fragment_1, true)
    }
}
