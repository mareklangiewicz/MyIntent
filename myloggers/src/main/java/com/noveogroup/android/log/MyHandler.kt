/**
 * Created by Marek Langiewicz on 24.06.15.
 * A log handler with features I need. Like log history, tags, etc..
 */

package com.noveogroup.android.log

import android.support.annotation.UiThread
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.View
import snack

import java.util.Locale

/**
 * This class is not thread-safe. In particular you should use it only from UI thread
 * if you use features: setSnackView; setInvalidateView; getLogHistory;
 */
@UiThread
class MyHandler
/**
 * Creates new [MyHandler].

 * @param level          the level.
 * *
 * @param tagPattern     the tag pattern.
 * *
 * @param messagePattern the message pattern.
 */
(level: Logger.Level, tagPattern: String, messagePattern: String) : PatternHandler(level, tagPattern, messagePattern) {
    /**
     * WARNING: use MyHandler from one thread only (like UI thread) - if you want to use this feature.
     */
    val logHistory = LogHistory(HISTORY_LEN)

    /**
     * WARNING: remember to set it back to null if the view is not used anymore - to avoid memory leaks
     * WARNING: use MyHandler from UI thread only - if you want to use this feature.
     */
    var snackView: View? = null
    /**
     * WARNING: remember to set it back to null if the view is not used anymore - to avoid memory leaks
     * WARNING: use MyHandler from UI thread only - if you want to use this feature.
     */
    var invalidateView: View? = null
    private var mAdapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>? = null
    /**
     * WARNING: remember to set it back to null if the adapter is not used anymore - to avoid memory leaks
     * WARNING: use MyHandler from UI thread only - if you want to use this feature.
     */
    fun setAdapter(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) {
        mAdapter = adapter
    }

    fun setHistoryFilterLevel(level: Logger.Level) {
        logHistory.filterLevel = level
        if (mAdapter != null)
            mAdapter!!.notifyDataSetChanged()
        if (invalidateView != null)
            invalidateView!!.invalidate()
    }

    @Throws(IllegalArgumentException::class)
    override fun print(loggerName: String, level: Logger.Level, throwable: Throwable?, message: String?) {
        var msg = message ?: ""

        if (msg.startsWith(SNACK_TAG)) {
            msg = msg.substring(SNACK_TAG.length)
            var duration = Snackbar.LENGTH_LONG
            if (msg.startsWith(SHORT_TAG)) {
                msg = msg.substring(SHORT_TAG.length)
                duration = Snackbar.LENGTH_SHORT
            }
            snackView?.let { it.snack(msg, duration) {} }
        }

        logHistory.add(loggerName, level, msg)

        if (invalidateView != null)
            invalidateView!!.invalidate()
        if (mAdapter != null)
            mAdapter!!.notifyDataSetChanged()

        super.print(loggerName, level, throwable, msg)

    }

    @Throws(IllegalArgumentException::class)
    override fun print(loggerName: String, level: Logger.Level, throwable: Throwable?, messageFormat: String?,
                       vararg args: Any) {

        if (args.size == 0 || messageFormat == null)
            print(loggerName, level, throwable, messageFormat)
        else
            print(loggerName, level, throwable, String.format(Locale.US, messageFormat, *args))

    }

    companion object {

        const val SNACK_TAG = "[SNACK]"
        const val SHORT_TAG = "[SHORT]"
        const private val HISTORY_LEN = 160
    }
}

