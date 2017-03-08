package pl.mareklangiewicz.myloggers

import android.graphics.Canvas
import android.graphics.Paint
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import pl.mareklangiewicz.myutils.*

/**
 * Created by Marek Langiewicz on 19.02.16.
 * Logging to console with source code line numbers can be slow, so:
 * In release mode use: tosystem = false.
 */

class MyAndroLogger(val tosystem: Boolean = true, val tomemory: Boolean = true) : IPushee<MyLogEntry> {


    val history = MyLogHistory()

    private var logger = compose(null)

    var view: View? = null
        set(value) {
            field = value
            logger = compose(value)
        }


    private fun compose(view: View?): IPushee<MyLogEntry> {

        if(!tosystem && !tomemory)
            return { }

        if(!tosystem)
            return history.push.snack(view)

        val lconsole = MyAndroSystemLogger().trace(14)

        if(tomemory)
            return lconsole.apeek(history.push).snack(view)
        else
            return lconsole.snack(view)
    }

    override fun invoke(entry: MyLogEntry) = logger(entry)

}

/**
 * Simple android logger that logs on console using android.util.Log class. It can be is a bit slow, so use it in debug only.
 * This logger can be used from any thread.
 */
class MyAndroSystemLogger : IPushee<MyLogEntry> {
    override fun invoke(entry: MyLogEntry) {
        val suffix = if(entry.throwable === null) "" else "\n${Log.getStackTraceString(entry.throwable)}"
        Log.println(entry.level.number, entry.tag, entry.message + suffix)
    }
}

/**
 * WARNING: use this one only from main thread!
 * TODO LATER: delete this global logger and use dagger2 to provide appropriate singleton loggers for debug and for release
 */
var MY_DEFAULT_ANDRO_LOGGER = MyAndroLogger()



fun MyLogEntry.draw(canvas: Canvas, x: Int, y: Int, paint: Paint) {
    paint.color = level.color
    canvas.drawText(toString(), x.toFloat(), y.toFloat(), paint)
}

fun IArr<MyLogEntry>.draw(canvas: Canvas, x: Int, y: Int, paint: Paint, lines: Int = len, fade: Boolean = true) {
    var ay = y
    val minY = canvas.clipBounds.top
    val N = lines.coerceAtMost(len)
    for (i in 0..N - 1) {

        this[i].draw(canvas, x, ay, paint)

        ay -= (paint.textSize + 2).toInt()

        if (ay < minY)
            break

        if(fade)
            paint.alpha = paint.alpha * 7 / 8
    }
}

/**
 * Displays every given log message that matches given prefix on snackbar
 * You have to specify a View object used to find root view for Snackbar
 * This function should be last in loggers "IPushee chain", so it is invoked first for every log message.
 * Important: this last step should be rebuild every time we have to reconnect to new View to avoid memleaks.
 */

const val SNACK_TAG = "[SNACK]"
const val SHORT_TAG = "[SHORT]"
const val INDEF_TAG = "[INDEF]"

fun IPushee<MyLogEntry>.snack(view: View?): IPushee<MyLogEntry> = {
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
        view?.snack(msg, length) {}
    }
    this@snack(it.copy(message = msg))
}

