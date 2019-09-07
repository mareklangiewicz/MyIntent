package pl.mareklangiewicz.myutils

import android.os.Handler
import android.os.Looper
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Created by Marek Langiewicz on 20.02.16.
 */


/**
 * Pue is a kind of minimalist functional active/passive library
 *
 * By "active" I mean object that decides itself when to do something (called: Puer - like Pusher or Puller)
 * By "passive" I mean object that has public method others can call and it just reacts
 * (just a function, usually called: Puee - like Pushee or Pullee)
 *
 * As as base interface for almost all in here we use kotlin.jvm.functions.Function1<T, R>
 * so we can use ordinary functions, but often we use it as "function objects" that can contain
 * some internal state and can behave differently every time the are called/invoked.
 *
 * We can compose programs in two complementary styles:
 *
 * 1. Pull-based: with base interfaces: IPullee<T> and IPuller<T> (IPullee<T> is actually just: Function1<Unit, T>)
 *    A minimalist collection of some composable functions and classes where the IPuller is an active side
 *    that decides when to call a IPullee for next item. It is similar to Java 8 Stream (IPullee)
 *    and Collector (IPuller), but simpler.
 *
 * 2. Push-based: with base interfaces: IPushee<T> and IPusher<T> (IPushee<T> is actually just: Function1<T, Unit>)
 *    A minimalist collection of some composable functions and classes where the IPusher is an active side
 *    that decides when to send next item to the IPushee. It is similar to RxJava Observer (IPushee)
 *    and Observable (IPusher), but simpler.
 *
 * We can combine these two styles (pull-based and push-based) easily.
 *
 *
 * Two active interfaces: IPusher and IPuller are in fact just special examples of IPuer interface.
 * IPuller<T> = IPuer<Unit, T>; IPusher<T> = IPuer<T, Unit>
 * And the IPuer<T, R> is just an impure function that calls provided Function1<T, R> (alias: Puee) when it wants to.
 * The puer (as an active side of composition) decides when and how many times to call attached function (puee).
 *
 * Note: we will sometimes add more restrictions about how our IPullee, IPushee, IPuller, IPusher
 * objects should behave. Such restrictions will extend our base rules (base protocol).
 * First restriction/extension will be:
 * If we push or pull nullable types - null item means the end of the stream.
 *
 *
 * TODO LATER: IMPORTANT: make most functions inline..
 * DETAILS:
 * I had some strange problems with inlining functions so I removed all 'inline', 'reified', 'crossinline', etc. modifiers for now.
 * But it is important to put it back when this library is more mature because inlining can provide a lot of performance
 * especially in this kind of code.
 * Example of compilation error: java.lang.UnsupportedOperationException: Don't render receiver parameters
 * (looks like I should mark receiver function object of my function as crossinline and this is not possible in Kotlin now...)
 */


typealias IPuee<T, R> = (T) -> R

/** passive producer (similar to iterator) */
typealias IPullee<R> = IPuee<Unit, R>

/** passive consumer (similar to rx: observer) */
typealias IPushee<T> = IPuee<T, Unit>


/**
 * IPuer interface is the core of this library. It is an active side of communication.
 * It takes a function and calls it whenever it wants to :-)
 * It returns other function that can be used to control the behavior of the puer regarding given function.
 * Usage: You give the puer your function (puee), and from now on, you do not call your function manually,
 * but puer does it for you. And puer just gives you some other function: the controller,
 * that can give you some control on how and when the puer call your original function.
 * What kind of controller you get depends on particular puer implementation.
 * The simplest controller could be just: Function1<Unit, Unit> that just detaches
 * the original function from the puer entirely so it is never called again (like rx: unsubscribe).
 * But we will usually implement puers that return more sophisticated controllers like:
 * Function1<ICommand, Unit> where the Command can be: Start, Stop, Pause, Cancel, etc...
 *
 */
typealias IPuer<T, R, Cmd> = (IPuee<T, R>) -> IPushee<Cmd>

/* Do not read next line if you don't want to be confused:
 * A puer is actually also type of puee: typealias IPuer<T, R, Cmd> = IPuee<IPuee<T, R>,IPushee<Cmd>>
 * (more about this kind of duality: http://csl.stanford.edu/~christos/pldi2010.fit/meijer.duality.pdf)
 */

/** active consumer (similar to java 8: collector) */
typealias IPuller<R, Cmd> = IPuer<Unit, R, Cmd>

/** active producer (similar to rx.observable) */
typealias IPusher<T, Cmd> = IPuer<T, Unit, Cmd>

// IMPORTANT:
// First (and most general) extension to our basic protocol defined by interfaces above is: null value means: end of the stream.
// So for example if you want to have FINITE streams - use nullable value type. See "asNPullee" below for simple example.
// We will call pushers of nullable streams: npushers; pullers: npullers; etc. (npushee; npushee)


// NPullee means Pullee that returns null when the stream has ended.
fun <R> Iterator<R>.asNPullee() = { _: Unit -> if (hasNext()) next() else null }

fun <R> Iterable<R>.asNPullee() = iterator().asNPullee()
fun <R> Sequence<R>.asNPullee() = iterator().asNPullee()

fun <R> nPulleeOf(vararg ts: R) = ts.iterator().asNPullee()

// Examples how to create simple NPullee:
// (1..50).asNPullee()
// listOf("bla", "ble").asNPullee()
// nPulleeOf('a', 'b', 'c')


/**
 * This allows to use "for in" loop to traverse through NPullee.
 * TODO: tests and examples
 */
operator fun <R> IPullee<R?>.iterator() = object : Iterator<R> {

    var end = false
    var current: R? = null

    override fun hasNext(): Boolean {
        if (end) return false
        if (current !== null) return true
        current = invoke(Unit)
        if (current === null) {
            end = true
            return false
        }
        return true
    }

    override fun next(): R {
        if (end) throw IllegalStateException("No more items available.")
        val r = current
        if (r !== null) {
            current = null
            return r
        }
        val rr = invoke(Unit)
        if (rr === null) {
            end = true
            throw IllegalStateException("No more items available.")
        }
        return rr
    }
}

fun <R> IPullee<R>.cut(end: R) = object : IPullee<R> {
    var up: IPullee<R>? = this@cut
    override fun invoke(u: Unit): R {
        up?.run {
            val r = invoke(Unit)
            if (r != end) return r
            up = null //  TODO: will it garbage collect this@cut ?
            return end
        } ?: return end
    }
}

fun <R> IPullee<R?>.ncut() = cut(null)

/**
 * TODO: version for Puller (using lift?)
 * TODO: versions for Pushee and Pusher (care about forgetting not needed references
 *  because we do not support unsubscribe in base protocol, but in most cases garbage collection
 *  should do the work)
 */


// TODO: operator (in all versions?) that adds (pushes actually) controller to special
//  controllers consumer (pushee<(cmd) -> Unit>) that can be used as ComposableDisposable in rx
//  to unsubscribe from many streams at once (or invoke any other Cmd)


/**
 * We can be more like RxJava by using some wrapper for items.
 * We also can easily add more types of events when needed.
 * And different puers can support different subset of events..
 */
interface IEvent<out I>

data class Item<out I>(val item: I) : IEvent<I> // sends next item (similar to rx: onNext)
data class Error<out I>(val error: Throwable) : IEvent<I> // sends some error; (similar to rx: onError, but it does NOT end the stream)

@Deprecated("Use null value instead")
class Completed<out I> : IEvent<I> // Do not use it: in this library just use null to notify the end of the stream.

// Example of how to extend type of events we can use:
data class Warning<out I>(val warning: Throwable) : IEvent<I> // sends some warning; does NOT end the stream


/**
 * We could also just use sealed Event class, to forbid extending it and make sure our operators cover all cases.
 * I like the open solution above more but... a sealed example similar to RxJava would look like this:
 */
//sealed class RxStyleEvent<T> {
//    data class Next<T>(val next: T)
//    data class Error(val error: Throwable)
//    object Completed
//}


// About IEvents:
// Error event should NOT end communication automatically - always send a null value after error to end the stream.
// Completed event should be removed (a null value without preceding error will be used instead)
// This solution have flaws, but it is much better because we can use all code that just understand
// nullable streams as it is. This way a lot of code doesn't have to know/care about IEvent type structure.
// It will just work for any (nullable) type.
// I guess the main disadvantage of this solution is network related - what if we loose connection after
// sending Error item. We will not have a chance to send terminating "null" value at all.
// But: in such cases we can write simple wrappers/converters that box error and null values in one special message
// like: class Fatal<out I>(val error: Throwable) : IEvent<I>
// and unbox it on the other side (back to two separate items).


// A IPuer can return some IPushee<ICommand> you can use to control the way it works.
interface ICommand

object Cancel : ICommand // can be used similarly to rx: Subscription.unsubscribe
// Every epuer should support Cancel command. Other commands are optional.
object Start : ICommand // can be used similarly to rx: ConnectableObservable.connect

object Stop : ICommand
object Pause : ICommand
object Step : ICommand
object Tick : ICommand
object Tock : ICommand

data class Request(val count: Long) : ICommand // for future backpressure implementation (for particular IPuers only)
// Puer should keep track how many items are requested so far and how many has been emited already - and NOT emit more than requested.

// user can add more special case commands for his special puers. For example:
data class Inject<out T>(val item: T) : ICommand // Commanding puer to push additional item provided "by hand" immediately.


// Our base interfaces using IEvent and ICommand (with prefix E):

typealias IEPullee<R> = IPullee<IEvent<R>?>


typealias IEPushee<T> = IPushee<IEvent<T>?>


typealias IEPuller<R> = IPuller<IEvent<R>?, ICommand>


typealias IEPusher<T> = IPusher<IEvent<T>?, ICommand>


fun <R> Iterator<R>.asEPullee(): IEPullee<R> = { if (hasNext()) Item(next()) else null }

// We could also just use vnmap operator like this:
//fun <R> Iterator<R>.asEPullee(): IEPullee<R> = asNPullee().vnmap { Item(it) }
// but it could be slower (unless inlined property)


fun <R> Iterable<R>.asEPullee() = iterator().asEPullee()

fun <R> Sequence<R>.asEPullee() = iterator().asEPullee()

fun <R> ePulleeOf(vararg ts: R) = ts.iterator().asEPullee()

// Examples how to create simple EPullee:
// (1..50).asEPullee()
// listOf("bla", "ble").asEPullee()
// ePulleeOf('a', 'b', 'c')


// It will extract items; pass through nulls; throw on errors and any other IEvents
fun <I> IEPullee<I>.vitems() = vnmap {
    when (it) {
        is Item -> it.item
        else -> throw IllegalStateException("Unsupported event: $it")
    }
}

// Dual to vitems but for pushees (reminder: pushee is a kind of passive consumer)
// implementation is simple but it requres some "backward" thinking to really understand it :)
fun <I> IPushee<I?>.aitems() = anmap<I, Unit, IEvent<I>?> {
    when (it) {
        is Item -> it.item
        else -> throw IllegalStateException("Unsupported event: $it")
    }
}



typealias Millis = Long // TODO SOMEDAY: use newtype when kotlin has it

typealias CurrentTime = IPullee<Millis> // TODO SOMEDAY: use newtype when kotlin has it

interface IScheduler {
    /**
     * delay is in milliseconds; non-positive value means: no delay.
     * Returns the "cancel" function. Which cancels scheduled action when invoked.
     * (If some implementation does not support delays - it can throw UnsupportedOperationException if delay is > 0)
     * No delay does not have to mean: immediately - actual execution time is implementation specific.
     */
    fun schedule(delay: Long = 0, action: (Unit) -> Unit): IPushee<Cancel>

    val now: CurrentTime
}
// Some schedulers may ensure happens-before relationship between scheduled actions - it should be documented in particular scheduler.


// TODO LATER: test it (this is just first fast attempt to implement something simple - it will probably be rewritten)
class ExecutorScheduler(private val ses: ScheduledExecutorService) : IScheduler {

    constructor(threads: Int = Runtime.getRuntime().availableProcessors() * 2) : this(Executors.newScheduledThreadPool(threads))

    override fun schedule(delay: Long, action: (Unit) -> Unit): (Cancel) -> Unit {
        val sf = ses.schedule({ action(Unit) }, delay, TimeUnit.MILLISECONDS)
        return { sf.cancel(true) }
    }

    override val now = { _: Unit -> System.currentTimeMillis() }
}


class HandlerScheduler(private val handler: Handler = Handler(Looper.getMainLooper())) : IScheduler {

    override fun schedule(delay: Long, action: (Unit) -> Unit): (Cancel) -> Unit {
        val runnable = Runnable { action(Unit) }
        if (delay > 0)
            handler.postDelayed(runnable, delay)
        else
            handler.post(runnable)
        return { handler.removeCallbacks(runnable) }
    }

    override val now = { _: Unit -> System.currentTimeMillis() }
}

/**
 * Wraps a scheduler adding additional delay to every schedule and current time.
 */
class FutureScheduler(val scheduler: IScheduler, val future: Millis) : IScheduler {

    override fun schedule(delay: Long, action: (Unit) -> Unit) = scheduler.schedule(delay + future, action)

    override val now = { u: Unit -> scheduler.now(u) + future }
}
// TODO LATER: test it


/**
 * Wraps a scheduler and adds catching exceptions and logging them using provided logger
 */
fun IScheduler.logex(log: IPushee<MyLogEntry>): IScheduler = object : IScheduler by this@logex {
    override fun schedule(delay: Long, action: (Unit) -> Unit) = this@logex.schedule(delay) {
        try {
            action(Unit)
        } catch (e: Throwable) {
            log.e(e, throwable = e)
        }
    }
}

/**
 * Pushes a Long number every time the Step command is invoked but only when pusher is started.
 * Steps are ignored when pusher is stopped. By default it pushes 0 value once for every step,
 * but you can provide explicit 'step' constructor parameter to change it to more.
 * It pushes a Long value that counts invocations in every step (starting from 0 for every step)
 * UPDATE: It supports Tick and Tock commands too (just like Step)
 * UPDATE: It supports Inject command too.
 */
open class StepPusher(val step: Long = 1, val tick: Long = 1, val tock: Long = 1) : IPusher<Long, ICommand> {

    protected class Pusher(
        private var function: IPushee<Long>?,
        val step: Long = 1,
        val tick: Long = 1,
        val tock: Long = 1
    ) : IPushee<ICommand> {

        var started = false

        override fun invoke(cmd: ICommand) {
            when (cmd) {
                Cancel -> {
                    started = false
                    function = null
                }
                Stop, Pause -> started = false
                Start -> started = true
                Step -> for (i in 0..step - 1) if (started) function?.invoke(i) else break
                Tick -> for (i in 0..tick - 1) if (started) function?.invoke(i) else break
                Tock -> for (i in 0..tock - 1) if (started) function?.invoke(i) else break
                is Inject<*> -> if (started) function?.invoke(cmd.item as Long)
                else -> throw IllegalArgumentException("Unsupported command.")
            }
        }
    }

    override fun invoke(f: IPushee<Long>): IPushee<ICommand> {
        return Pusher(f, step, tick, tock)
    }
}


/**
 * Takes an NPullee of subsequent intervals in ms. Interval <= 0 means immediately.
 * It supports commands:
 * - Start;
 * - Stop (the same as Pause - stops but can be started again);
 * - Cancel (unsubscribes - forgets given pushee);
 * - Step (the same as Tick and Tock - just invokes function once)
 * - Inject (mostly for debugging purposes)
 * Timer pushes a Long numbers counting from 0...
 * If intervals have ended (returned null): Timer will push null value once and finish (detach from its pushee).
 * If we subscribe more than once: all subscribers will share the same scheduler and all will pull intervals from the same pullee!
 */
class Timer(private val scheduler: IScheduler, private val intervals: IPullee<Long?>) : IPusher<Long?, ICommand> {

    override fun invoke(pushee: IPushee<Long?>): IPushee<ICommand> = SingleTimer(pushee)

    private inner class SingleTimer(private var pushee: IPushee<Long?>?) : IPushee<ICommand> {

        private var counter = 0L

        private var unschedule: IPushee<Cancel>? = null

        private val action = { _: Unit ->
            pushee?.invoke(counter++)
            invoke(Start)
        }

        override tailrec fun invoke(cmd: ICommand) {

            when (cmd) {

                Step, Tick, Tock -> pushee?.invoke(counter++)

                Stop, Pause -> {
                    unschedule?.invoke(Cancel)
                    unschedule = null
                }

                Cancel -> {
                    unschedule?.invoke(Cancel)
                    unschedule = null
                    pushee = null
                }

                Start -> {

                    if (pushee === null) return

                    val interval = intervals(Unit)

                    if (interval === null) {
                        pushee?.invoke(null)
                        invoke(Cancel)
                        return
                    }

                    if (interval <= 0L) {
                        pushee?.invoke(counter++)
                        invoke(Start)
                        return
                    }

                    unschedule = scheduler.schedule(interval, action)
                }
                is Inject<*> -> pushee?.invoke(cmd.item as Long?)
            }
        }
    }
}


/** maps argument before giving it to receiver function - actually just a function composition */
fun <A, V, B> IPuee<A, V>.amap(f: (B) -> A): IPuee<B, V> = f * this

/** maps result value from function (similar to Sequence.map) - actually just a function composition */
fun <A, V, W> IPuee<A, V>.vmap(f: (V) -> W): IPuee<A, W> = this * f


/** forwards null values down the stream */
fun <A, V> n(f: (A) -> V): (A?) -> V? = { if (it === null) null else f(it) }

/** maps items and forwards other events (and nulls) */
fun <A, V> e(f: (A) -> V): (IEvent<A>?) -> IEvent<V>? = {
    when (it) {
        is Item -> Item(f(it.item))
        is Warning -> Warning(it.warning)
        is Error -> Error(it.error)
        null -> null
        else -> throw UnsupportedOperationException("Unknown IEvent.")
    }
}
// TODO SOMEDAY: maybe something that captures thrown exceptions in Error IEvent?


fun <A, V, B> IPuee<A?, V>.anmap(f: (B) -> A): IPuee<B?, V> = n(f) * this
fun <A, V, W> IPuee<A, V?>.vnmap(f: (V) -> W): IPuee<A, W?> = this * n(f)

fun <A, B> IEPushee<A>.aemap(f: (B) -> A): IEPushee<B> = (e(f) * this)
fun <V, W> IEPullee<V>.vemap(f: (V) -> W): IEPullee<W> = (this * e(f))


/** allows to implement some side effect every time function is called with some argument */
fun <A, V> IPuee<A, V>.apeek(spy: IPushee<A>): IPuee<A, V> = { spy(it); this(it) }

/** allows to implement some side effect every time function returns some value (similar to rx: doOnNext but pull based) */
fun <A, V> IPuee<A, V>.vpeek(spy: IPushee<V>): IPuee<A, V> = this % spy


// peeks that ignore null values:
fun <A, V> IPuee<A?, V>.anpeek(spy: IPushee<A>): IPuee<A?, V> = { if (it !== null) spy(it); this(it) }

fun <A, V> IPuee<A, V?>.vnpeek(spy: IPushee<V>): IPuee<A, V?> = this % { if (it !== null) spy(it) }


/** forwards given items to IPushee, but only if they meet given predicate */
fun <A> IPushee<A>.afilter(pred: (A) -> Boolean): IPushee<A> = { if (pred(it)) this(it) }

/** Something like Sequence.filter - warning: it will block until next matching item is found.
 * Usually we will use it with nullable streams and return true for null value (so it does not block forever).
 */
fun <V> IPullee<V>.vfilter(pred: (V) -> Boolean): IPullee<V> = object : IPullee<V> {
    override fun invoke(u: Unit): V {
        var b = this@vfilter(Unit)
        while (!pred(b))
            b = this@vfilter(Unit)
        return b
    }
}

@Deprecated("Just use vfilter with predicate that returns true for null", ReplaceWith("vfilter"))
fun <V> IPullee<V?>.vnfilter(pred: (V) -> Boolean) = vfilter { it === null || pred(it) }

/** pushes forward only specified number of items */
fun <A> IPushee<A>.atake(n: Long): IPushee<A> = object : IPushee<A> {
    var count = n // it is public so user can potentially alter it anytime and make it push some more items.
    override fun invoke(a: A) = if (count-- > 0L) this@atake(a) else Unit
}

/** pushes forward only specified number of items, then one null value, and then nothing else */
fun <A> IPushee<A?>.antake(n: Long): IPushee<A?> = object : IPushee<A?> {
    var count = n // it is public so user can potentially alter it anytime and make it push some more items.
    override fun invoke(a: A?) { // if a is null it is ok too (because our protocol says it can happen only once)
        if (count > 0L)
            this@antake(a)
        else if (count == 0L)
            this@antake(null)
        count--
    }
}

/** pulls only specified number of items, then returns null values (similar to Kotlin: Sequence.take) */
fun <V> IPullee<V?>.vntake(n: Long): IPullee<V?> = object : IPullee<V?> {
    var count = n // it is public so user can potentially alter it anytime and make it pull some more items.
    override fun invoke(u: Unit): V? = if (count-- > 0) this@vntake(Unit) else null
}


/** start pushing forward after specified number of (dropped) items */
fun <A> IPushee<A>.adrop(n: Long): IPushee<A> = object : IPushee<A> {
    var count = n // it is public so user can potentially alter it anytime and make it drop some more items.
    override fun invoke(a: A) = if (count-- <= 0) this@adrop(a) else Unit
}

/** drops first n items immediately when first item is pulled, then forwards items as they are */
fun <V> IPullee<V>.vdrop(n: Long): IPullee<V> = object : IPullee<V> {
    var count = n // it is public so user can potentially alter it anytime and make it drop some more items.
    override fun invoke(u: Unit): V {
        while (count-- > 0) this@vdrop(Unit)
        return this@vdrop(Unit)
    }
}


/** forwards items as long as specified predicate holds */
fun <A> Function1<A, Unit>.atakeWhile(pred: Function1<A, Boolean>): Function1<A, Unit> = object : Function1<A, Unit> {
    var end = false
    override fun invoke(a: A) {
        if (end)
            return
        if (pred(a))
            return this@atakeWhile(a)
        end = true
    }
}

/** forwards items as long as specified predicate holds, then forwards null item, then nothing more */
fun <A> IPushee<A?>.antakeWhile(pred: (A) -> Boolean): IPushee<A?> = object : IPushee<A?> {
    var end = false
    override fun invoke(a: A?) {
        if (end)
            return
        if (a !== null && pred(a))
            return this@antakeWhile(a)
        end = true
        this@antakeWhile(null)
    }
}

/** forwards items as long as specified predicate holds, then forwards null items */
fun <V> IPullee<V?>.vntakeWhile(pred: (V) -> Boolean): IPullee<V?> = object : IPullee<V?> {
    var end = false
    override fun invoke(u: Unit): V? {
        if (end)
            return null
        val v = this@vntakeWhile(Unit)
        if (v !== null && pred(v))
            return v
        end = true
        return null
    }
}


/** drops items as long as specified predicate holds, then forwards all next items as they are */
fun <A> IPushee<A>.adropWhile(pred: (A) -> Boolean): IPushee<A> = object : IPushee<A> {
    var found = false
    override fun invoke(a: A) {
        if (found)
            return this@adropWhile(a)
        if (pred(a))
            return
        found = true
        return this@adropWhile(a)
    }
}

/** drops items as long as specified predicate holds in loop on first call, then forwards next items as they are one by one */
fun <V> IPullee<V>.vdropWhile(pred: (V) -> Boolean): IPullee<V> = object : IPullee<V> {
    var found = false
    override fun invoke(u: Unit): V {
        var v = this@vdropWhile(Unit)
        if (found)
            return v
        while (true) {
            if (!pred(v)) {
                found = true
                return v
            }
            v = this@vdropWhile(Unit)
        }
    }
}


/**
 * NOTE1: see vzip first - it is more straightforward; this azip requires some backward thinking :-)
 * NOTE3: usually we compose azip with amap, so this Pair type is only temporary
 * NOTE4: This suprising pullee here (in push based composition) is on purpose (it is not a mistake).
 */
fun <A, B> IPushee<Pair<A, B>>.azip(pullee: IPullee<B>): IPushee<A> = { this(it to pullee(Unit)) }

/**
 * NOTE: usually we compose vzip with vmap, so this Pair type is only temporary
 */
fun <V, W> IPullee<V>.vzip(pullee: IPullee<W>): IPullee<Pair<V, W>> = { this(Unit) to pullee(Unit) }


fun <A, B> IPushee<Pair<A, B>?>.anzip(pullee: IPullee<B?>): IPushee<A?> = { a: A? ->
    val b = pullee(Unit)
    this(if (a === null || b === null) null else a to b)
}

fun <V, W> IPullee<V?>.vnzip(pullee: IPullee<W?>): IPullee<Pair<V, W>?> = {
    val x = this(it)
    val y = pullee(Unit)
    if (x === null || y === null)
        null
    else
        x to y
}

// NOTE: we could implement vnzip using vzip, but it would be slower:
//fun <V, W> IPullee<V?>.vnzip(pullee: IPullee<W?>): IPullee<Pair<V, W>?> = vzip(pullee).vmap {
//    if(it.first === null || it.second === null) null else it.first!! to it.second!!
//}

// TODO tests for all zips

// TODO lzips...


/** TODO LATER: review it; documentation; implement some tests */
fun <V> IPullee<IPullee<V?>?>.vnflat(): IPullee<V?> = object : IPullee<V?> {
    var provider: Function1<Unit, V?>? = null
    var end = false
    override tailrec fun invoke(u: Unit): V? {
        if (end) return null
        if (provider === null)
            provider = this@vnflat(Unit) // ask for next provider
        if (provider === null) {
            end = true
            return null
        }
        val v = provider?.invoke(Unit)
        if (v !== null)
            return v
        provider = null
        return invoke(Unit)
    }
}

// NOTE: anflat doesn't make sense - pushee is just a passive consumer;
// but we will implement flat like operator(s) for pushers (active side of push based communication) (like flatMap or switchMap in rx)


// NOW THE ACTIVE SIDE: PUERS:


// This is similar to lift in RxJava, but works not only for IPushers (IPuer<T, Unit>) but all IPuers (IPullers too)
fun <T, R, X, Y, C> IPuer<T, R, C>.lift(oper: (IPuee<X, Y>) -> IPuee<T, R>) = object : IPuer<X, Y, C> {
    override fun invoke(f: (X) -> Y): (C) -> Unit = this@lift(oper(f))
}
// TODO: tests, examples


/**
 * Adds a mapping to pushed items of IPuer. Returned values (if any) are left as they are.
 * similar to rx: Observable.map, but it works not only for IPushers, but for all IPuers.
 */
fun <A, B, V, C> IPuer<A, V, C>.lamap(f: Function1<A, B>): IPuer<B, V, C> = lift { f * it }
// TODO: tests, examples, check in practice if we even need to define it.. (probably yes: to avoid 'functional headache')

/**
 * Adds a mapping to returned values to IPuers. Arguments are not changed.
 */
fun <A, V, W, C> IPuer<A, W, C>.lvmap(f: Function1<V, W>): IPuer<A, V, C> = lift { it * f }
// TODO: tests, examples, check in practice if we even need to define it.. (probably yes: to avoid 'functional headache')


fun <A, B, V, C> IPuer<A?, V, C>.lanmap(f: Function1<A, B>): IPuer<B?, V, C> = lamap(n(f))
fun <A, V, W, C> IPuer<A, W?, C>.lvnmap(f: Function1<V, W>): IPuer<A, V?, C> = lvmap(n(f))

fun <A, B, V, C> IPuer<IEvent<A>?, V, C>.laemap(f: Function1<A, B>): IPuer<IEvent<B>?, V, C> = lamap(e(f))
fun <A, V, W, C> IPuer<A, IEvent<W>?, C>.lvemap(f: Function1<V, W>): IPuer<A, IEvent<V>?, C> = lvmap(e(f))


fun <A, V, C> IPuer<A, V, C>.lapeek(spy: Function1<A, Unit>): IPuer<A, V, C> = lift { f -> { spy(it); f(it) } }
// TODO: tests, examples, check in practice if we even need to define it.. (probably yes: to avoid 'functional headache')

fun <A, V, C> IPuer<A, V, C>.lvpeek(spy: Function1<V, Unit>): IPuer<A, V, C> = lift { it % spy }
// TODO: tests, examples, check in practice if we even need to define it.. (probably yes: to avoid 'functional headache')


// lpeeks that ignore null values:
fun <A, V, C> IPuer<A?, V, C>.lanpeek(spy: Function1<A, Unit>): IPuer<A?, V, C> = lift { f -> { if (it !== null) spy(it); f(it) } }

fun <A, V, C> IPuer<A, V?, C>.lvnpeek(spy: Function1<V, Unit>): IPuer<A, V?, C> = lift { it % { if (it !== null) spy(it) } }


fun <A, C> IPusher<A, C>.lafilter(pred: Function1<A, Boolean>): IPusher<A, C> = lift<A, Unit, A, Unit, C> { it.afilter(pred) } // TODO: tests, examples


fun <A, C> IPusher<A, C>.latake(n: Long) = lift<A, Unit, A, Unit, C> { it.atake(n) }
fun <A, C> IPusher<A?, C>.lantake(n: Long) = lift<A?, Unit, A?, Unit, C> { it.antake(n) }
fun <V, C> IPuller<V?, C>.lvntake(n: Long) = lift<Unit, V?, Unit, V?, C> { it.vntake(n) }
fun <A, C> IPusher<A, C>.ladrop(n: Long) = lift<A, Unit, A, Unit, C> { it.adrop(n) }
fun <V, C> IPuller<V, C>.lvdrop(n: Long) = lift<Unit, V, Unit, V, C> { it.vdrop(n) }


fun <A, C> IPusher<A, C>.latakeWhile(pred: Function1<A, Boolean>) = lift<A, Unit, A, Unit, C> { it.atakeWhile(pred) }
fun <A, C> IPusher<A?, C>.lantakeWhile(pred: Function1<A, Boolean>) = lift<A?, Unit, A?, Unit, C> { it.antakeWhile(pred) }
fun <V, C> IPuller<V?, C>.lvntakeWhile(pred: Function1<V, Boolean>) = lift<Unit, V?, Unit, V?, C> { it.vntakeWhile(pred) }
fun <A, C> IPusher<A, C>.ladropWhile(pred: Function1<A, Boolean>) = lift<A, Unit, A, Unit, C> { it.adropWhile(pred) }
fun <V, C> IPuller<V, C>.lvdropWhile(pred: Function1<V, Boolean>) = lift<Unit, V, Unit, V, C> { it.vdropWhile(pred) }


fun <T> pusheeOf(pushees: List<IPushee<T>>) = { t: T -> for (p in pushees) p(t) }
fun <T> pusheeOf(vararg pushees: IPushee<T>) = pusheeOf(listOf(*pushees))

fun <T, Cmd> merge(pushers: List<IPusher<T, Cmd>>) = object : IPusher<T, Cmd> {
    override fun invoke(pushee: IPushee<T>): IPushee<Cmd> {
        val controllers = pushers.map { it(pushee) }
        return pusheeOf(controllers)
    }
}

fun <T, Cmd> merge(vararg pushers: IPusher<T, Cmd>) = merge(listOf(*pushers))

// TODO: think it through again (this is just a fast experiment and I have no idea if it is correct at all)
// TODO: Syntax can be shortened, but I postpone using clever kotlin syntax to keep it more explicit until I'm sure it's correct.
// TODO: Common implementation for all base types (pull based too, passive too); use "lift"
fun <A, T, Cmd> IPusher<T, Cmd>.scan(seed: A, reduce: (Pair<A, T>) -> A): IPusher<A, Cmd> = object : IPusher<A, Cmd> {
    var acc = seed
    override fun invoke(apushee: IPushee<A>): IPushee<Cmd> {
        val tpushee = { t: T -> acc = reduce(acc to t); apushee(acc) }
        return this@scan(tpushee)
    }
}

fun <T, Cmd> IPusher<T, Cmd>.withLast(seed: T) = scan(seed to seed) { (last, curr) -> last.second to curr }

fun <T, Cmd> IPusher<T, Cmd>.dropRepeats(seed: T, equals: (Pair<T, T>) -> Boolean = { it.first == it.second })
        = withLast(seed)
        .lafilter { !equals(it) }
        .lamap { it.second }


// TODO: tests, examples for all these take and drop versions..


interface IPush<in T> {
    val push: IPushee<T>
}

interface IPull<out T> {
    val pull: IPullee<T>
}

interface IPeek<out T> {
    val peek: IPullee<T>
        get() = throw UnsupportedOperationException() // it usually is "an optional operation" so by default it throws.
}

class Remove<I>(private val i: I, private val items: MutableCollection<I>) : Function1<Cancel, Unit> {
    override fun invoke(c: Cancel) {
        items.remove(i)
    } // it does nothing if i is already removed
}


/**
 * This is something similar to rx: Subject (or to Jake Wharton library: RxRelay) - but as always: it is simpler ;-)
 * A relay is a pusher you can attach many pushees. Every time you get a Unit -> Unit function you can use to detach your pushee.
 * The relay itself has a "pushee" property that - when called - forwards given item to all currently attached pushees.
 * TODO SOMEDAY: thread-safe version?
 */
class Relay<A>(initcap: Int = 16) : IPusher<A, Cancel>, IPush<A> {

    private val pushees = ArrayList<Function1<A, Unit>>(initcap)

    override val push: Function1<A, Unit> = { for (p in pushees) p(it) }

    override fun invoke(p: (A) -> Unit): (Cancel) -> Unit {
        pushees.add(p)
        return Remove(p, pushees)
        // TODO: check why not just lambda instead of Remove class:
        //  return { _: Cancel -> pushees.remove(p); Unit }
    }
}


/**
 * Small experiment: A kind of dual class for Relay, but for pull based communication.
 * I don't know yet if it will be useful at all, or isn't it too confusing..
 * IMPORTANT: when we pull from it: it does not pull from all attached pullees immediately
 * instead it returns special NPullee, and user can iterate through it to get one item from each attached pullee.
 * TODO LATER: test it!!
 */
class Yaler<R>(initcap: Int = 16) : IPuller<R, Cancel>, IPull<Function1<Unit, R?>> {

    private val pullees = ArrayList<Function1<Unit, R>>(initcap)

    override val pull: Function1<Unit, Function1<Unit, R?>> = { pullees.asNPullee().vnmap { it(Unit) } }

    override fun invoke(p: (Unit) -> R): (Cancel) -> Unit {
        pullees.add(p)
        return Remove(p, pullees)
        // TODO: check why not just lambda instead of Remove class (as in Relay)
    }
}


// TODO SOMEDAY: use type alias: Pushee<A>
fun <A> IPushee<A>.reschedule(scheduler: IScheduler): IPushee<A> = { a: A -> scheduler.schedule { this(a) } }
// WARNING: if we use scheduler that can call our function from more than one thread - we usually should FIRST wrap the original function
// in "sync" extension function that synchronizes these calls and assures the happens-before relationship between calls.


fun <A, V> IPuee<A, V>.sync(): IPuee<A, V> = { a: A -> synchronized(this) { this(a) } }
// it synchronizes calls and assures the happens-before relationship between calls.


// TODO NOW: WARNING: Now we try to just "lift" these reschedule and sync extension functions to pushers...
// TODO NOW: This is suspiciously concise. So it is probably buggy (to good to be correct), so:
// TODO NOW: it needs A LOT of good testing...

/**
 * This operator should work similarly to rx: observeOn. But it does NOT synchronize pushing items,
 * so you almost always want to add an operator "lsync" right after this one.
 */
fun <A, C> IPusher<A, C>.lreschedule(scheduler: IScheduler): IPusher<A, C> = lift { it.reschedule(scheduler) }
// WARNING 1: we use lift, so all ICommands (if our Pusher supports ICommands) are just passed to upstream Pusher,
// so for example the Cancel command will just be passed upstream and it will NOT cancel any item already scheduled with provided scheduler
// WARNING 2: We do not implement any backpressure here, so if upstream pusher is too fast - it will overwhelm our scheduler, and
// it can create too many threads (or in general consume to much resources)! (and crash..)

/**
 * This operator makes sure all items are pushed serially and that there is the happens-before relationship between pushes.
 */
fun <A, C> IPusher<A, C>.lsync(): IPusher<A, C> = lift { it.sync() }


// TODO SOMEDAY MAYBE: add Pue performance test to David Karnok android test app for different reactive solutions:
// https://github.com/akarnokd/AgeraTest2


// TODO LATER: implement a lot of most used stuff from Rx
// (and maybe from Iterables/Iterators/Java8streams/ guava fluent iterators???)
// TODO LATER: first try these: reduce, scan, zip, combineLatest, concat, concatMap, switchMap?, flatMapIterable?, merge, switchOnNext?, split?


// TODO LATER: zrobic ladny zestaw operatorow odpowiadajacych Rx nawet dla tych dla ktorych u nas wystarczy cos banalnego jak skladanie funkcji
// po to zeby byla dokumentacja jak u nas robimy rzeczy Rxowe i testy czy rzeczywiscie tak latwo dziala(robic inline gdzie sie da!)
// oczywiscie gdzie sie da nie robic osobno dla push i dla pull, tylko wspolna implementacje...


// TODO LATER: konwertery miedzy swiatami: Pue, RxJava, Iterables, Sequences, itp..
// TODO LATER: wrappery tamtych swiatow do Pue - czyli zeby np opakowac rx.Observable zeby spelnial interfejs: IPusher...
// i porzadne testy czy cos takiego moze dzialac... moze wrappery operatorow tez da sie?? wtedy trzebaby odrozniac ktory operator
// odpalamy: czy ten pod spodem (RxJavowy), czy nasz zewnetrzny Pueowy...


// UPDATE: Komentarz ponizej jest troche przestarzaly, ale pomyslec jeszcze nad jakas forma podzielenia puerow na Factory i prostsze IPuer
// (na przyklad zaimplementowany juz Timer to wlasciwie Factory of SingleTimer, tylko na razie implicit..)

// TODO LATER: Zdefiniowac klase: Factory - troche odpowiednik cold observable.I ona bedzie zwracac puery ktore sluchaja komend.
// czyli puery beda kontrolerami, a nie beda zwracac kontrolerow. Dzieki temu nie bedzie problemu w przypdku synchronicznej komunikacji,
// ze w trakcie subscribowania od razu puer dziala i leci z itemami az skonczy i dopiero dostajemy kontrolera. I dopiero mozemy
// jej np przerwac jak ona juz skonczyla... (a moze nigdy nie skonczy..) To nam pozwoli zaimplementowac cos jak Observable.take, ktore
// canceluje puera jak juz nie potrzebuje wiecej itemow.
// Acha ten nasz nowy puer  - jesli bedzie obslugiwal komendy (powiedzmy ICMPuer : IPuer) - bedzie dziedziczyl po dwoch! Function1
// czyli bedzie mial dwie! metody invoke. Jedna bioraca funkcje, a druga bioraca komendy - nie powinno byc problemu bo to rozne typy danych.
// Czyli IPuer<T, R> : IPushee<Function1<T, R> (jak juz beda aliasy..)
// Czyli: ICPuer : IPuer, Function1<ICommand, Unit> (a Funct... zmienimy na IPushee jak beda aliasy..)
// I jeszcze chyba IECPuer : IEPuer, Function1<ICommand, Unit> (a Funct... zmienimy na IPushee jak beda aliasy)
// A nawet: IECPusher<T> :  IPushee<IPushee<IEvent<T>>>, IPushee<ICommand> (jak beda aliasy) (jakos tak...)


