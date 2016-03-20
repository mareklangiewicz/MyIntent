package pl.mareklangiewicz.myviews

import android.support.design.widget.FloatingActionButton

/**
 * Created by Marek Langiewicz on 03.09.15.
 * An object that manages ui navigation etc.. usually it's an activity
 */
interface IMyUIManager {
    // TODO NOW: zmienic na title zaraz po skotlinowaniu MyActivity (teraz sie nie da bo zle mapowani CharSequence (java.lang <-> kotlin)
    var mytitle: CharSequence
    val fab: FloatingActionButton?
    val gnav: IMyUINavigation?
    val lnav: IMyUINavigation?
    // TODO LATER: maybe define some command consumer here? so we do not couple fragments with MyActivity class directly..
}
