package pl.mareklangiewicz.myutils

import android.os.Handler
import android.os.Looper
import android.support.annotation.MainThread
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Created by Marek Langiewicz on 20.02.16.
 */


/**
 * My Machines is a kind of minimalist functional active/passive library
 *
 * By "active" I mean object that decides itself when to do something (machine)
 * By "passive" I mean object that has public method others can call and it just reacts (function)
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
 * Two active interfaces: IPusher and IPuller are in fact just special examples of IMachine interface.
 * IPuller<T> = IMachine<Unit, T>; IPusher<T> = IMachine<T, Unit>
 * And the IMachine<T, R> is just a class that calls attached Function1<T, R> when it wants to.
 * The machine (as an active side of composition) decides when and how many times to call attached function.
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


/**
 * IMachine interface is the core of this library. It is an active side of communication.
 * It takes a function and calls it whenever it wants to :-)
 * Usually it returns some other function that potentially can be used to control the behavior of the machine
 * regarding given function. (But I do not define any specific return type here yet. Concrete machines will.)
 * Usage: You give the machine your function, and from now on, you do not call your function manually,
 * but machine does it for you. And machine usually gives you some other function: the controller,
 * that can give you some control on how and when the machine call your original function.
 * What kind of controller you get depends on particular machine implementation.
 * The simplest controller would be just: Function1<Unit, Unit> that just detaches
 * the original function from the machine entirely so it is never called again (like rx: unsubscribe).
 * But we will also implement machines that return more sophisticated controllers like:
 * Function1<Command, Unit> where the Command can be: Start, Stop, Pause, Cancel, etc...
 *
 */
interface IMachine<out T, in R, out H> : Function1<Function1<T, R>, H>
    // SOMEDAY: use alias when Kotlin supports type aliases..

/** passive producer (similar to iterator) */
//alias IPullee<out R> = Function1<Unit, R> // SOMEDAY: use alias when Kotlin supports type aliases..

/** passive consumer (similar to rx: observer) */
//alias IPushee<in T> = Function1<T, Unit> // SOMEDAY: use alias when Kotlin supports type aliases..


/** active consumer (similar to java 8: collector) */
interface IPuller<in R, out H> : IMachine<Unit, R, H> // SOMEDAY: use alias when Kotlin supports type aliases..

// temporary wrapper..
// TODO SOMEDAY: remove this wrapper when we have type aliases
class Puller<in R, out H>(private val m: IMachine<Unit, R, H>): IPuller<R, H> {
    override fun invoke(f: (Unit) -> R): H {
        return m(f)
    }
}


/** active producer (similar to rx.observable) */
interface IPusher<out T, out H> : IMachine<T, Unit, H> // SOMEDAY: use alias when Kotlin supports type aliases..


// temporary wrapper..
// TODO SOMEDAY: remove this wrapper when we have type aliases
class Pusher<out T, out H>(private val m: IMachine<T, Unit, H>): IPusher<T, H> {
    override fun invoke(f: (T) -> Unit): H {
        return m(f)
    }
}


// IMPORTANT:
// First (and most general) extension to our basic protocol defined by interfaces above is: null value means: end of the stream.
// So for example if you want to have FINITE streams - use nullable value type. See "asNPullee" below for simple example.
// We will call pushers of nullable streams: npushers; pullers: npullers; etc. (npushee; npushee)



// NPullee means Pullee that returns null when the stream has ended.
fun <R> Iterator<R>.asNPullee() = { u: Unit -> if(hasNext()) next() else null }
fun <R> Iterable<R>.asNPullee() = iterator().asNPullee()
fun <R> Sequence<R>.asNPullee() = iterator().asNPullee()

fun <R> nPulleeOf(vararg ts: R) = ts.iterator().asNPullee()

// Examples how to create simple NPullee:
// (1..50).asNPullee()
// listOf("bla", "ble").asNPullee()
// nPulleeOf('a', 'b', 'c')


/**
 * This allows to use for in loop to traverse through NPullee.
 * WARNING: iterating destroys INPullee so you can do it only once!
 * TODO: tests and examples
 */
//fun <R> IPullee<R?>.niter() = object : Iterable<R> { // SOMEDAY: enable when we have aliases
fun <R> Function1<Unit, R?>.niter() = object : Iterable<R> {
    override operator fun iterator() = object: Iterator<R> {

        var current = invoke(Unit)

        override fun hasNext(): Boolean {
            return current !== null
        }

        override fun next(): R {
            val r = current ?: throw IllegalStateException("No more items available.")
            current = invoke(Unit)
            return r
        }
    }
}

/**
 * makes sure we stop asking an upstream function for next elements after we get a null value once
 * The U type parameter here will usually be just a Unit (then our function is just a pullee).
 * But I leave U type unspecified here to be a little more generic.
 */
fun <U, R> Function1<U, R?>.nnn() = object : Function1<U, R?> {
    var end = false
    override fun invoke(u: U): R? = if(end) null else this@nnn(u) ?: null.apply { end = true }
}






/**
 * We can be more like RxJava by using some wrapper for items.
 * We also can easily add more types of events when needed.
 * And different machines can support different subset of events..
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




// A machine can return some IPushee<ICommand> you can use to control the way it works.
interface ICommand
object Cancel : ICommand // can be used similarly to rx: Subscription.unsubscribe
// Every emachine should support Cancel command. Other commands are optional.
object Start : ICommand // can be used similarly to rx: ConnectableObservable.connect
object Stop : ICommand
object Pause : ICommand
object Step : ICommand
object Tick : ICommand
object Tock : ICommand

data class Request(val count: Long) : ICommand // for future backpressure implementation (for particular IMachines only)
// Machine should keep track how many items are requested so far and how many has been emited already - and NOT emit more than requested.

// user can add more special case commands for his special machines. For example:
data class Inject<T>(val item: T) : ICommand // Commanding machine to push additional item provided "by hand" immediately.






// Our base interfaces using IEvent and ICommand (with prefix E):

//alias IEPullee<out R> = IPullee<IEvent<R>?> // SOMEDAY: use alias when Kotlin supports type aliases..


//alias IEPushee<in T> = IPushee<IEvent<T>?> // SOMEDAY: use alias when Kotlin supports type aliases..



//alias IEPuller<in R> = IPuller<IEvent<R>?, IPushee<ICommand>> // SOMEDAY: use alias when Kotlin supports type aliases..
interface IEPuller<in R> : IPuller<IEvent<R>?, Function1<ICommand, Unit>>


// Temporary wrapper.. // SOMEDAY: remove when we have type aliases
class EPuller<in R>(private val p: IPuller<IEvent<R>?, Function1<ICommand, Unit>>): IEPuller<R> {
    override fun invoke(f: (Unit) -> IEvent<R>?): (ICommand) -> Unit {
        return p(f)
    }
}

//alias IEPusher<out T> = IPusher<IEvent<T>?, IPushee<ICommand>> // SOMEDAY: use alias when Kotlin supports type aliases..
interface IEPusher<out T> : IPusher<IEvent<T>?, Function1<ICommand, Unit>>

class EPusher<out T>(private val p: IPusher<IEvent<T>?, Function1<ICommand, Unit>>): IEPusher<T> { // SOMEDAY: remove when we have type aliases
    override fun invoke(f: (IEvent<T>?) -> Unit): (ICommand) -> Unit {
        return p(f)
    }
}





//fun <R> Iterator<R>.asEPullee(): IEPullee<R> = { if(hasNext()) Item(next()) else null } // SOMEDAY: enable when we have aliases
fun <R> Iterator<R>.asEPullee(): Function1<Unit, IEvent<R>?> = { if(hasNext()) Item(next()) else null }

fun <R> Iterable<R>.asEPullee() = iterator().asEPullee()

fun <R> Sequence<R>.asEPullee() = iterator().asEPullee()

fun <R> ePulleeOf(vararg ts: R) = ts.iterator().asEPullee()

// Examples how to create simple EPullee:
// (1..50).asEPullee()
// listOf("bla", "ble").asEPullee()
// ePulleeOf('a', 'b', 'c')



// It will extract items; pass through nulls; throw on errors and any other IEvents
fun <I> Function1<Unit, IEvent<I>?>.vitems() = vnmap {
    when(it) {
        is Item -> it.item
        else -> throw IllegalStateException("Unsupported event: $it")
    }
}

// Dual to vitems but for pushees (reminder: pushee is a kind of passive consumer)
// implementation is simple but it requres some "backward" thinking to really understand it :)
fun <I> Function1<I?, Unit>.aitems() = anmap<I, Unit, IEvent<I>?> {
    when(it) {
        is Item -> it.item
        else -> throw IllegalStateException("Unsupported event: $it")
    }
}



@Deprecated("Just use .vitems().niter() explicitly.", ReplaceWith("vitems().niter()"))
fun <R> Function1<Unit, IEvent<R>?>.eiter() = vitems().niter()




interface IScheduler {
    /**
     * delay is in milliseconds; not positive value means: no delay.
     * Returns the "cancel" function. Which cancels scheduled action when invoked.
     * (If some implementation does not support delays - it can throw UnsupportedOperationException if delay is > 0)
     * No delay does not have to mean: immediately - actual execution time is implementation specific.
     */
    fun schedule(delay: Long = 0, action: Function1<Unit, Unit>): Function1<Unit, Unit>
}
// Some schedulers may ensure happenss-before relationship between scheduled actions - it should be documented in particular scheduler.


// TODO LATER: test it (this is just first fast attempt to implement something simple - it will probably be rewritten)
class ExecutorScheduler(private val ses: ScheduledExecutorService) : IScheduler {
    constructor(threads: Int = Runtime.getRuntime().availableProcessors() * 2) : this(Executors.newScheduledThreadPool(threads))
    override fun schedule(delay: Long, action: (Unit) -> Unit): (Unit) -> Unit {
        val sf = ses.schedule({ action(Unit) }, delay, TimeUnit.MILLISECONDS)
        return { sf.cancel(true) }
    }
}


class HandlerScheduler(private val handler: Handler = Handler(Looper.getMainLooper())) : IScheduler {
    override fun schedule(delay: Long, action: (Unit) -> Unit): (Unit) -> Unit {
        val runnable = Runnable { action(Unit) }
        if(delay > 0)
            handler.postDelayed(runnable, delay)
        else
            handler.post(runnable)
        return { handler.removeCallbacks(runnable) }
    }
}

/**
 * Wraps a scheduler adding additional delay to every schedule.
 */
fun IScheduler.delay(add: Long): IScheduler = object : IScheduler {
    override fun schedule(delay: Long, action: (Unit) -> Unit): (Unit) -> Unit {
        return this@delay.schedule(delay + add, action)
    }
}
// TODO LATER: test it


/**
 * Wraps a scheduler and adds catching exceptions and logging them using provided logger
 */
fun IScheduler.logex(log: Function1<MyLogEntry, Unit>): IScheduler = object : IScheduler {
    override fun schedule(delay: Long, action: (Unit) -> Unit): (Unit) -> Unit {
        return this@logex.schedule(delay) { try { action(Unit) } catch(e: Throwable) { log.e(e, throwable = e) } }
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
open class StepPusher( val step: Long = 1, val tick: Long = 1, val tock: Long = 1 ) : IPusher<Long, Function1<ICommand, Unit>> {

    protected class Pusher(
            private var function: Function1<Long, Unit>?,
            val step: Long = 1,
            val tick: Long = 1,
            val tock: Long = 1
    ) : Function1<ICommand, Unit> {

        var started = false

        override fun invoke(cmd: ICommand) {
            when(cmd) {
                Cancel -> {
                    started = false
                    function = null
                }
                Stop, Pause -> started = false
                Start -> started = true
                Step -> for(i in 0..step-1) if(started) function?.invoke(i) else break
                Tick -> for(i in 0..tick-1) if(started) function?.invoke(i) else break
                Tock -> for(i in 0..tock-1) if(started) function?.invoke(i) else break
                is Inject<*> -> if(started) function?.invoke(cmd.item as Long)
                else -> throw IllegalArgumentException("Unsupported command.")
            }
        }
    }

    override fun invoke(f: Function1<Long, Unit>): Function1<ICommand, Unit> {
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
 * TODO SOMEDAY: use alias type for intervals: IPullee<Long?> (when we have type aliases)
 */
class Timer(private val scheduler: IScheduler, private val intervals: Function1<Unit, Long?>)
: IPusher<Long?, Function1<ICommand, Unit>> {

    override fun invoke(pushee: Function1<Long?, Unit>): Function1<ICommand, Unit> = SingleTimer(pushee)

    private inner class SingleTimer(private var pushee: Function1<Long?, Unit>?) : Function1<ICommand, Unit> {

        private var counter = 0L

        private var unschedule: Function1<Unit, Unit>? = null

        private val action = { u: Unit ->
            pushee?.invoke(counter++)
            invoke(Start)
        }

        override tailrec fun invoke(cmd: ICommand) {

            when(cmd) {

                Step, Tick, Tock -> pushee?.invoke(counter++)

                Stop, Pause -> {
                    unschedule?.invoke(Unit);
                    unschedule = null
                }

                Cancel -> {
                    unschedule?.invoke(Unit);
                    unschedule = null
                    pushee = null
                }

                Start -> {

                    if(pushee === null) return

                    val interval = intervals(Unit)

                    if(interval === null) {
                        pushee?.invoke(null)
                        invoke(Cancel)
                        return
                    }

                    if(interval <= 0L) {
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
fun <A, V, B> Function1<A, V>.amap(f: Function1<B, A>): Function1<B, V> = f * this

/** maps result value from function (similar to Sequence.map) - actually just a function composition */
fun <A, V, W> Function1<A, V>.vmap(f: Function1<V, W>): Function1<A, W> = this * f


/** forwards null values down the stream */
fun <A, V> n(f: Function1<A, V>): Function1<A?, V?> = { if(it === null) null else f(it) }

/** maps items and forwards other events (and nulls) */
fun <A, V> e(f: Function1<A, V>): Function1<IEvent<A>?, IEvent<V>?> = {
    when (it) {
        is Item -> Item(f(it.item))
        is Warning -> Warning(it.warning)
        is Error -> Error(it.error)
        null -> null
        else -> throw UnsupportedOperationException("Unknown IEvent.")
    }
}
// TODO SOMEDAY: maybe something that captures thrown exceptions in Error IEvent?


fun <A, V, B> Function1<A?, V>.anmap(f: Function1<B, A>): Function1<B?, V> = n(f) * this
fun <A, V, W> Function1<A, V?>.vnmap(f: Function1<V, W>): Function1<A, W?> = this * n(f)

fun <A, B, V> Function1<IEvent<A>?, V>.aemap(f: Function1<B, A>): Function1<IEvent<B>?, V> = (e(f) * this)
fun <A, V, W> Function1<A, IEvent<V>?>.vemap(f: Function1<V, W>): Function1<A, IEvent<W>?> = (this * e(f))



/** allows to implement some side effect every time function is called with some argument */
fun <A, V> Function1<A, V>.apeek(spy: Function1<A, Unit>): Function1<A, V> = { spy(it); this(it) }

/** allows to implement some side effect every time function returns some value (similar to rx: doOnNext but pull based) */
fun <A, V> Function1<A, V>.vpeek(spy: Function1<V, Unit>): Function1<A, V> = this % spy


// peeks that ignore null values:
fun <A, V> Function1<A?, V>.anpeek(spy: Function1<A, Unit>): Function1<A?, V> = { if(it !== null) spy(it); this(it) }
fun <A, V> Function1<A, V?>.vnpeek(spy: Function1<V, Unit>): Function1<A, V?> = this % { if(it !== null) spy(it) }


/** forwards given items to IPushee, but only if they meet given predicate */
fun <A> Function1<A, Unit>.afilter(pred: Function1<A, Boolean>): Function1<A, Unit> = { if(pred(it)) this(it) }

/** Something like Sequence.filter - warning: it will block until next matching item is found.
 * Usually we will use it with nullable streams and return true for null value (so it does not block forever).
 */
fun <V> Function1<Unit, V>.vfilter(pred: Function1<V, Boolean>): Function1<Unit, V> = object : Function1<Unit, V> {
    override fun invoke(u: Unit): V {
        var b = this@vfilter(Unit)
        while(!pred(b))
            b = this@vfilter(Unit)
        return b
    }
}

@Deprecated("Just use vfilter with predicate that returns true for null", ReplaceWith("vfilter"))
fun <V> Function1<Unit, V?>.vnfilter(pred: Function1<V, Boolean>) = vfilter { it === null || pred(it) }

/** pushes forward only specified number of items */
fun <A> Function1<A, Unit>.atake(n: Long): Function1<A, Unit> = object : Function1<A, Unit> {
    var count = n // it is public so user can potentially alter it anytime and make it push some more items.
    override fun invoke(a: A) = if(count-- > 0L) this@atake(a) else Unit
}

/** pushes forward only specified number of items, then one null value, and then nothing else */
fun <A> Function1<A?, Unit>.antake(n: Long): Function1<A?, Unit> = object : Function1<A?, Unit> {
    var count = n // it is public so user can potentially alter it anytime and make it push some more items.
    override fun invoke(a: A?) { // if a is null it is ok too (because our protocol says it can happen only once)
        if(count > 0L)
            this@antake(a)
        else if(count == 0L)
            this@antake(null)
        count --
    }
}

/** pulls only specified number of items, then returns null values (similar to Kotlin: Sequence.take) */
fun <V> Function1<Unit, V?>.vntake(n: Long): Function1<Unit, V?> = object : Function1<Unit, V?> {
    var count = n // it is public so user can potentially alter it anytime and make it pull some more items.
    override fun invoke(u: Unit): V? = if(count-- > 0) this@vntake(Unit) else null
}





/** start pushing forward after specified number of (dropped) items */
fun <A> Function1<A, Unit>.adrop(n: Long): Function1<A, Unit> = object : Function1<A, Unit> {
    var count = n // it is public so user can potentially alter it anytime and make it drop some more items.
    override fun invoke(a: A) = if(count-- <= 0) this@adrop(a) else Unit
}

/** drops first n items immediately when first item is pulled, then forwards items as they are */
fun <V> Function1<Unit, V>.vdrop(n: Long): Function1<Unit, V> = object : Function1<Unit, V> {
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
        if(end)
            return
        if(pred(a))
            return this@atakeWhile(a)
        end = true
    }
}

/** forwards items as long as specified predicate holds, then forwards null item, then nothing more */
fun <A> Function1<A?, Unit>.antakeWhile(pred: Function1<A, Boolean>): Function1<A?, Unit> = object : Function1<A?, Unit> {
    var end = false
    override fun invoke(a: A?) {
        if(end)
            return
        if(a !== null && pred(a))
            return this@antakeWhile(a)
        end = true
        this@antakeWhile(null)
    }
}

/** forwards items as long as specified predicate holds, then forwards null items */
fun <V> Function1<Unit, V?>.vntakeWhile(pred: Function1<V, Boolean>): Function1<Unit, V?> = object : Function1<Unit, V?> {
    var end = false
    override fun invoke(u: Unit): V? {
        if(end)
            return null
        val v = this@vntakeWhile(Unit)
        if(v !== null && pred(v))
            return v
        end = true
        return null
    }
}





/** drops items as long as specified predicate holds, then forwards all next items as they are */
fun <A> Function1<A, Unit>.adropWhile(pred: Function1<A, Boolean>): Function1<A, Unit> = object : Function1<A, Unit> {
    var found = false
    override fun invoke(a: A) {
        if(found)
            return this@adropWhile(a)
        if(pred(a))
            return
        found = true
        return this@adropWhile(a)
    }
}

/** drops items as long as specified predicate holds in loop on first call, then forwards next items as they are one by one */
fun <V> Function1<Unit, V>.vdropWhile(pred: Function1<V, Boolean>): Function1<Unit, V> = object : Function1<Unit, V> {
    var found = false
    override fun invoke(u: Unit): V {
        var v = this@vdropWhile(Unit)
        if(found)
            return v
        while(true) {
            if(!pred(v)) {
                found = true
                return v
            }
            v = this@vdropWhile(Unit)
        }
    }
}


/**
 * NOTE1: see vzip first - it is more straightforward; this azip requires some backward thinking :-)
 * NOTE2: usually V here is just Unit, so our receiver function and returned function are just pushees
 * NOTE3: usually we compose azip with amap, so this Pair type is only temporary
 * NOTE4: This suprising pullee here (in push based composition) is on purpose (it is not a mistake).
 */
fun <A, B, V> Function1<Pair<A, B>, V>.azip(pullee: Function1<Unit, B>): Function1<A, V> = { this(it to pullee(Unit)) }

/**
 * NOTE1: usually A here is just Unit, so our receiver function and returned function are just pullees
 * NOTE3: usually we compose vzip with vmap, so this Pair type is only temporary
 */
fun <A, V, W> Function1<A, V>.vzip(pullee: Function1<Unit, W>): Function1<A, Pair<V, W>> = { this(it) to pullee(Unit) }



fun <A, B, V> Function1<Pair<A, B>?, V>.anzip(pullee: Function1<Unit, B?>): Function1<A?, V> = { a: A? ->
    val b = pullee(Unit)
    this(if(a === null || b === null) null else a to b)
}

fun <A, V, W> Function1<A, V?>.vnzip(pullee: Function1<Unit, W?>): Function1<A, Pair<V, W>?> = {
    val x = this(it)
    val y = pullee(Unit)
    if(x === null || y === null)
        null
    else
        x to y
}

// NOTE: we could implement vnzip using vzip, but it would be slower:
//fun <A, V, W> Function1<A, V?>.vnzip(pullee: Function1<Unit, W?>): Function1<A, Pair<V, W>?> = vzip(pullee).vmap {
//    if(it.first === null || it.second === null) null else it.first!! to it.second!!
//}

// TODO tests for all zips

// TODO lzips...




/** TODO LATER: review it; documentation; implement some tests */
fun <V> Function1<Unit, Function1<Unit, V?>?>.vnflat(): Function1<Unit, V?> = object : Function1<Unit, V?> {
    var provider: Function1<Unit, V?>? = null
    var end = false
    override tailrec fun invoke(u: Unit): V? {
        if(end) return null
        if(provider === null)
            provider = this@vnflat(Unit) // ask for next provider
        if(provider === null) {
            end = true
            return null
        }
        val v = provider?.invoke(Unit)
        if(v !== null)
            return v
        provider = null
        return invoke(Unit)
    }
}

// NOTE: anflat doesn't make sense - pushee is just a passive consumer;
// but we will implement flat like operator(s) for pushers (active side of push based communication) (like flatMap or switchMap in rx)













// NOW THE ACTIVE SIDE: MACHINES:



// This is similar to lift in RxJava, but works not only for IPushers (IMachine<T, Unit>) but all IMachines (IPullers too)
fun <T, R, X, Y, H> IMachine<T, R, H>.lift(oper: (Function1<X, Y>) -> Function1<T, R>) = object : IMachine<X, Y, H>  {
    override fun invoke(f: (X) -> Y): H = this@lift(oper(f))
}
// TODO: tests, examples





/**
 * Adds a mapping to pushed items of IMachine. Returned values (if any) are left as they are.
 * similar to rx: Observable.map, but it works not only for IPushers, but for all IMachines.
 */
fun <A, B, V, H> IMachine<A, V, H>.lamap(f: Function1<A, B>): IMachine<B, V, H> = lift { f * it }
// TODO: tests, examples, check in practice if we even need to define it.. (probably yes: to avoid 'functional headache')

/**
 * Adds a mapping to returned values to IMachine. Arguments are not changed.
 */
fun <A, V, W, H> IMachine<A, W, H>.lvmap(f: Function1<V, W>): IMachine<A, V, H> = lift { it * f }
// TODO: tests, examples, check in practice if we even need to define it.. (probably yes: to avoid 'functional headache')


fun <A, B, H> IPusher<A, H>.lmap(f: Function1<A, B>): IPusher<B, H> = Pusher(lamap(f))
fun <V, W, H> IPuller<W, H>.lmap(f: Function1<V, W>): IPuller<V, H> = Puller(lvmap(f))


fun <A, B, V, H> IMachine<A?, V, H>.lanmap(f: Function1<A, B>): IMachine<B?, V, H> = lamap(n(f))
fun <A, V, W, H> IMachine<A, W?, H>.lvnmap(f: Function1<V, W>): IMachine<A, V?, H> = lvmap(n(f))

fun <A, B, H> IPusher<A?, H>.lnmap(f: Function1<A, B>): IPusher<B?, H> = lmap(n(f))
fun <V, W, H> IPuller<W?, H>.lnmap(f: Function1<V, W>): IPuller<V?, H> = lmap(n(f))

fun <A, B, V, H> IMachine<IEvent<A>?, V, H>.laemap(f: Function1<A, B>): IMachine<IEvent<B>?, V, H> = lamap(e(f))
fun <A, V, W, H> IMachine<A, IEvent<W>?, H>.lvemap(f: Function1<V, W>): IMachine<A, IEvent<V>?, H> = lvmap(e(f))

fun <A, B> IEPusher<A>.lemap(f: Function1<A, B>): IEPusher<B> = EPusher(lmap(e(f)))
fun <W, V> IEPuller<W>.lemap(f: Function1<V, W>): IEPuller<V> = EPuller(lmap(e(f)))



fun <A, V, H> IMachine<A, V, H>.lapeek(spy: Function1<A, Unit>): IMachine<A, V, H> = lift { f -> { spy(it); f(it) } }
// TODO: tests, examples, check in practice if we even need to define it.. (probably yes: to avoid 'functional headache')

fun <A, V, H> IMachine<A, V, H>.lvpeek(spy: Function1<V, Unit>): IMachine<A, V, H> = lift { it % spy }
// TODO: tests, examples, check in practice if we even need to define it.. (probably yes: to avoid 'functional headache')


fun <A, H> IPusher<A, H>.lpeek(spy: Function1<A, Unit>): IPusher<A, H> = Pusher(lapeek(spy))
fun <V, H> IPuller<V, H>.lpeek(spy: Function1<V, Unit>): IPuller<V, H> = Puller(lvpeek(spy))


// lpeeks that ignore null values:
fun <A, V, H> IMachine<A?, V, H>.lanpeek(spy: Function1<A, Unit>): IMachine<A?, V, H> = lift { f -> { if(it !== null) spy(it); f(it) } }
fun <A, V, H> IMachine<A, V?, H>.lvnpeek(spy: Function1<V, Unit>): IMachine<A, V?, H> = lift { it % { if(it !== null) spy(it) } }
fun <A, H> IPusher<A?, H>.lnpeek(spy: Function1<A, Unit>): IPusher<A?, H> = Pusher(lanpeek(spy))
fun <V, H> IPuller<V?, H>.lnpeek(spy: Function1<V, Unit>): IPuller<V?, H> = Puller(lvnpeek(spy))




fun <A, H> IPusher<A, H>.lfilter(pred: Function1<A, Boolean>): IPusher<A, H>
        = Pusher(lift<A, Unit, A, Unit, H> { it.afilter(pred) }) // TODO: tests, examples



fun <A, H> IPusher<A, H>.ltake (n: Long) = Pusher(lift<A, Unit, A, Unit, H> { it.atake (n) })
fun <A, H> IPusher<A?, H>.lntake(n: Long) = Pusher(lift<A?,Unit, A?,Unit, H> { it.antake(n) })
fun <V, H> IPuller<V?, H>.lntake(n: Long) = Puller(lift<Unit, V?, Unit, V?, H> { it.vntake(n) })
fun <A, H> IPusher<A, H>.ldrop (n: Long) = Pusher(lift<A, Unit, A, Unit, H> { it.adrop (n) })
fun <V, H> IPuller<V, H>.ldrop (n: Long) = Puller(lift<Unit, V, Unit, V, H> { it.vdrop (n) })



fun <A, H> IPusher<A, H>.ltakeWhile (pred: Function1<A, Boolean>) = Pusher(lift<A, Unit, A, Unit, H> { it.atakeWhile (pred) })
fun <A, H> IPusher<A?, H>.lntakeWhile(pred: Function1<A, Boolean>) = Pusher(lift<A?,Unit, A?,Unit, H> { it.antakeWhile(pred) })
fun <V, H> IPuller<V?, H>.lntakeWhile(pred: Function1<V, Boolean>) = Puller(lift<Unit, V?, Unit, V?, H> { it.vntakeWhile(pred) })
fun <A, H> IPusher<A, H>.ldropWhile (pred: Function1<A, Boolean>) = Pusher(lift<A, Unit, A, Unit, H> { it.adropWhile (pred) })
fun <V, H> IPuller<V, H>.ldropWhile (pred: Function1<V, Boolean>) = Puller(lift<Unit, V, Unit, V, H> { it.vdropWhile (pred) })


 // TODO: tests, examples for all these take and drop versions..


/**
 * This is something similar to rx: Subject (or to Jake Wharton library: RxRelay) - but as always: it is simpler ;-)
 * A relay is a pusher you can attach many pushees. Every time you get a Unit -> Unit function you can use to detach your pushee.
 * The relay itself has a "pushee" property that - when called - forwards given item to all currently attached pushees.
 * TODO SOMEDAY: thread-safe version
 */
@MainThread
class Relay<A>(initcap: Int = 16) : IPusher<A, Function1<Unit, Unit>> {

    private val pushees = ArrayList<Function1<A, Unit>>(initcap)

    val pushee: Function1<A, Unit> = { for(p in pushees) p(it) }

    override fun invoke(p: (A) -> Unit): (Unit) -> Unit {
        pushees.add(p)
        return Remove(p, pushees)
    }

    class Remove<F>(private val f: F, private val fs: MutableCollection<F>): Function1<Unit, Unit> {
        override fun invoke(u: Unit) {
            fs.remove(f)
        }

    }
}


// TODO SOMEDAY: use type alias: Pushee<A>
fun <A> Function1<A, Unit>.reschedule(scheduler: IScheduler): Function1<A, Unit> = { a: A -> scheduler.schedule { this(a) } }
// WARNING: if we use scheduler that can call our function from more than one thread - we usually should FIRST wrap the original function
// in "sync" extension function that synchronizes these calls and assures the happens-before relationship between calls.


fun <A, V> Function1<A, V>.sync(): Function1<A, V> = { a: A -> synchronized(this) { this(a) } }
// it synchronizes calls and assures the happens-before relationship between calls.








// TODO NOW: WARNING: Now we try to just "lift" these reschedule and sync extension functions to pushers...
// TODO NOW: This is suspiciously concise. So it is probably buggy (to good to be correct), so:
// TODO NOW: it needs A LOT of good testing...

/**
 * This operator should work similarly to rx: observeOn. But it does NOT synchronize pushing items,
 * so you almost always want to add an operator "lsync" right after this one.
 */
fun <A, H> IPusher<A, H>.lreschedule(scheduler: IScheduler): IPusher<A, H> = Pusher(lift { it.reschedule(scheduler) })
// WARNING 1: we use lift, so all ICommands (if our Pusher supports ICommands) are just passed to upstream Pusher,
// so for example the Cancel command will just be passed upstream and it will NOT cancel any item already scheduled with provided scheduler
// WARNING 2: We do not implement any backpressure here, so if upstream pusher is too fast - it will overwhelm our scheduler, and
// it can create too many threads (or in general consume to much resources)! (and crash..)

/**
 * This operator makes sure all items are pushed serially and that there is the happens-before relationship between pushes.
 */
fun <A, H> IPusher<A, H>.lsync(): IPusher<A, H> = Pusher(lift { it.sync() })




// TODO SOMEDAY MAYBE: add MyMachines performance test to David Karnok android test app for different reactive solutions:
// https://github.com/akarnokd/AgeraTest2



// TODO LATER: implement a lot of most used stuff from Rx
// (and maybe from Iterables/Iterators/Java8streams/ guava fluent iterators???)
// TODO LATER: first try these: reduce, scan, zip, combineLatest, concat, concatMap, switchMap?, flatMapIterable?, merge, switchOnNext?, split?


// TODO LATER: zrobic ladny zestaw operatorow odpowiadajacych Rx nawet dla tych dla ktorych u nas wystarczy cos banalnego jak skladanie funkcji
// po to zeby byla dokumentacja jak u nas robimy rzeczy Rxowe i testy czy rzeczywiscie tak latwo dziala(robic inline gdzie sie da!)
// oczywiscie gdzie sie da nie robic osobno dla push i dla pull, tylko wspolna implementacje...


// TODO LATER: konwertery miedzy swiatami: MyMachines, RxJava, Iterables, Sequences, itp..
// TODO LATER: wrappery tamtych swiatow do MyMachines - czyli zeby np opakowac rx.Observable zeby spelnial interfejs: IPusher...
// i porzadne testy czy cos takiego moze dzialac... moze wrappery operatorow tez da sie?? wtedy trzebaby odrozniac ktory operator
// odpalamy: czy ten pod spodem (RxJavowy), czy nasz zewnetrzny MyMachinowy...



// UPDATE: Komentarz ponizej jest troche przestarzaly, ale pomyslec jeszcze nad jakas forma podzielenia maszyn na Factory i prostsze IMachine
// (na przyklad zaimplementowany juz Timer to wlasciwie Factory of SingleTimer, tylko na razie implicit..)

// TODO LATER: Zdefiniowac klase: Factory - troche odpowiednik cold observable.I ona bedzie zwracac maszynki ktore sluchaja komend.
// czyli maszynki beda kontrolerami, a nie beda zwracac kontrolerow. Dzieki temu nie bedzie problemu w przypdku synchronicznej komunikacji,
// ze w trakcie subscribowania od razu maszyna dziala i leci z itemami az skonczy i dopiero dostajemy kontrolera. I dopiero mozemy
// jej np przerwac jak ona juz skonczyla... (a moze nigdy nie skonczy..) To nam pozwoli zaimplementowac cos jak Observable.take, ktore
// canceluje maszyne jak juz nie potrzebuje wiecej itemow.
// Acha ta nasza nowa maszynka - jesli bedzie obslugiwala komendy (powiedzmy ICMachine : IMachine) - bedzie dziedziczyla po dwoch! Function1
// czyli bedzie miala dwie! metody invoke. Jedna bioraca funkcje, a druga bioraca komendy - nie powinno byc problemu bo to rozne typy danych.
// Czyli IMachine<T, R> : IPushee<Function1<T, R> (jak juz beda aliasy..)
// Czyli: ICMachine : IMachine, Function1<ICommand, Unit> (a Funct... zmienimy na IPushee jak beda aliasy..)
// I jeszcze chyba IECMachine : IEMachine, Function1<ICommand, Unit> (a Funct... zmienimy na IPushee jak beda aliasy)
// A nawet: IECPusher<T> :  IPushee<IPushee<IEvent<T>>>, IPushee<ICommand> (jak beda aliasy) (jakos tak...)


