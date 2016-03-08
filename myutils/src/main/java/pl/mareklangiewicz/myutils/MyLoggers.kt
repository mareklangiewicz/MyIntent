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


data class MyLogEntry(
        val message: String,
        val level: MyLogLevel = MyLogLevel.INFO,
        val tag: String = "",
        val throwable: Throwable? = null,
        val id: Long = counter.getAndIncrement(),
        val time: Long = System.currentTimeMillis()) {
    companion object {
        val counter = AtomicLong(0)
    }

    override fun toString(): String = "%03d %c: %tT:%s %s%s".format(id, level.symbol, time, tag, message, if(throwable === null) "" else " $throwable")
}



// alias IMyLogger = IPushee<MyLogEntry> // SOMEDAY: do it when Kotlin have type aliases
// for now our IMyLogger IS just: Function1<MyLogEntry, Unit> without alias name...

@Deprecated( "Remove this temporary class!.", ReplaceWith("Function1<MyLogEntry, Unit>"))
interface IMyLogger : Function1<MyLogEntry, Unit> { // TODO NOW: remove this class and use Function1<MyLogEntry, Unit>
    fun log(
            message: String,
            level: MyLogLevel = MyLogLevel.INFO,
            tag: String = "",
            throwable: Throwable? = null
    ) = this(MyLogEntry(message, level, tag, throwable))

    fun v(message: String) { log(message, MyLogLevel.VERBOSE) }
    fun d(message: String) { log(message, MyLogLevel.DEBUG  ) }
    fun i(message: String) { log(message, MyLogLevel.INFO   ) }
    fun w(message: String) { log(message, MyLogLevel.WARN   ) }
    fun e(message: String) { log(message, MyLogLevel.ERROR  ) }
    fun a(message: String) { log(message, MyLogLevel.ASSERT ) }
    fun e(message: String, throwable: Throwable?) { log(message, MyLogLevel.ERROR , throwable = throwable) }
    fun a(message: String, throwable: Throwable?) { log(message, MyLogLevel.ASSERT, throwable = throwable) }
}


fun Function1<MyLogEntry, Unit>.log(
        message: String,
        level: MyLogLevel = MyLogLevel.INFO,
        tag: String = "",
        throwable: Throwable? = null
) = this(MyLogEntry(message, level, tag, throwable))

fun Function1<MyLogEntry, Unit>.v(message: String, tag: String = "", throwable: Throwable? = null) { log(message, MyLogLevel.VERBOSE, tag, throwable) }
fun Function1<MyLogEntry, Unit>.d(message: String, tag: String = "", throwable: Throwable? = null) { log(message, MyLogLevel.DEBUG  , tag, throwable) }
fun Function1<MyLogEntry, Unit>.i(message: String, tag: String = "", throwable: Throwable? = null) { log(message, MyLogLevel.INFO   , tag, throwable) }
fun Function1<MyLogEntry, Unit>.w(message: String, tag: String = "", throwable: Throwable? = null) { log(message, MyLogLevel.WARN   , tag, throwable) }
fun Function1<MyLogEntry, Unit>.e(message: String, tag: String = "", throwable: Throwable? = null) { log(message, MyLogLevel.ERROR  , tag, throwable) }
fun Function1<MyLogEntry, Unit>.a(message: String, tag: String = "", throwable: Throwable? = null) { log(message, MyLogLevel.ASSERT , tag, throwable) }

@Suppress("UNUSED_PARAMETER", "unused")
fun Function1<MyLogEntry, Unit>.q(message: String, tag: String = "", throwable: Throwable? = null) { }



@Deprecated( "Not really needed now. Just use empty function.", ReplaceWith(" { } "))
class MyEmptyLogger : IMyLogger {
    override fun invoke(le: MyLogEntry) { }
}


@Deprecated( "Not really needed now. Just use println function.", ReplaceWith(" { println(it) } "))
class MySystemLogger : IMyLogger {
    override fun invoke(le: MyLogEntry) {
        val stream = if(le.level > MyLogLevel.ERROR) System.err else System.out
        stream.println(le.message)
    }
}

@Deprecated( "Not really needed now. Just use MyRingBuffer<MyLogEntry>.", ReplaceWith("MyRingBuffer<MyLogEntry>"))
class MyHistoryLogger(buffer: IMyBuffer<MyLogEntry> = MyRingBuffer<MyLogEntry>()) : IMyBuffer<MyLogEntry> by buffer, IMyLogger
