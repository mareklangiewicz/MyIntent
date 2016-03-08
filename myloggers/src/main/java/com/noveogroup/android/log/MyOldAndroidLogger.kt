package com.noveogroup.android.log

import android.graphics.Canvas
import android.graphics.Paint
import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.View
import pl.mareklangiewicz.myutils.IMyLogger
import pl.mareklangiewicz.myutils.MyLogEntry
import pl.mareklangiewicz.myutils.MyLogLevel
import java.util.*

/**
 * Created by Marek Langiewicz on 24.06.15.
 */

// Flagged as UiThread to be on the safe side - we will change this requirement if needed.
@UiThread
@Deprecated("")
class MyOldAndroidLogger(
        name: String = Utils.getCallerClassName(),
        level: Logger.Level = Logger.Level.VERBOSE,
        tagPattern: String = "%logger",
        messagePattern: String = "%s"
) : AbstractLogger(name), IMyLogger {

    private val mHandler: MyHandler = MyHandler(level, tagPattern, messagePattern)

    /**
     * WARNING: remember to set it back to null if the view is not used anymore - to avoid memory leaks
     * WARNING: use MyAndroidLogger from UI thread only - if you want to use this feature.
     */
    var snackView: View?
        get() = mHandler.snackView
        set(snackView) {
            mHandler.snackView = snackView
        }

    /**
     * WARNING: remember to set it back to null if the view is not used anymore - to avoid memory leaks
     * WARNING: use MyAndroidLogger from UI thread only - if you want to use this feature.
     */
    var invalidateView: View?
        get() = mHandler.invalidateView
        set(invalidateView) {
            mHandler.invalidateView = invalidateView
        }

    /**
     * WARNING: remember to set it back to null if the adapter is not used anymore - to avoid memory leaks
     * WARNING: use MyAndroidLogger from UI thread only - if you want to use this feature.
     */
    fun setAdapter(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) {
        mHandler.setAdapter(adapter)
    }

    val logHistory: LogHistory
        get() = mHandler.logHistory

    fun setHistoryFilterLevel(level: Logger.Level) {
        mHandler.setHistoryFilterLevel(level)
    }

    override fun isEnabled(level: Logger.Level): Boolean {
        return mHandler.isEnabled(level)
    }

    override fun print(level: Logger.Level, message: String?, throwable: Throwable?) {
        mHandler.print(name, level, throwable, message)
    }

    override fun print(level: Logger.Level, throwable: Throwable?, messageFormat: String?, vararg args: Any) {
        mHandler.print(name, level, throwable, messageFormat, *args)
    }

    fun drawHistoryOnCanvas(canvas: Canvas, x: Int, y: Int, paint: Paint, lines: Int) {
        var y = y
        val minY = canvas.clipBounds.top
        val history = logHistory
        var N = history.filteredSize
        if (N > lines)
            N = lines
        for (i in 0..N - 1) {

            val id = history.getFilteredId(i)
            val time = history.getFilteredTime(i)
            val level = history.getFilteredLevel(i)
            var message = history.getFilteredMessage(i)
            message = String.format(Locale.US, "%03d %c: %tT: %s", id, getLevelChar(level), time, message)

            paint.color = getLevelColor(level)

            canvas.drawText(message, x.toFloat(), y.toFloat(), paint)

            y -= (paint.textSize + 2).toInt()
            if (y < minY)
                break
            paint.alpha = paint.alpha * 7 / 8
        }

    }

    fun log(level: MyLogLevel, message: String, throwable: Throwable?) {
        var lvl: Logger.Level = Logger.Level.INFO
        when (level.number) {
            2 -> lvl = Logger.Level.VERBOSE
            3 -> lvl = Logger.Level.DEBUG
            4 -> lvl = Logger.Level.INFO
            5 -> lvl = Logger.Level.WARN
            6 -> lvl = Logger.Level.ERROR
            7 -> lvl = Logger.Level.ASSERT
        }
        print(lvl, message, throwable)
    }

    companion object {

        /**
         * Default logger for use in UI thread
         */
        @JvmField val UIL = MyOldAndroidLogger("ML")
        val COLOR_VERBOSE = 0xFFB0B0B0.toInt()
        val COLOR_DEBUG = 0xFF606060.toInt()
        val COLOR_INFO = 0xFF000000.toInt()
        val COLOR_WARNING = 0xFF0000A0.toInt()
        val COLOR_ERROR = 0xFFA00000.toInt()
        val COLOR_ASSERT = 0xFFE00000.toInt()
        private val LEVEL_CHARS = charArrayOf('X', 'X', 'V', 'D', 'I', 'W', 'E', 'A')

        fun getLevelChar(level: Logger.Level): Char {
            return LEVEL_CHARS[level.intValue()]
        }

        fun getLevelColor(level: Logger.Level): Int {
            when (level) {
                Logger.Level.ASSERT -> return COLOR_ASSERT
                Logger.Level.ERROR -> return COLOR_ERROR
                Logger.Level.WARN -> return COLOR_WARNING
                Logger.Level.INFO -> return COLOR_INFO
                Logger.Level.DEBUG -> return COLOR_DEBUG
                Logger.Level.VERBOSE -> return COLOR_VERBOSE
                else -> throw IllegalArgumentException()
            }
        }
    }

    private fun ll(level: MyLogLevel): Logger.Level = when(level) {
        MyLogLevel.VERBOSE -> Logger.Level.VERBOSE
        MyLogLevel.DEBUG -> Logger.Level.DEBUG
        MyLogLevel.INFO -> Logger.Level.INFO
        MyLogLevel.WARN -> Logger.Level.WARN
        MyLogLevel.ERROR -> Logger.Level.ERROR
        MyLogLevel.ASSERT -> Logger.Level.ASSERT
    }
    override operator fun invoke(entry: MyLogEntry) {
        print(ll(entry.level), entry.throwable, entry.message)
    }
    override fun e(message: String, throwable: Throwable?) { log(message, MyLogLevel.ERROR , throwable = throwable) }
    override fun a(message: String, throwable: Throwable?) { log(message, MyLogLevel.ASSERT, throwable = throwable) }
}
