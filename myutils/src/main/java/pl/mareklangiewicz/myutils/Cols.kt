package pl.mareklangiewicz.myutils

import java.util.*

/**
 * Created by Marek Langiewicz on 18.02.16.
 * My collection names are abbreviations to differ from standard Java and Kotlin collection names.
 */

interface ISize { val size: Int }

interface IClear { fun clear(): Unit = throw UnsupportedOperationException() }


interface ISak<T> : IPush<T>, IPull<T?>, IPeek<T?> // I don't require the "size" property in all stacks intentionally.
interface IQue<T> : IPush<T>, IPull<T?>, IPeek<T?> // I don't require the "size" property in all queues intentionally.

interface IDeq<T> {
    val head: ISak<T>
    val tail: ISak<T>
}


interface ICol<out T> : Iterable<T>, ISize, IClear


// IMPORTANT: methods behavior for indicies: idx < 0 || idx >= size   is UNDEFINED here!
// Subclasses may define for example negative indicies count backwards from the end,
// but we do NOT promise anything like that here in IMyArray.
// Any contract enchantments should be documented in particular implementation.
interface IArr<T> : ICol<T> {

    operator fun get(idx: Int): T
    operator fun set(idx: Int, item: T): Unit = throw UnsupportedOperationException()

    operator fun get(start: Int, stop: Int) = Cut(this, start, stop) // TODO LATER: test it

    override operator fun iterator(): Iterator<T> = Itor(this)

    private class Itor<out T>(private val arr: IArr<T>, private var idx: Int = 0) : Iterator<T> {
        override fun hasNext(): Boolean = idx < arr.size
        override fun next(): T = arr[idx++]
    }

    /**
     * Something like Python list slicing (start idx is included, stop idx is excluded)
     * Negative start and stop indicies are also interpreted like in Python
     * IMPORTANT: this implementation assumes that underlying array does not change its size
     * TODO SOMEDAY: third argument: step (also like in Python)
     * TODO LATER: test it!
     */
    class Cut<T>(val arr: IArr<T>, astart: Int, astop: Int) : IArr<T> by arr {

        val stop : Int = astop.pos(arr.size).chk(0, arr.size)
        val start: Int = astart.pos(arr.size).chk(0, stop)
        override val size: Int = stop - start

        override fun get(idx: Int         ) = arr.get(start + idx.pos(size).chk(0, size-1)      )
        override fun set(idx: Int, item: T) = arr.set(start + idx.pos(size).chk(0, size-1), item)
    }
}

interface ILst<T> : IArr<T>, IDeq<T> {

    fun ins(idx: Int, item: T): Unit = throw UnsupportedOperationException()
    fun del(idx: Int): T = throw UnsupportedOperationException()

    fun mov(src: Int, dst: Int) {
        if(dst == src) return
        val t = del(src)
        ins(if(dst > src) dst-1 else dst, t)
    }

    interface IChange<out T> : IEvent<T>
    data class Insert<out T>(val idx: Int, val item: T) : IChange<T>
    data class Delete<out T>(val idx: Int) : IChange<T>
    data class Modify<out T>(val idx: Int, val item: T) : IChange<T>
    data class Append<out T>(val item: T) : IChange<T>
    data class Move<out T>(val src: Int, val dst: Int) : IChange<T>
    // user can add and support more change types.
    // user should usually sync all data if he gets unknown change type.

    fun asEPushee(ch: IChange<T>) {
        when (ch) {
            is Insert<T> -> ins(ch.idx, ch.item)
            is Delete<T> -> del(ch.idx)
            is Modify<T> -> set(ch.idx, ch.item)
            is Append<T> -> ins(size, ch.item)
            is Move<T> -> mov(ch.src, ch.dst)
            else -> throw ClassCastException("Unsupported arr change: $ch")
        }
    }

    // use it to implement the head property of IDeq
    class Head<T>(val lst: ILst<T>) : ISak<T> {
        override val push: (T) -> Unit = { lst.ins(0, it) }
        override val pull: (Unit) -> T? = { if(lst.size <= 0) null else lst.del(0) }
        override val peek: (Unit) -> T? = { if(lst.size <= 0) null else lst.get(0) }
    }

    // use it to implement the tail property of IDeq
    class Tail<T>(val lst: ILst<T>) : ISak<T> {
        override val push: (T) -> Unit = { lst.ins(lst.size, it) }
        override val pull: (Unit) -> T? = { if(lst.size <= 0) null else lst.del(lst.size-1) }
        override val peek: (Unit) -> T? = { if(lst.size <= 0) null else lst.get(lst.size-1) }
    }
}


fun <T> Collection<T>.asCol() = object : ICol<T> {
    override val size: Int get() = this@asCol.size
    override fun iterator() = this@asCol.iterator()
}

fun <T> List<T>.asArr() = object : IArr<T> {
    override fun get(idx: Int) = this@asArr.get(idx)
    override val size: Int get() = this@asArr.size
    override fun iterator() = this@asArr.iterator()
}

fun <T> Array<T>.asArr() = object : IArr<T> {
    override fun get(idx: Int) = this@asArr.get(idx)
    override fun set(idx: Int, item: T) { this@asArr.set(idx, item) }
    override val size: Int get() = this@asArr.size
    override fun iterator() = this@asArr.iterator()
}

fun <T> List<T>.asLst() = object : ILst<T> {
    override fun get(idx: Int) = this@asLst.get(idx)
    override val size: Int get() = this@asLst.size
    override fun iterator() = this@asLst.iterator()

    override val head = ILst.Head(this)
    override val tail = ILst.Tail(this)
}


fun <T> MutableCollection<T>.asMCol() = object : ICol<T> {
    override val size: Int get() = this@asMCol.size
    override fun iterator() = this@asMCol.iterator()
    override fun clear() = this@asMCol.clear()
}


fun <T> MutableList<T>.asMArr() = object : IArr<T> {
    override fun get(idx: Int) = this@asMArr.get(idx)
    override fun set(idx: Int, item: T) { this@asMArr.set(idx, item) }

    override val size: Int get() = this@asMArr.size
    override fun iterator() = this@asMArr.iterator()
    override fun clear() = this@asMArr.clear()
}

fun <T> MutableList<T>.asMLst() = object : ILst<T> {
    override fun get(idx: Int) = this@asMLst.get(idx)
    override fun set(idx: Int, item: T) { this@asMLst.set(idx, item) }

    override fun ins(idx: Int, item: T) = this@asMLst.add(idx, item)
    override fun del(idx: Int): T = this@asMLst.removeAt(idx)

    override val size: Int get() = this@asMLst.size
    override fun iterator() = this@asMLst.iterator()
    override fun clear() = this@asMLst.clear()

    override val head = ILst.Head(this)
    override val tail = ILst.Tail(this)
}

fun Int.pos(size: Int) = if(this < 0) size + this else this
fun Int.chk(min: Int, max: Int) = if(this < min || this > max) throw IndexOutOfBoundsException() else this







// TODO LATER: remove this - just use ILst
interface IBuf<T> : IArr<T>, IPush<T>


// TODO LATER: remove this - just use Lst
open class MyALBuffer<T>(capacity: Int = 16) : IBuf<T> {

    private val array: ArrayList<T> = ArrayList(capacity)

    override fun get(idx: Int): T = array[idx]
    override fun set(idx: Int, item: T) { array[idx] = item }

    override val size: Int get() = array.size

    override val push: (T) -> Unit
        get() = { array.add(it) }

    override fun clear() { array.clear() }
}


// TODO LATER: remove this - just use Lst (override to never grow but to forget elements from the other side (if full))
open class MyRingBuffer<T>(val capacity: Int = 256) : IBuf<T> {

    private val array: ArrayList<T> = ArrayList(capacity) // it never grows beyond capacity (never reallocates memory)

    private var startpos: Int = 0


    override fun get(idx: Int): T = array[(startpos + idx) % size ]
    override fun set(idx: Int, item: T) { array[(startpos + idx) % size ] = item
    }

    override val size: Int get() = array.size

    override val push: (T) -> Unit
        get() = {
            if(size < capacity)
                array.add(it)
            else {
                array[startpos] = it
                startpos = (startpos + 1) % size
            }
        }

    override fun clear() {
        array.clear()
        startpos = 0
    }
}

fun <A, V> Iterable<Function1<A, V>>.invokeAll(a: A) { for(f in this) f(a) }


// TODO LATER: we do not really need it. we can just use Lst collection (and invokeAll and clear)
interface IToDo : IBuf<Function1<Unit, Unit>> {

    /**
     * invokes all accumulated tasks and clears the To Do buffer.
     */
    fun doItAll()
}


// TODO LATER: we do not really need it. we can just use Lst collection (and invokeAll and clear)
class ToDo(capacity: Int = 1) : MyALBuffer<Function1<Unit, Unit>>(capacity), IToDo {

    override fun doItAll() {
        for(task in this)
            task(Unit)
        clear()
    }
}




