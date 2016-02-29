package pl.mareklangiewicz.myutils

import java.util.concurrent.atomic.AtomicLong

/**
 * Created by Marek Langiewicz on 16.02.16.
 */

/** number equals appropriate android logging level; colors look good on bright background; */
enum class MyLogLevel(val number: Int, val symbol: Char, val color: Long) {
    VERBOSE(2, 'V', 0xFFB0B0B0),
    DEBUG  (3, 'D', 0xFF606060),
    INFO   (4, 'I', 0xFF000000),
    WARN   (5, 'W', 0xFF0000A0),
    ERROR  (6, 'E', 0xFFA00000),
    ASSERT (7, 'A', 0xFFE00000),
}

interface IMyLogger {

    fun log(level: MyLogLevel, message: String, throwable: Throwable? = null)

    fun v(message: String, throwable: Throwable? = null) { log(MyLogLevel.VERBOSE, message, throwable) }
    fun d(message: String, throwable: Throwable? = null) { log(MyLogLevel.DEBUG  , message, throwable) }
    fun i(message: String, throwable: Throwable? = null) { log(MyLogLevel.INFO   , message, throwable) }
    fun w(message: String, throwable: Throwable? = null) { log(MyLogLevel.WARN   , message, throwable) }
    fun e(message: String, throwable: Throwable? = null) { log(MyLogLevel.ERROR  , message, throwable) }
    fun a(message: String, throwable: Throwable? = null) { log(MyLogLevel.ASSERT , message, throwable) }

    fun q(message: String, throwable: Throwable? = null) { }

}



class MyEmptyLogger : IMyLogger {
    override fun log(level: MyLogLevel, message: String, throwable: Throwable?) { }
}


class MySystemLogger : IMyLogger {
    override fun log(level: MyLogLevel, message: String, throwable: Throwable?) {
        val stream = (level > MyLogLevel.ERROR) % System.err ?: System.out
        stream.println(message)
    }
}

class MyMultiLogger(vararg val loggers: IMyLogger) : IMyLogger {
    override fun log(level: MyLogLevel, message: String, throwable: Throwable?) {
        loggers.forEach { it.log(level, message, throwable) }
    }
}

data class MyLogEntry(
        val id: Long = counter.getAndIncrement(),
        val time: Long = System.currentTimeMillis(),
        val tag: String = "",
        val level: MyLogLevel,
        val message: String,
        val throwable: Throwable? = null ) {
    companion object {
        val counter = AtomicLong(0)
    }
}

fun IMyConsumer<MyLogEntry>.toLogger(tag: String = "") = object : IMyLogger {
    override fun log(level: MyLogLevel, message: String, throwable: Throwable?) {
        accept(MyLogEntry(tag = tag, level = level, message = message, throwable = throwable))
    }
}




