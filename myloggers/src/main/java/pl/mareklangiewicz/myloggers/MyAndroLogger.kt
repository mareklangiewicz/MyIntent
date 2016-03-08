package pl.mareklangiewicz.myloggers

import com.noveogroup.android.log.Logger
import com.noveogroup.android.log.PatternHandler
import pl.mareklangiewicz.myutils.IMyLogger
import pl.mareklangiewicz.myutils.MyLogEntry
import pl.mareklangiewicz.myutils.MyLogLevel

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

