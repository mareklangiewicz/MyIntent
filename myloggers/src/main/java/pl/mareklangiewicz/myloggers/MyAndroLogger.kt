package pl.mareklangiewicz.myloggers

import android.graphics.Canvas
import android.graphics.Paint
import com.noveogroup.android.log.Logger
import com.noveogroup.android.log.PatternHandler
import pl.mareklangiewicz.myutils.IMyLogger
import pl.mareklangiewicz.myutils.MyLogEntry
import pl.mareklangiewicz.myutils.MyLogLevel
import pl.mareklangiewicz.myutils.MyRingBuffer
import java.util.*

/**
 * Created by Marek Langiewicz on 19.02.16.
 */

class MyAndroLogger(
        val level: Logger.Level = Logger.Level.VERBOSE,
        val tagPattern: String = "%logger",
        val messagePattern: String = "%s"
) : IMyLogger {
    val handler = PatternHandler(level, tagPattern, messagePattern)

    private fun ll(level: MyLogLevel): Logger.Level = when(level) {
        MyLogLevel.VERBOSE -> Logger.Level.VERBOSE
        MyLogLevel.DEBUG -> Logger.Level.DEBUG
        MyLogLevel.INFO -> Logger.Level.INFO
        MyLogLevel.WARN -> Logger.Level.WARN
        MyLogLevel.ERROR -> Logger.Level.ERROR
        MyLogLevel.ASSERT -> Logger.Level.ASSERT
    }

    override fun invoke(entry: MyLogEntry) {
        handler.print(entry.tag, ll(entry.level), entry.throwable, entry.message)
    }

}


fun MyLogEntry.draw(canvas: Canvas, x: Int, y: Int, paint: Paint) {
    paint.color = level.color
    canvas.drawText(message, x.toFloat(), y.toFloat(), paint)
}

fun MyRingBuffer<MyLogEntry>.draw(canvas: Canvas, x: Int, y: Int, paint: Paint, lines: Int = size, fade: Boolean = true) {
    var ay = y
    val minY = canvas.clipBounds.top
    val N = lines.coerceAtMost(size)
    for (i in 0..N - 1) {

        this[i].draw(canvas, x, ay, paint)

        ay -= (paint.textSize + 2).toInt()

        if (ay < minY)
            break

        if(fade)
            paint.alpha = paint.alpha * 7 / 8
    }
}

