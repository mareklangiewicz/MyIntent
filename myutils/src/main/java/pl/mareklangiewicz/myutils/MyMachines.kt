package pl.mareklangiewicz.myutils

import android.os.Handler
import android.support.annotation.MainThread
import java.util.*

/**
 * Created by Marek Langiewicz on 20.02.16.
 */


/**
 * My Machines is a kind of toy functional active/passive library
 *
 * By "active" I mean object that decides itself when to do something (machine)
 * By "passive" I mean object that has public method others can call and it just reacts (function)
 *
 * As as base interface for almost all in here we use kotlin.jvm.functions.Function1<T, R>
 * so we can use oridinary functions, but often we use it as "function objects" that can contain
 * some internal state and can behave differently every time the are called/invoked.
 *
 * We can compose programs in two pretty independent styles:
 *
 * 1. Pullable: with base interfaces: IPullee<T> and IPuller<T> (IPullee<T> is actually just: Function1<Unit, T>)
 *    A minimalist collection of some composable functions and classes where the IPuller is an active side
 *    that decides when to call a IPullee for next item. It is similar to Java 8 Stream (IPullee)
 *    and Collector (IPuller), but simpler.
 *
 * 2. Pushable: with base interfaces: IPushee<T> and IPusher<T> (IPushee<T> is actually just: Function1<T, Unit>)
 *    A minimalist collection of some composable functions and classes where the IPusher is an active side
 *    that decides when to send next item to the IPushee. It is similar to RxJava Observer (IPushee)
 *    and Observable (IPusher), but simpler.
 *
 * We can combine these two styles (pullable and pushable) easily.
 *
 *
 * Two active interfaces: IPusher and IPuller are in fact just special examples of IMachine interface.
 * IPuller<T> = IMachine<Unit, T>; IPusher<T> = IMachine<T, Unit>
 * And the IMachine<T, R> is just a class that calls attached Function1<T, R> when it wants to.
 * The machine (as an active side of composition) decides when and how many times to call attached function.
 *
 * Note: we will sometimes add more restrictions about how our IPullee, IPushee, IPuller, IPusher
 * objects should behave. Such restrictions will extend our base rules (base protocol).
 * One simple example of restriction/extension can be:
 * If we push or pull nullable types - null item usually means the end of the stream.
 *
 *
 * TODO LATER: IMPORTANT: make most functions inline..
 * DETAILS:
 * I had some strange problems with inlining functions so I removed all 'inline', 'reified', 'crossinline', etc. modifiers for now.
 * But it is important to put it back when this library is more mature because inlining can provide a lot of performance
 * especially in this kind of code.
 * Example of copilation error: java.lang.UnsupportedOperationException: Don't render receiver parameters
 * (looks like I should mark receiver function object of my function as crossinline and this is not possible in Kotlin now...)
 */


/**
 * This is the heart of this library. It is an active side of communication.
 * It takes a function and calls it whenever it wants to :-)
 * Usually it returns some other function that potentially can be used to control the behavior of the machine
 * regarding given function. (But I do not define any specific return type here yet. Concrete machines will.)
 * Usage: You give the machine your function, and from now on, you do not call your function manually,
 * but machine does it for you. And machine usually gives you some other function: the controller,
 * that can give you some control on how and when the machine call your original function.
 * What kind of controller you get depends on particular machine implementation.
 * The simplest controller would be just: Function1<Unit, Unit> that just detaches
 * the original function from the machine entirely so it is never called again (like rx: unsubscribe).
 * But potentially we can have machines that return more sophisticated controllers like:
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

class Puller<in R, out H>(private val m: IMachine<Unit, R, H>): IPuller<R, H> { // TODO SOMEDAY: remove this wrapper when we have type aliases
    override fun invoke(f: (Unit) -> R): H {
        return m(f)
    }
}

/** active producer (similar to rx.observable) */
interface IPusher<out T, out H> : IMachine<T, Unit, H> // SOMEDAY: use alias when Kotlin supports type aliases..

class Pusher<out T, out H>(private val m: IMachine<T, Unit, H>): IPusher<T, H> { // TODO SOMEDAY: remove this wrapper when we have type aliases
    override fun invoke(f: (T) -> Unit): H {
        return m(f)
    }
}




/**
 * We can be more like RxJava by using some wrapper for items.
 * We also can easily add more types of events when needed.
 * And different machines can support different subset of events..
 */
interface IEvent<out I>
data class Item<out I>(val item: I) : IEvent<I> // sends next item (similar to rx: onNext)
data class Warning<out I>(val warning: Throwable) : IEvent<I> // sends some warning; does not end communication
data class Error<out I>(val error: Throwable) : IEvent<I> // sends some error; ends communication abnormally (similar to rx: onError)
class Completed<out I> : IEvent<I>

/**
 * We could also just use sealed Event class, to forbid extending it and make sure our operators cover all cases.
 * I like the open soulution above more but... a sealed example would be like this:
 */
//sealed class RxStyleEvent<T> {
//    data class Next<T>(val next: T)
//    data class Error(val error: Throwable)
//    object Completed
//}



// TODO LATER: change IEvent protocol to be compatibile with more general/basic "null-ends-stream" protocol:
// Error event should NOT end communication automatically - always send a null value after error to end the stream.
// Completed event should be removed (a null value without preceding error will be used instead)
// This solution have flaws, but it is much better because we can use all code that just understand
// nullable streams as it is. This way a lot of code doesn't have to know/care about IEvent type structure.
// It will just work for any (nullable) type.
// I guess the main disadventage of this solution is network related - what if we loose connection after
// sending Error item. We will not have a chance to send terminating "null" value at all.
// But: in such cases we can write simple wrappers/conwerters that box error and null values in one message
// and unbox it on the oter side (back to two separate items).



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
// user can add more commands for his machines







// Our base interfaces using IEvent and ICommand (with prefix E):

//alias IEPullee<out R> = IPullee<IEvent<R>> // SOMEDAY: use alias when Kotlin supports type aliases..


//alias IEPushee<in T> = IPushee<IEvent<T>> // SOMEDAY: use alias when Kotlin supports type aliases..



//alias IEPuller<in R> = IPuller<IEvent<R>, IPushee<ICommand>> // SOMEDAY: use alias when Kotlin supports type aliases..
interface IEPuller<in R> : IPuller<IEvent<R>, Function1<ICommand, Unit>>

class EPuller<in R>(private val p: IPuller<IEvent<R>, Function1<ICommand, Unit>>): IEPuller<R> { // SOMEDAY: remove when we have type aliases
    override fun invoke(f: (Unit) -> IEvent<R>): (ICommand) -> Unit {
        return p(f)
    }
}

//alias IEPusher<out T> = IPusher<IEvent<T>, IPushee<ICommand>> // SOMEDAY: use alias when Kotlin supports type aliases..
interface IEPusher<out T> : IPusher<IEvent<T>, Function1<ICommand, Unit>>

class EPusher<out T>(private val p: IPusher<IEvent<T>, Function1<ICommand, Unit>>): IEPusher<T> { // SOMEDAY: remove when we have type aliases
    override fun invoke(f: (IEvent<T>) -> Unit): (ICommand) -> Unit {
        return p(f)
    }
}


// NPullee means Pullee that emits null when the stream has ended.

fun <R> Iterator<R>.asNPullee() = { u: Unit -> if(hasNext()) next() else null }
fun <R> Iterable<R>.asNPullee() = iterator().asNPullee()
fun <R> Sequence<R>.asNPullee() = iterator().asNPullee()

fun <R> nPulleeOf(vararg ts: R) = ts.iterator().asNPullee()

// Examples how to create simple Pullee:
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


//fun <R> Iterator<R>.asEPullee(): IEPullee<R> = { if(hasNext()) Item(next()) else Completed<R>() } // SOMEDAY: enable when we have aliases
fun <R> Iterator<R>.asEPullee(): Function1<Unit, IEvent<R>> = { if(hasNext()) Item(next()) else Completed<R>() }

fun <R> Iterable<R>.asEPullee() = iterator().asEPullee()

fun <R> Sequence<R>.asEPullee() = iterator().asEPullee()

fun <R> ePulleeOf(vararg ts: R) = ts.iterator().asEPullee()

// Examples how to create simple EPullee:
// (1..50).asEPullee()
// listOf("bla", "ble").asEPullee()
// ePulleeOf('a', 'b', 'c')


/**
 * This allows to use for in loop to traverse through EPullee.
 * WARNING: iterating destroys IEPullee so you can do it only once!
 * TODO: tests and examples
 */
//fun <R> IEPullee<R>.eiter() = object : Iterable<R> { // SOMEDAY: enable when we have aliases
fun <R> Function1<Unit, IEvent<R>>.eiter() = object : Iterable<R> {
    override operator fun iterator() = object : Iterator<R> {

        var current = invoke(Unit)

        override fun hasNext(): Boolean {
            return current !is Completed
        }

        override fun next(): R {
            val c = current
            current = invoke(Unit)
            when (c) {
                is Item -> return c.item
                is Error -> throw c.error
                is Completed -> throw IllegalStateException("No more items available.")
                else -> throw IllegalStateException("Unsupported event.")
            }
        }
    }
}





/**
 * Pushes a Long number every time the Step command is invoked but only when pusher is started.
 * Steps are ignored when pusher is stopped. By default it pushes 0 value once for every step,
 * but you can provide explicit 'step' constructor parameter to change it to more.
 * It pushes a Long value that counts invocations in every step (starting from 0 for every step)
 * UPDATE: It supports Tick and Tock commands too (just like Step)
 */
class StepPusher( val step: Long = 1, val tick: Long = 1, val tock: Long = 1 ) : IPusher<Long, Function1<ICommand, Unit>> {

    protected class Pusher(
            private var function: Function1<Long, Unit>?,
            val step: Long = 1,
            val tick: Long = 1,
            val tock: Long = 1
    ) : Function1<ICommand, Unit> {

        protected var started = false

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
                else -> throw IllegalArgumentException("Unsupported command.")
            }
        }
    }

    override fun invoke(f: Function1<Long, Unit>): Function1<ICommand, Unit> {
        return Pusher(f, step, tick, tock)
    }
}


/**
 * Takes an IEPullee of intervals in ms. Interval 0 means immediately.
 * It should be subscribed only once.
 * It supports commands:
 * - Start;
 * - Stop (the same as Pause - stops but can be started again);
 * - Cancel (unsubscribes);
 * - Step (the same as Tick and Tock - just invokes function once)
 * Timer pushes a Long numbers counting from 0...
 */
@MainThread
class Timer(private val handler: Handler, private val intervals: Function1<Unit, IEvent<Long>>) : IPusher<Long, Function1<ICommand, Unit>> {

    private var counter = 0L

    private var function: Function1<Long, Unit>? = null

    override fun invoke(f: Function1<Long, Unit>): Function1<ICommand, Unit> {

        function = f

        return object : Function1<ICommand, Unit> {

            private val runnable = Runnable {
                if(function !== null) {
                    function?.invoke(counter++)
                    invoke(Start)
                }
            }

            override tailrec fun invoke(cmd: ICommand) {

                when(cmd) {

                    Step, Tick, Tock -> function?.invoke(counter++)

                    Stop, Pause -> handler.removeCallbacks(runnable)

                    Cancel -> {
                        handler.removeCallbacks(runnable)
                        function = null
                    }

                    Start -> {
                        val interval = intervals(Unit)
                        when(interval) {
                            is Completed -> return invoke(Cancel)
                            is Error -> throw interval.error
                            is Item -> {
                                if(interval.item == 0L) {
                                    function?.invoke(counter++)
                                    return invoke(Start)
                                }

                                if(interval.item < 0L)
                                    throw IllegalArgumentException("Pulled time interval is negative: $interval")

                                handler.postDelayed(runnable, interval.item)
                            }
                        }
                    }
                }
            }
        }
    }
}






/** maps argument before giving it to receiver function - actually just a function composition */
fun <P, R, Q> Function1<P, R>.amap(f: Function1<Q, P>): Function1<Q, R> = f * this

/** maps result value from function (similar to Sequence.map) - actually just a function coposition */
fun <P, R, S> Function1<P, R>.vmap(f: Function1<R, S>): Function1<P, S> = this * f


/** forwards null values down the stream */
fun <T, R> n(f: Function1<T, R>): Function1<T?, R?> = { if(it === null) null else f(it) }

/** maps items and forwards other events */
fun <T, R> e(f: Function1<T, R>): Function1<IEvent<T>, IEvent<R>> = {
    when (it) {
        is Item -> Item(f(it.item))
        is Warning -> Warning(it.warning)
        is Error -> Error(it.error)
        is Completed -> Completed()
        else -> throw UnsupportedOperationException("Unknown IEvent.")
    }
}


fun <P, R, Q> Function1<P?, R>.anmap(f: Function1<Q, P>): Function1<Q?, R> = n(f) * this
fun <P, R, S> Function1<P, R?>.vnmap(f: Function1<R, S>): Function1<P, S?> = this * n(f)

fun <P, R, U> Function1<IEvent<P>, U>.aemap(f: Function1<R, P>): Function1<IEvent<R>, U> = (e(f) * this)
fun <P, R, U> Function1<U, IEvent<P>>.vemap(f: Function1<P, R>): Function1<U, IEvent<R>> = (this * e(f))



/** allows to implement some side effect every time function is called with some argument */
fun <A, B> Function1<A, B>.apeek(spy: Function1<A, Unit>): Function1<A, B> = { spy(it); this(it) }

/** allows to implement some side effect every time function returns some value (similar to rx: doOnNext but pull based) */
fun <A, B> Function1<A, B>.vpeek(spy: Function1<B, Unit>): Function1<A, B> = this % spy



/** forwards given items to IPushee, but only if they meet given predicate */
fun <A> Function1<A, Unit>.afilter(pred: Function1<A, Boolean>): Function1<A, Unit> = { if(pred(it)) this(it) }

/**
 * Something like Sequence.filter - warning: it will block until next matching item is found. Usually nfilter or efilter is better.
 */
fun <V> Function1<Unit, V>.vfilter(pred: Function1<V, Boolean>): Function1<Unit, V> = object : Function1<Unit, V> {
    override fun invoke(u: Unit): V {
        var b = this@vfilter(Unit)
        while(!pred(b))
            b = this@vfilter(Unit)
        return b
    }
}

/**
 * Something like Sequence.filter, where null means end of sequence. warning: it will block until it finds next maching item or null
 */
fun <V> Function1<Unit, V?>.vnfilter(pred: Function1<V, Boolean>): Function1<Unit, V?> = object : Function1<Unit, V?> {

    var end = false

    override fun invoke(u: Unit): V? {

        if(end)
            return null

        while(true) {
            val b = this@vnfilter(Unit)
            if(b === null) {
                end = true
                return null
            }
            if(pred(b))
                return b
        }
    }
}



/**
 * Something like Sequence.filter for IEvents. warning: it will block until if find next maching item or if EPullee is Completed.
 */
fun <V> Function1<Unit, IEvent<V>>.vefilter(pred: Function1<V, Boolean>): Function1<Unit, IEvent<V>> = object : Function1<Unit, IEvent<V>> {

    var end = false

    override fun invoke(u: Unit): IEvent<V> {

        if(end)
            return Completed()

        while(true) {
            val b = this@vefilter(Unit)
            when(b) {
                is Completed -> {
                    end = true
                    return Completed()
                }
                is Item -> {
                    if(pred(b.item))
                        return b
                    // if not -> we allow 'while' loop go try again
                }
                else -> return b
            }

        }

    }
}




/** pushes forward only specified number of items */
fun <A> Function1<A, Unit>.atake(n: Long): Function1<A, Unit> = object : Function1<A, Unit> {
    var count = n
    override fun invoke(a: A) = if(count-- > 0L) this@atake(a) else Unit
}

/** pushes forward only specified number of items, then one null value, and then nothing else */
fun <A> Function1<A?, Unit>.antake(n: Long): Function1<A?, Unit> = object : Function1<A?, Unit> {
    var count = n
    override fun invoke(a: A?) { // if t is null it is ok too (because our protocol says it can happen only once)
        if(count > 0L)
            this@antake(a)
        else if(count == 0L)
            this@antake(null)
        count --
    }
}

/** pushes forward only specified number of events, then one Completed() event, and then nothing else */
fun <A> Function1<IEvent<A>, Unit>.aetake(n: Long): Function1<IEvent<A>, Unit> = object : Function1<IEvent<A>, Unit> {
    var count = n
    override fun invoke(e: IEvent<A>) { // see comment in ntake above (situation here is similar)
        if(count > 0L)
            this@aetake(e)
        else if(count == 0L)
            this@aetake(Completed())
        count --
    }
}
/** pulls only specified number of items, then returns null values (similar to Kotlin: Sequence.take) */
fun <V> Function1<Unit, V?>.vntake(n: Long): Function1<Unit, V?> = object : Function1<Unit, V?> {
    var count = n
    override fun invoke(u: Unit): V? = if(count-- > 0) this@vntake(Unit) else null
}

/** pulls only specified number of events, then returns Completed() events (similar to Kotlin: Sequence.take) */
fun <V> Function1<Unit, IEvent<V>>.vetake(n: Long): Function1<Unit, IEvent<V>> = object : Function1<Unit, IEvent<V>> {
    var count = n
    override fun invoke(u: Unit): IEvent<V> = if(count-- > 0) this@vetake(Unit) else Completed()
}





/** start pushing forward after specified number of (dropped) items */
fun <T> Function1<T, Unit>.adrop(n: Long): Function1<T, Unit> = object : Function1<T, Unit> {
    var count = n
    override fun invoke(t: T) = if(count-- <= 0) this@adrop(t) else Unit
}

/** drops first n items immediately when first item is pulled, then forwards items as they are */
fun <T> Function1<Unit, T>.vdrop(n: Long): Function1<Unit, T> = object : Function1<Unit, T> {
    var count = n
    override fun invoke(u: Unit): T {
        while (count-- > 0) this@vdrop(Unit)
        return this@vdrop(Unit)
    }
}


/** forwards items as long as specified predicate holds */
fun <T> Function1<T, Unit>.atakeWhile(pred: Function1<T, Boolean>): Function1<T, Unit> = object : Function1<T, Unit> {
    var end = false
    override fun invoke(t: T) {
        if(end)
            return
        if(pred(t))
            return this@atakeWhile(t)
        end = true
    }
}

/** forwards items as long as specified predicate holds, then forwards null item, then nothing more */
fun <T> Function1<T?, Unit>.antakeWhile(pred: Function1<T, Boolean>): Function1<T?, Unit> = object : Function1<T?, Unit> {
    var end = false
    override fun invoke(t: T?) {
        if(end)
            return
        if(t !== null && pred(t))
            return this@antakeWhile(t)
        end = true
        this@antakeWhile(null)
    }
}

/** forwards items as long as specified predicate holds, then forwards null items */
fun <T> Function1<Unit, T?>.vntakeWhile(pred: Function1<T, Boolean>): Function1<Unit, T?> = object : Function1<Unit, T?> {
    var end = false
    override fun invoke(u: Unit): T? {
        if(end)
            return null
        val t = this@vntakeWhile(Unit)
        if(t !== null && pred(t))
            return t
        end = true
        return null
    }
}





/** drops items as long as specified predicate holds, then forwards all next items as they are */
fun <T> Function1<T, Unit>.adropWhile(pred: Function1<T, Boolean>): Function1<T, Unit> = object : Function1<T, Unit> {
    var found = false
    override fun invoke(t: T) {
        if(found)
            return this@adropWhile(t)
        if(pred(t))
            return
        found = true
        return this@adropWhile(t)
    }
}

/** drops items as long as specified predicate holds in loop on first call, then forwards next items as they are one by one */
fun <T> Function1<Unit, T>.vdropWhile(pred: Function1<T, Boolean>): Function1<Unit, T> = object : Function1<Unit, T> {
    var found = false
    override fun invoke(u: Unit): T {
        var t = this@vdropWhile(Unit)
        if(found)
            return t
        while(true) {
            if(!pred(t)) {
                found = true
                return t
            }
            t = this@vdropWhile(Unit)
        }
    }
}


fun <P, Q, R> Function1<Pair<P, Q>, R>.azip(pullee: Function1<Unit, Q>): Function1<P, R> = { this(it to pullee(Unit)) }

fun <P, R, S> Function1<P, R>.vzip(pullee: Function1<Unit, S>): Function1<P, Pair<R, S>> = { this(it) to pullee(Unit) }

// TODO anzip

fun <P, R, S> Function1<P, R?>.vnzip(pullee: Function1<Unit, S?>): Function1<P, Pair<R, S>?> = {
    val x = this(it)
    val y = pullee(Unit)
    if(x === null || y === null)
        null
    else
        x to y
}

// TODO aezip, vezip
// TODO tests for all zips

// TODO lzips...




/** TODO LATER: review it; documentation; about that null finishes stream; implement some tests */
fun <T> Function1<Unit, Function1<Unit, T?>?>.vnflat(): Function1<Unit, T?> = object : Function1<Unit, T?> {
    var provider: Function1<Unit, T?>? = null
    var end = false
    override tailrec fun invoke(unit: Unit): T? {
        if(end) return null
        if(provider === null)
            provider = this@vnflat(Unit) // ask for next provider
        if(provider === null) {
            end = true
            return null
        }
        val t = provider?.invoke(Unit)
        if(t !== null)
            return t
        provider = null
        return invoke(Unit)
    }
}

// TODO LATER veflat, then anflat and aeflat for pushers




// This is similar to lift in RxJava, but works not only for IPushers (IMachine<T, Unit>) but all IMachines (IPullers too)
fun <T, R, X, Y, H> IMachine<T, R, H>.lift(oper: (Function1<X, Y>) -> Function1<T, R>) = object : IMachine<X, Y, H>  {

    override fun invoke(f: (X) -> Y): H {
        return this@lift(oper(f))
    }
}
// TODO: tests, examples





/**
 * Adds a mapping to pushed items of IMachine. Returned values (if any) are left as they are.
 * similar to rx: Observable.map, but it works not only for IPushers, but for all IMachines.
 */
fun <P, Q, R, H> IMachine<P, Q, H>.lamap(f: Function1<P, R>): IMachine<R, Q, H> = lift { f * it }
// TODO: tests, examples, check in practice if we even need to define it.. (probably yes: to avoid 'functional headache')

/**
 * Adds a mapping to returned values to IMachine. Arguments are not changed.
 */
fun <P, Q, R, H> IMachine<P, Q, H>.lvmap(f: Function1<R, Q>): IMachine<P, R, H> = lift { it * f }
// TODO: tests, examples, check in practice if we even need to define it.. (probably yes: to avoid 'functional headache')


fun <P, R, H> IPusher<P, H>.lmap(f: Function1<P, R>): IPusher<R, H> = Pusher(lamap(f))
fun <Q, R, H> IPuller<Q, H>.lmap(f: Function1<R, Q>): IPuller<R, H> = Puller(lvmap(f))


fun <P, Q, R, H> IMachine<P?, Q, H>.lanmap(f: Function1<P, R>): IMachine<R?, Q, H> = lamap(n(f))
fun <P, Q, R, H> IMachine<P, Q?, H>.lvnmap(f: Function1<R, Q>): IMachine<P?, R, H> = lvmap(n(f))

fun <P, R, H> IPusher<P?, H>.lnmap(f: Function1<P, R>): IPusher<R?, H> = lmap(n(f))
fun <P, R, H> IPuller<P?, H>.lnmap(f: Function1<R, P>): IPuller<R?, H> = lmap(n(f))

fun <P, R> IEPusher<P>.lemap(f: Function1<P, R>): IEPusher<R> = EPusher(lmap(e(f)))
fun <P, R> IEPuller<P>.lemap(f: Function1<R, P>): IEPuller<R> = EPuller(lmap(e(f)))



fun <P, R, H> IMachine<P, R, H>.lapeek(spy: Function1<P, Unit>): IMachine<P, R, H> = lift { f -> { spy(it); f(it) } }
// TODO: tests, examples, check in practice if we even need to define it.. (probably yes: to avoid 'functional headache')

fun <P, R, H> IMachine<P, R, H>.lvpeek(spy: Function1<R, Unit>): IMachine<P, R, H> = lift { it % spy }
// TODO: tests, examples, check in practice if we even need to define it.. (probably yes: to avoid 'functional headache')


fun <P, H> IPusher<P, H>.lpeek(spy: Function1<P, Unit>): IPusher<P, H> = Pusher(lapeek(spy))
fun <R, H> IPuller<R, H>.lpeek(spy: Function1<R, Unit>): IPuller<R, H> = Puller(lvpeek(spy))



fun <T, H> IPusher<T, H>.lfilter(pred: Function1<T, Boolean>): IPusher<T, H>
        = Pusher(lift<T, Unit, T, Unit, H> { it.afilter(pred) }) // TODO: tests, examples



fun <T, H> IPusher<T , H>.ltake (n: Long) = Pusher(lift<T, Unit, T, Unit, H> { it.atake (n) })
fun <T, H> IPusher<T?, H>.lntake(n: Long) = Pusher(lift<T?,Unit, T?,Unit, H> { it.antake(n) })
fun <R, H> IPuller<R?, H>.lntake(n: Long) = Puller(lift<Unit,R?, Unit,R?, H> { it.vntake(n) })
fun <T, H> IPusher<T , H>.ldrop (n: Long) = Pusher(lift<T, Unit, T, Unit, H> { it.adrop (n) })
fun <R, H> IPuller<R , H>.ldrop (n: Long) = Puller(lift<Unit, R, Unit, R, H> { it.vdrop (n) })



fun <T, H> IPusher<T , H>.ltakeWhile (pred: Function1<T, Boolean>) = Pusher(lift<T, Unit, T, Unit, H> { it.atakeWhile (pred) })
fun <T, H> IPusher<T?, H>.lntakeWhile(pred: Function1<T, Boolean>) = Pusher(lift<T?,Unit, T?,Unit, H> { it.antakeWhile(pred) })
fun <R, H> IPuller<R?, H>.lntakeWhile(pred: Function1<R, Boolean>) = Puller(lift<Unit,R?, Unit,R?, H> { it.vntakeWhile(pred) })
fun <T, H> IPusher<T , H>.ldropWhile (pred: Function1<T, Boolean>) = Pusher(lift<T, Unit, T, Unit, H> { it.adropWhile (pred) })
fun <R, H> IPuller<R , H>.ldropWhile (pred: Function1<R, Boolean>) = Puller(lift<Unit, R, Unit, R, H> { it.vdropWhile (pred) })


 // TODO: tests, examples for all these take and drop versions..


/**
 * This is something similar to rx: Subject (or to Jake Wharton library: RxRelay) - but as always: it is simpler ;-)
 * A realy is a pusher you can attach many pushees. Every time you get a Unit -> Unit function you can use to detach your pushee.
 * The relay itself has a "pushee" property that - when called - forwards given item to all currently attached pushees.
 * TODO SOMEDAY: thread-safe version
 */
@MainThread
class Relay<T>(initcap: Int = 16) : IPusher<T, Function1<Unit, Unit>> {

    private val pushees = ArrayList<Function1<T, Unit>>(initcap)

    val pushee: Function1<T, Unit> = { for(p in pushees) p(it) }

    override fun invoke(p: (T) -> Unit): (Unit) -> Unit {
        pushees.add(p)
        return Remove(p, pushees)
    }

    class Remove<F>(private val f: F, private val fs: MutableCollection<F>): Function1<Unit, Unit> {
        override fun invoke(u: Unit) {
            fs.remove(f)
        }

    }
}





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
