package pl.mareklangiewicz.myutils

import android.os.Handler
import android.support.annotation.MainThread

/**
 * Created by Marek Langiewicz on 20.02.16.
 */


/**
 * My Machines is a kind of toy functional active/reactive library
 *
 * By "active" I mean object that decides itself when to do something (machine)
 * By "reactive" I mean object that has public method others can call and it just "reacts" (function)
 *
 * We can compose programs in two pretty independent styles:
 *
 * 1. Pullable: with base interfaces: IPullee<T> and IPuller<T> (IPullee<T> is actually just: Function1<Unit, T>)
 *    A minimalist collection of some composable functions and classes where the IPuller is an active side
 *    that decides when to call a IPullee for next item. It is similar to Java 8 Stream (IPullee)
 *    and Collector (IPuller); but simpler and not so powerful.
 *
 * 2. Pushable: with base interfaces: IPushee<T> and IPusher<T> (IPushee<T> is actually just: Function1<T, Unit>)
 *    A minimalist collection of some composable functions and classes where the IPusher is an active side
 *    that decides when to send next item to the IPushee. It is similar to RxJava Observer (IPushee)
 *    and Observable (IPusher); but simpler and not so powerful.
 *
 * Two active interfaces: IPusher and IPuller are in fact just special esamples of IMachine interface.
 * IPuller<T> = IMachine<Unit, T>; IPusher<T> = IMachine<T, Unit>
 * And the IMachine<T, R> is just a class that calls attached Function1<T, R> when it wants to.
 * The machine (as an active side of composition) decides when and how many times to call attached function.
 *
 * Note: our Functions (passive side of composition) usually are not just regular functions
 * (although we can use regular functions and lambdas right away).
 * Usually our Functions are real objects that contain state.
 * We can use any object that implements Function1<T, Unit> as IPushee
 * and any object that implements Function1<Unit, T> as IPullee
 *
 * Note: we will sometimes add more restrictions about how our IPullee, IPushee, IPuller, IPusher
 * objects should behave. The most common restriction will be:
 * If we push or pull nullable types - null item usually means the end of the stream
 * and no more pushing or pulling should be performed. And the IMachine should forget its given function,
 * (or functions - if it supports attaching many functions) so it can be garbage collected.
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


// TODO NOW: dodac gdzie sie da in/out przy zmiennych typowych! i gdzie sie da reified i inline

interface IAttach<in F> {
    fun attach(f: F) {} // similar to rx: subscribe
}

interface IDetach {
    fun detach() {} // similar to rx: unscubscribe
}

interface IStart {
    fun start() {} // similar to rx: ConnectableObservable.connect
}

interface IStop {
    fun stop() {} // similar to rx: ConnectableObservable.connect
}

interface IMachine<out T, in R> : IAttach<Function1<T, R>>, IDetach, IStart, IStop

/** reactive producer (similar to iterator) */
// TODO: type IPullee<T> = Function1<Unit, T> // SOMEDAY: use it when Kotlin supports type aliases..
// for now we will just use: Function1<Unit, T>

/** reactive producer (similar to rx: observer) */
// TODO: type IPushee<T> = Function1<T, Unit> // SOMEDAY: use some type alias when Kotlin supports type aliases..
// for now we will just use: Function1<T, Unit>


/** active consumer (similar to java 8: collector) */
// TODO: type IPuller<in R> = IMachine<Unit, R> // SOMEDAY: use it when Kotlin supports type aliases..
// for now we will just use: IMachine<Unit, R>


/** active producer (similar to rx.observable) */
// TODO: type IPusher<out T> = IMachine<T, Unit> // SOMEDAY: use some type alias when Kotlin supports type aliases..
// for now we will just use: IMachine<T, Unit>


/**
 * Schortcut for starting a pusher with given pushee attached.
 * This function returns its receiver (pusher) to make it easy to save a reference to pusher to stop it later..
 */
fun <T, R> IMachine<T, R>.attrt(f: Function1<T, R>): IMachine<T, R> { attach(f); start(); return this }


/**
 * Schortcut for starting a pusher with empty pushee attached.
 * A default empty pushee is used (it just ignore every given item)
 * This function returns its receiver (pusher) to make it easy to save a reference to pusher to stop it later..
 */
fun <T> IMachine<T, Unit>.attrt(): IMachine<T, Unit> { attach {}; start(); return this }


/** active part of the machine that does not push or pull any data, just unit */
interface IEngine : IMachine<Unit, Unit> // : IPuller<Unit>, IPusher<Unit>
// it can be used as a pusher or a puller, just for without any data (just for Unit items)
// it just initiates when to proces next piece of work




// NPullee means Pullee that emits null when the stream has ended.

fun <T> Iterator<T>.asNPullee(): Function1<Unit, T?> = { if(hasNext()) next() else null }
fun <T> Iterable<T>.asNPullee(): Function1<Unit, T?> = iterator().asNPullee()
fun <T> Sequence<T>.asNPullee(): Function1<Unit, T?> = iterator().asNPullee()

fun <T> NPulleeOf(vararg ts: T) = ts.iterator().asNPullee()

// Examples how to create simple Pullee:
// (1..50).toPullee()
// listOf("bla", "ble").toPullee()
// NPulleeOf('a', 'b', 'c')


/**
 * An engine that calls its function in loop immediately when start() is called - until stop() is called
 * TODO: make it threadsafe? (what actions are atomic in Kotlin??)
 */
class LoopEngine : IEngine {

    private var started = false

    private var function: Function1<Unit, Unit>? = null

    override fun attach(f: Function1<Unit, Unit>) {
        if(function !== null)
            throw IllegalStateException("The engine has some function attached already.")
        function = f
    }

    override fun detach() {
        if(started)
            throw IllegalStateException("You have to stop the engine first.")
        function = null
    }

    override fun stop() {
        started = false
    }

    override fun start() {
        val f = function
        if(f === null) throw IllegalStateException("You have to attach some function to this engine first.")
        if(started) return
        started = true
        while(started) f(Unit)
    }

}

/**
 * Takes an NPullee of intervals in ms. Interval 0 means immediately.
 * TODO NOW: test it!
 * TODO LATER: maybe divide it to base: StepEngine (with additional method: step()) and use it to implement TimeEngine
 */
@MainThread
class TimeEngine(private val handler: Handler, private val intervals: Function1<Unit, Long?>) : IEngine {

    private var started = false

    private var function: Function1<Unit, Unit>? = null

    private val runnable = Runnable {
        function?.invoke(Unit)
        start()
    }

    override fun attach(f: Function1<Unit, Unit>) {
        if(function !== null)
            throw IllegalStateException("The engine has some function attached already.")
        function = f
    }

    override fun detach() {
        if(started)
            throw IllegalStateException("You have to stop the engine first.")
        function = null
    }

    override fun stop() {
        started = false
        handler.removeCallbacks(runnable)
    }

    override tailrec fun start() {
        val f = function
        if(f === null) throw IllegalStateException("You have to attach some function to this engine first.")
        started = true
        val interval = intervals(Unit)
        if (interval === null) {
            stop()
            detach()
            return
        }

        if(interval == 0L) {
            f(Unit)
            return start()
        }

        if(interval < 0L)
            throw IllegalArgumentException("Pulled time interval is negative: $interval")

        handler.postDelayed(runnable, interval)
    }
}



fun <T> NPuller(): IMachine<Unit, T?> = LoopEngine().vmap { t: T? -> Unit }.pullUntil { it === null }

fun <T> npull(pullee: Function1<Unit, T?>) { while(pullee(Unit) !== null); }




/** maps argument before giving it to receiver function - actually just a function composition */
fun <P, R, Q> Function1<P, R>.amap(f: Function1<Q, P>): Function1<Q, R> = f * this

/** maps result value from function (similar to Sequence.map) - actually just a function coposition */
fun <P, R, S> Function1<P, R>.vmap(f: Function1<R, S>): Function1<P, S> = this * f

/** forwards null values down the stream */
fun <T, R> n(f: Function1<T, R>): Function1<T?, R?> = { if(it === null) null else f(it) }

fun <P, R, Q> Function1<P?, R>.anmap(f: Function1<Q, P>): Function1<Q?, R> = n(f) * this
fun <P, R, S> Function1<P, R?>.vnmap(f: Function1<R, S>): Function1<P, S?> = this * n(f)



/** allows to implement some side effect every time function is called with some parameter value */
fun <A, B, U> Function1<A, B>.aeff(f: Function1<A, U>): Function1<A, B> = { f(it); this(it) }

/** allows to implement some side effect every time function returns some value (similar to rx: doOnNext - but pull based) */
fun <A, B, U> Function1<A, B>.veff(f: Function1<B, U>): Function1<A, B> = this % f




/** forwards given items to IPushee, but only if they meet given predicate */
fun <T> Function1<T, Unit>.afilter(pred: Function1<T, Boolean>): Function1<T, Unit> = { if(pred(it)) this(it) }


/**
 * Something like Sequence.filter - warning: it will block until next matching item is found. Usually nfilter is more useful.
 * The U type in here should normally be just Unit. But I leave it unspecified - maybe in will be useful in some strange usecases.
 */
fun <U, V> Function1<U, V>.vfilter(pred: Function1<V, Boolean>): Function1<U, V> = object : Function1<U, V> {
    override fun invoke(u: U): V {
        var b = this@vfilter(u)
        while(!pred(b))
            b = this@vfilter(u)
        return b
    }
}

/**
 * Something like Sequence.filter, where null means end of sequence. warning: it will block until if find next maching item or null
 * The U type in here should normally be just Unit. But I leave it unspecified - maybe in will be useful in some strange usecases.
 */
fun <U, V> Function1<U, V?>.nvfilter(pred: Function1<V, Boolean>): Function1<U, V?> = object : Function1<U, V?> {

    var end = false

    override fun invoke(u: U): V? {

        if(end)
            return null

        while(true) {
            val b = this@nvfilter(u)
            if(b === null) {
                end = true
                return null
            }
            if(pred(b))
                return b
        }

    }
}



/** TODO LATER: review it; documentation; about that null finishes stream; implement some tests */
fun <T> Function1<Unit, Function1<Unit, T?>?>.nflat(): Function1<Unit, T?> = object : Function1<Unit, T?> {
    var provider: Function1<Unit, T?>? = null
    var end: Boolean = false
    override tailrec fun invoke(unit: Unit): T? {
        if(end) return null
        if(provider === null)
            provider = this@nflat(Unit) // ask for next provider
        if(provider === null) {
            end = true
            return null
        }
        val t = provider!!(Unit)
        if(t !== null)
            return t
        provider = null
        return invoke(Unit)
    }
}










// This is similar to lift in RxJava, but works not only for IPushers (IMachine<T, Unit>) but all IMachines.
fun <T, R, X, Y> IMachine<T, R>.lift(oper: (Function1<X, Y>) -> Function1<T, R>) = object : IMachine<X, Y>  {

    override fun attach(f: Function1<X, Y>) {
        return this@lift.attach(oper(f))
    }

    override fun detach() {
        this@lift.detach()
    }

    override fun start() {
        this@lift.start()
    }

    override fun stop() {
        this@lift.stop()
    }
}
// TODO: tests, examples





/**
 * Adds a mapping to pushed items of IMachine. Returned values (if any) are left as they are.
 * similar to rx: Observable.map, but it works not only for IPushers, but for all IMachines.
 */
fun <A, B, C> IMachine<A, B>.amap(f: Function1<A, C>): IMachine<C, B> = lift { it.amap(f) }
// TODO: tests, examples, check in practice if we even need to define it.. (probably yes: to avoid 'functional headache')

/**
 * Adds a mapping to returned values to IMachine. Pushed values are not changed.
 */
fun <A, B, C> IMachine<A, B>.vmap(f: Function1<C, B>): IMachine<A, C> = lift { it.vmap(f) }
// TODO: tests, examples, check in practice if we even need to define it.. (probably yes: to avoid 'functional headache')

fun <A, B, C> IMachine<A?, B>.anmap(f: Function1<A, C>): IMachine<C?, B> = amap(n(f))
fun <A, B, C> IMachine<A, B?>.vnmap(f: Function1<C, B>): IMachine<A, C?> = vmap(n(f))


fun <A, B, U> IMachine<A, B>.aeff(f: Function1<A, U>): IMachine<A, B> = lift { it.aeff(f) }
// TODO: tests, examples, check in practice if we even need to define it.. (probably yes: to avoid 'functional headache')

fun <A, B, U> IMachine<A, B>.veff(f: Function1<B, U>): IMachine<A, B> = lift { it.veff(f) }
// TODO: tests, examples, check in practice if we even need to define it.. (probably yes: to avoid 'functional headache')



fun <T> IMachine<T, Unit>.afilter(pred: Function1<T, Boolean>) = lift<T, Unit, T, Unit> { it.afilter(pred) }
// TODO: tests, examples, check in practice if we even need to define it.. (probably yes: to avoid 'functional headache')


/**
 * Similar to rx: Observable.take(int)
 * Pushes only count given elements from underlying machine;
 * then stops the machine and detaches it.
 * important: there is no special item sent to mark the end of the stream.
 * TODO NOW: implement: skip
 */
fun <T> IMachine<T, Unit>.limit(count: Int) = object : IMachine<T, Unit> by this {
    var counter = count
    override fun attach(f: (T) -> Unit) {
        this@limit.attach {
            if(counter <= 0) {
                stop()
                detach()
            }
            else {
                f(it)
                counter --
            }
        }
    }
}


/**
 * Similar to rx: Observable.take(int)
 * Pushes only count given elements from underlying machine;
 * then it pushes a null element, stops the machine and detaches it.
 */
fun <T> IMachine<T?, Unit>.nlimit(count: Int) = object : IMachine<T?, Unit> by this {
    var counter = count
    override fun attach(f: (T?) -> Unit) {
        this@nlimit.attach {
            if(counter <= 0 || it === null) {
                counter = 0
                stop()
                detach()
                f(null) // we push null as the last item
            }
            else {
                f(it)
                counter --
            }
        }
    }
}

/**
 * Similar to rx: Observable.takeWhile
 * Forwards pushed items while they satisfy given predicate.
 * Then it stops and detaches the machine.
 * important: there is no special item sent to mark the end of the stream.
 */
fun <T> IMachine<T, Unit>.pushWhile(pred: Function1<T, Boolean>)
        = lift { f: Function1<T, Unit> -> { if(pred(it)) f(it) else { stop(); detach() } } }

/**
 * Similar to rx: Observable.takeUntil
 * Forwards pushed items until item with pred(item) === true.
 * Then it still pushes this last item and stops and detaches the machine.
 */
fun <T> IMachine<T, Unit>.pushUntil(pred: Function1<T, Boolean>)
        = lift { f: Function1<T, Unit> -> { f(it); if(pred(it)) { stop(); detach() } } }

/**
 * Something like pushUntil, but for pullers.
 */
fun <T> IMachine<Unit, T>.pullUntil(pred: Function1<T, Boolean>)
        = lift { f: Function1<Unit, T> -> { val t = f(it); if(pred(t)) { stop(); detach() }; t } }






fun <T> Iterator<T>.asLoopNPusher(): IMachine<T?, Unit> {
    return LoopEngine()
            .amap(this.asNPullee())
            .pushUntil { it === null } // first null is pushed through so the end of the stream can be detected.

}


fun <T> Iterable<T>.asLoopNPusher(): IMachine<T?, Unit> = iterator().asLoopNPusher()
fun <T> Sequence<T>.asLoopNPusher(): IMachine<T?, Unit> = asIterable().asLoopNPusher()










// TODO NOW: to wywalic - pokazac w testach jak takie rzeczy sie robi uzywajac IEngine
fun <T, R> Function1<T, R>.callUntil(t: T, pred: (R) -> Boolean) { while (!pred(this(t))); }
fun <T, R> Function1<T, R>.callTimes(t: T, n: Int) { (1..n).forEach { this(t) } }



// TODO LATER: implement a lot of most used stuff from Rx
// (and maybe from Iterables/Iterators/Java8streams/ guava fluent iterators???)
// TODO NOW: first try these: skip!!!, reduce, scan, zip, combineLatest, concat, concatMap, switchMap?, flatMapIterable?, merge, switchOnNext?, split?

// TODO LATER: try to separate the time related stuff as a final Pusher<Unit>/Puller<Unit> UPDATE: As IEngine!
// (so we consume items in the middle of the chain, and active element at the end just launches the whole composition when it wants)



// TODO LATER: zrobic ladny zestaw operatorow odpowiadajacych Rx nawet dla tych dla ktorych u nas wystarczy cos banalnego jak skladanie funkcji
// po to zeby byla dokumentacja jak u nas robimy rzeczy Rxowe i testy czy rzeczywiscie tak latwo dziala(robic inline gdzie sie da!)
// robic wersje z konczacym nullem z przedrostkiem n na przyklad: nmap i z eventami z przedrostkiem e na przyklad emap
// oczywiscie gdzie sie da nie robic osobno dla push i dla pull, tylko wspolna implementacje...


// TODO LATER: konwertery miedzy swiatami: MyCallers, RxJava, Iterables, Sequences, itp..
// TODO LATER: wrappery tamtych swiatow do MyCallers - czyli zeby np opakowac rx.Observable zeby spelnial interfejs: IPusher...
// i porzadne testy czy cos takiego moze dzialac... moze wrappery operatorow tez da sie?? wtedy trzebaby odrozniac ktory operator
// odpalamy: czy ten pod spodem (RxJavowy), czy nasz zewnetrzny MyCallerowy...


/**
 * We can emulate RxJava protocol using some wrapper for items.
 * We also can easily add more types of events when needed.
 * And different machines can support different subset of events..
 */
interface IEvent

class Tick : IEvent // an event without any data

class Start : IEvent // sent when machine starts
// (on pull based communication this could be send by Pullee as first item - usually not needed..)

class Stop : IEvent // sent when machine stops (similar to rx: onCompleted)
// (on pull based communication this can inform than no more items are available)

data class Item<I>(val item: I) : IEvent // sends next item (similar to rx: onNext)

data class Warning<W>(val warning: W) : IEvent // sends some warning; does not end communication

data class Error<E>(val error: E) : IEvent // sends some error; does not end communication

data class Fail<F>(val fail: F) : IEvent // sends some fatal error; ends communication abnormally (similar to rx: onError)




/**
 * We could also just use sealed Event class, to forbid extending it and make sure our operators cover all cases.
 * I like the open soulution above more but... a sealed example would be like this:
 */
//sealed class RxStyleEvent<T> {
//    data class Next<T>(val next: T)
//    data class Error(val error: Throwable)
//    class Completed
//}

