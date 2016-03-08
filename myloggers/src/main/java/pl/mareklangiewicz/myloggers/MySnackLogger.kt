package pl.mareklangiewicz.myloggers

import android.support.design.widget.Snackbar
import android.view.View
import pl.mareklangiewicz.myutils.MyLogEntry
import snack

/**
 * Created by Marek Langiewicz on 07.03.16.
 * Displays every given log message that matches given prefix on snackbar
 * You have to specify a View object used to find root view for Snackbar
 * This function should be last in loggers "IPushee chain", so it is invoked first for every log message.
 * Important: this last step should be rebuild every time we have to reconnect to new View to avoid memleaks.
 */

const val SNACK_TAG = "[SNACK]"
const val SHORT_TAG = "[SHORT]"
const val INDEF_TAG = "[INDEF]"

fun Function1<MyLogEntry, Unit>.snack(view: View): Function1<MyLogEntry, Unit> = {
    var msg = it.message.removePrefix(SNACK_TAG)
    if(msg !== it.message) {
        var length = Snackbar.LENGTH_LONG
        if (msg.startsWith(SHORT_TAG)) {
            length = Snackbar.LENGTH_SHORT
            msg = msg.substring(SHORT_TAG.length)
        } else if (msg.startsWith(INDEF_TAG)) {
            length = Snackbar.LENGTH_INDEFINITE
            msg = msg.substring(INDEF_TAG.length)
        }
        view.snack(msg, length) {}
    }
    this@snack(it.copy(message = msg))
}

