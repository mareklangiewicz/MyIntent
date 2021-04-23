package pl.mareklangiewicz.myutils

import pl.mareklangiewicz.upue.IPush
import pl.mareklangiewicz.upue.Pushee
import pl.mareklangiewicz.upue.IScheduler
import pl.mareklangiewicz.upue.amap
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


// alias IMyLogger = Pushee<MyLogEntry> // SOMEDAY: do it when Kotlin have type aliases
// for now our IMyLogger IS just: Pushee<MyLogEntry> without alias name...

// those functions below should be inlined for performance, but then I loose correct StackTraceElement.lineNumber,
// and I need those line numbers be able to find where in source the logging message was invoked
// (by just clicking on the message in android studio in android monitor window)
// TODO SOMEDAY: use inline versions in release mode, and leave as it is in debug mode.
// (unless they will fix the line numbers returned from StackTraceElement)

fun Pushee<MyLogEntry>.log(
        message: Any?,
        level: MyLogLevel = MyLogLevel.INFO,
        tag: String = "",
        throwable: Throwable? = null
) = this(MyLogEntry(message.toString(), level, tag, throwable))

fun Pushee<MyLogEntry>.v(message: Any?, tag: String = "ML", throwable: Throwable? = null) = log(message, MyLogLevel.VERBOSE, tag, throwable)
fun Pushee<MyLogEntry>.d(message: Any?, tag: String = "ML", throwable: Throwable? = null) = log(message, MyLogLevel.DEBUG  , tag, throwable)
fun Pushee<MyLogEntry>.i(message: Any?, tag: String = "ML", throwable: Throwable? = null) = log(message, MyLogLevel.INFO   , tag, throwable)
fun Pushee<MyLogEntry>.w(message: Any?, tag: String = "ML", throwable: Throwable? = null) = log(message, MyLogLevel.WARN   , tag, throwable)
fun Pushee<MyLogEntry>.e(message: Any?, tag: String = "ML", throwable: Throwable? = null) = log(message, MyLogLevel.ERROR  , tag, throwable)
fun Pushee<MyLogEntry>.a(message: Any?, tag: String = "ML", throwable: Throwable? = null) = log(message, MyLogLevel.ASSERT , tag, throwable)

@Suppress("UNUSED_PARAMETER", "unused")
fun Pushee<MyLogEntry>.q(message: Any?, tag: String = "", throwable: Throwable? = null) { }



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
fun Pushee<MyLogEntry>.trace(depth: Int): Pushee<MyLogEntry> = amap {
    val st = findStackTraceElement(depth)
    if(st === null) it else it.copy(message = "(${st.fileName}:${st.lineNumber}) ${it.message}")
}


/**
 * Logs given entries on standard system out stream (or err).
 * Ignores the log entry if level < outlvl
 * Redirects entry to err stream if level > errlvl.
 * WARNING: system err can be flushed at strange moments,
 * so usually it is better to use only out stream to avoid message reordering.
 */
class MySystemLogger(val outlvl: MyLogLevel = MyLogLevel.VERBOSE, val errlvl: MyLogLevel = MyLogLevel.ASSERT) : Pushee<MyLogEntry> {
    override fun invoke(le: MyLogEntry) {
        if(le.level < outlvl)
            return
        val stream = if(le.level > errlvl) System.err else System.out
        stream.println(le)
    }
}

// TODO NOW: do I even need MyLogHistory class now?
class MyLogHistory : IArr<MyLogEntry>, IPush<MyLogEntry>, IChanges<LstChg<MyLogEntry>> {

    private val buf = Lst<MyLogEntry>(256)
    private val buff = buf.withFilter()
    private val bufl = buff.withLimit(256)

    override fun get(idx: Int) = buff.out[len - 1 - idx]
    override fun set(idx: Int, item: MyLogEntry) { throw UnsupportedOperationException() }

    override val len: Int get() = buff.out.len

    override val changes = buff.out.changes

    override val push = bufl.tail.push

    var level = MyLogLevel.VERBOSE // minimum level of returned history
        set(value) {
            if(value != field) {
                field = value
                buff.filter = { it.level >= value }
            }
        }
}

/**
 * Wraps a scheduler and adds catching exceptions and logging them using provided logger
 */
fun IScheduler.logex(log: Pushee<MyLogEntry>): IScheduler = object : IScheduler by this@logex {
    override fun schedule(delay: Long, action: (Unit) -> Unit) = this@logex.schedule(delay) {
        try {
            action(Unit)
        } catch (e: Throwable) {
            log.e(e, throwable = e)
        }
    }
}

