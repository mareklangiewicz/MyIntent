package pl.mareklangiewicz.myviews

import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Created by Marek Langiewicz on 03.09.15.
 * An object that manages ui navigation etc.. usually it's an activity
 */
interface IMyUIManager {
    var name: String
    val fab: FloatingActionButton?
    val gnav: IMyUINavigation?
    val lnav: IMyUINavigation?
    // TODO LATER: maybe define some command consumer here? so we do not couple fragments with MyActivity class directly..
}
