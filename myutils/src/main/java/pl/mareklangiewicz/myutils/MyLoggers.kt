package pl.mareklangiewicz.myutils

import java.util.concurrent.atomic.AtomicLong

/**
 * Created by Marek Langiewicz on 16.02.16.
 */


/** number equals appropriate android logging level; colors look good on bright background; */
enum class MyLogLevel(val number: Int, val symbol: Char, val color: Int) {
    VERBOSE(2, 'V', 0xFFB0B0B0.toInt()),
    DEBUG  (3, 'D', 0xFF606060.toInt()),
    INFO   (4, 'I', 0xFF000000.toInt()),
    WARN   (5, 'W', 0xFF0000A0.toInt()),
    ERROR  (6, 'E', 0xFFA00000.toInt()),
    ASSERT (7, 'A', 0xFFE00000.toInt()),
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

// those functions below should be inlined for performance, but then I loose correct StackTraceElement.lineNumber,
// and I need those line numbers be able to find where in source the logging message was invoked
// (by just clicking on the message in android studio in android monitor window)
// TODO SOMEDAY: use inline versions in release mode, and leave as it is in debug mode.
// (unless they will fix the line numbers returned from StackTraceElement)

fun Function1<MyLogEntry, Unit>.log(
        message: Any?,
        level: MyLogLevel = MyLogLevel.INFO,
        tag: String = "",
        throwable: Throwable? = null
) = this(MyLogEntry(message.toString(), level, tag, throwable))

fun Function1<MyLogEntry, Unit>.v(message: Any?, tag: String = "ML", throwable: Throwable? = null) { log(message, MyLogLevel.VERBOSE, tag, throwable) }
fun Function1<MyLogEntry, Unit>.d(message: Any?, tag: String = "ML", throwable: Throwable? = null) { log(message, MyLogLevel.DEBUG  , tag, throwable) }
fun Function1<MyLogEntry, Unit>.i(message: Any?, tag: String = "ML", throwable: Throwable? = null) { log(message, MyLogLevel.INFO   , tag, throwable) }
fun Function1<MyLogEntry, Unit>.w(message: Any?, tag: String = "ML", throwable: Throwable? = null) { log(message, MyLogLevel.WARN   , tag, throwable) }
fun Function1<MyLogEntry, Unit>.e(message: Any?, tag: String = "ML", throwable: Throwable? = null) { log(message, MyLogLevel.ERROR  , tag, throwable) }
fun Function1<MyLogEntry, Unit>.a(message: Any?, tag: String = "ML", throwable: Throwable? = null) { log(message, MyLogLevel.ASSERT , tag, throwable) }

@Suppress("UNUSED_PARAMETER", "unused")
fun Function1<MyLogEntry, Unit>.q(message: Any?, tag: String = "", throwable: Throwable? = null) { }



fun findStackTraceElement(depth: Int): StackTraceElement? {
    val st = Thread.currentThread().stackTrace
    if(st === null || st.size <= depth)
        return null
    return st[depth]
}

/**
 * Add prefix to every log message with stack trace info.
 * The prefix format is prepared for android studio, so you can just click
 * on the log message and it will browse to exact place in source code.
 * You have to provide a depth level of stack frame which actually calls
 * the logger from user code. You can just try depths: 0, 1, 2, ...
 * until it starts to log correctly.
 * Warning: this can be slow - use it only in debug mode
 */
fun Function1<MyLogEntry, Unit>.trace(depth: Int): Function1<MyLogEntry, Unit>
        = this.amap {
    entry: MyLogEntry ->
    val st = findStackTraceElement(depth)
    if(st === null) entry else
    entry.copy(message = "(${st.fileName}:${st.lineNumber}) ${entry.message}")
}


/**
 * Logs given entries on standard system out stream (or err).
 * Ignores the log entry if level < outlvl
 * Redirects entry to err stream if level > errlvl.
 * WARNING: system err can be flushed at strange moments,
 * so usually it is better to use only out stream to avoid message reordering.
 */
class MySystemLogger(val outlvl: MyLogLevel = MyLogLevel.VERBOSE, val errlvl: MyLogLevel = MyLogLevel.ASSERT) : Function1<MyLogEntry, Unit> {
    override fun invoke(le: MyLogEntry) {
        if(le.level < outlvl)
            return
        val stream = if(le.level > errlvl) System.err else System.out
        stream.println(le)
    }
}

class MyLogHistory : Function1<MyLogEntry, Unit>, IMyArray<MyLogEntry>, IClear {

    private val fullBuffer = MyRingBuffer<MyLogEntry>()

    private val filteredBuffer = MyRingBuffer<MyLogEntry>()

    private val relay = Relay<Unit>()

    val changes: IPusher<Unit, (Unit) -> Unit> = relay

    var level = MyLogLevel.VERBOSE // minimum level of returned history
        set(value) {
            if(value != field) {
                field = value
                refilter()
                relay.pushee(Unit)
            }
        }

    override fun get(idx: Int) = filteredBuffer[size - 1 - idx]

    override val size: Int get() = filteredBuffer.size

    override fun invoke(e: MyLogEntry) {
        fullBuffer(e)
        if(e.level >= level) {
            filteredBuffer(e)
            relay.pushee(Unit)
        }
    }

    fun refilter() {
        filteredBuffer.clear()
        for(e in fullBuffer)
            if(e.level >= level)
                filteredBuffer(e)
    }

    override fun clear() {
        fullBuffer.clear()
        filteredBuffer.clear()
        relay.pushee(Unit)
    }
}

