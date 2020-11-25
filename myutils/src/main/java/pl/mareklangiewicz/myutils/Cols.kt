package pl.mareklangiewicz.myutils

import pl.mareklangiewicz.pue.IPeek
import pl.mareklangiewicz.pue.IPull
import pl.mareklangiewicz.pue.IPush

/**
 * Mini collections library
 *
 * Created by Marek Langiewicz on 18.02.16.
 *
 * Collection names are abbreviations to differ from standard Java and Kotlin collection names.
 */

// NOTE: I had some strange compilation problems related to type variance
// TODO SOMEDAY: read more about mixed site variance:
// http://www.cs.cornell.edu/~ross/publications/mixedsite/mixedsite-tate-fool13.pdf

interface IGet<T> { operator fun get(idx: Int): T }

interface ISet<T> { operator fun set(idx: Int, item: T) }

interface IContains<T> { operator fun contains(item: T): Boolean }

interface ILen { val len: Int }

interface IClr { fun clr() }


/** minimalist stack. pushes and pulls elements from the same side */
interface ISak<T> : IPush<T>, IPull<T?>, IPeek<T?> // I don't require the "len" property in all stacks intentionally.

/** minimalist queue. pushes elements to one side and pulls from the other */
interface IQue<T> : IPush<T>, IPull<T?>, IPeek<T?> // I don't require the "len" property in all queues intentionally.

interface IDeq<T> {
    val head: ISak<T>
    val tail: ISak<T>
}

fun <T> IDeq<T>.rev(): IDeq<T> = object : IDeq<T> {
    override val head = this@rev.tail
    override val tail = this@rev.head
}

fun <T> IDeq<T>.asQue(): IQue<T> = object : IQue<T> {
    override val push = tail.push
    override val pull = head.pull
}


interface ICol<T> : Iterable<T>, ILen, IContains<T>, IClr {
    override fun contains(item: T) = find { it == item } !== null
    override fun clr(): Unit = throw UnsupportedOperationException()
}


// IMPORTANT: methods behavior for indicies: idx < 0 || idx >= size   is UNDEFINED here!
// Subclasses may define for example negative indicies count backwards from the end,
// but we do NOT promise anything like that here in IArr.
// Any contract enchantments should be documented in particular implementation.
interface IArr<T> : ICol<T>, IGet<T>, ISet<T> {

    override operator fun iterator(): Iterator<T> = Itor(this)

    class Itor<out T>(private val arr: IArr<T>, private var idx: Int = 0) : Iterator<T> {
        override fun hasNext(): Boolean = idx < arr.len
        override fun next(): T = arr[idx++]
    }

}

/**
 * Something like Python list slicing (start idx is included, stop idx is excluded)
 * Negative start and stop indicies are also interpreted like in Python
 * IMPORTANT: this implementation assumes that underlying array does not change its size
 * TODO SOMEDAY: third argument: step (also like in Python)
 * TODO LATER: test it!
 */
class ArrCut<T>(val arr: IArr<T>, astart: Int, astop: Int) : IArr<T> by arr {

    val stop : Int = astop.pos(arr.len).chk(0, arr.len)
    val start: Int = astart.pos(arr.len).chk(0, stop)
    override val len: Int = stop - start

    override fun get(idx: Int         ) = arr.get(start + idx.pos(len).chk(0, len-1)      )
    override fun set(idx: Int, item: T) = arr.set(start + idx.pos(len).chk(0, len-1), item)
    override operator fun iterator(): Iterator<T> = IArr.Itor(this) // overridden so it iterates only from start to stop
    override fun contains(item: T) = find { it == item } !== null // overridden so it searches only from start to stop
    override fun clr(): Unit = throw UnsupportedOperationException() // we make sure we do not clear whole backing arr
}

operator fun <T> IArr<T>.get(start: Int, stop: Int) = ArrCut(this, start, stop) // TODO LATER: test it





interface ILst<T> : IArr<T>, IDeq<T> {
    fun ins(idx: Int, item: T)
    fun del(idx: Int): T
    fun add(item: T) = ins(len, item)
    fun mov(src: Int, dst: Int) {
        if(dst == src) return
        val item = del(src)
        ins(if(dst > src) dst-1 else dst, item)
    }

    override fun clr() { // almost always this implementation should be overridden
        for(i in len-1 downTo 0)
            del(i)
    }
}







fun <T> Collection<T>.asCol() = object : ICol<T> {
    override val len: Int get() = this@asCol.size
    override fun iterator() = this@asCol.iterator()
    override fun contains(item: T) = this@asCol.contains(item)
}

fun <T> List<T>.asArr() = object : IArr<T> {
    override fun get(idx: Int) = this@asArr.get(idx)
    override fun set(idx: Int, item: T) { throw UnsupportedOperationException() }
    override val len: Int get() = this@asArr.size
    override fun iterator() = this@asArr.iterator()
    override fun contains(item: T) = this@asArr.contains(item)
}

fun <T> Array<T>.asArr(): IArr<T> = object : IArr<T> {
    override fun get(idx: Int) = this@asArr.get(idx)
    override fun set(idx: Int, item: T) { this@asArr.set(idx, item) }
    override val len: Int get() = this@asArr.size
    override fun iterator() = this@asArr.iterator()
}

fun <T> List<T>.asLst() = object : ILst<T> {
    override fun get(idx: Int) = this@asLst.get(idx)
    override fun set(idx: Int, item: T) { throw UnsupportedOperationException() }
    override fun ins(idx: Int, item: T) { throw UnsupportedOperationException() }
    override fun del(idx: Int): T { throw UnsupportedOperationException() }
    override val len: Int get() = this@asLst.size
    override fun iterator() = this@asLst.iterator()
    override fun contains(item: T) = this@asLst.contains(item)

    override val head = LstHead(this)
    override val tail = LstTail(this)
}


fun <T> MutableCollection<T>.asMCol() = object : ICol<T> {
    override val len: Int get() = this@asMCol.size
    override fun iterator() = this@asMCol.iterator()
    override fun contains(item: T) = this@asMCol.contains(item)
    override fun clr() = this@asMCol.clear()
}


fun <T> MutableList<T>.asMArr() = object : IArr<T> {
    override fun get(idx: Int) = this@asMArr.get(idx)
    override fun set(idx: Int, item: T) { this@asMArr.set(idx, item) }

    override val len: Int get() = this@asMArr.size
    override fun iterator() = this@asMArr.iterator()
    override fun contains(item: T) = this@asMArr.contains(item)
    override fun clr() = this@asMArr.clear()
}

fun <T> MutableList<T>.asMLst() = object : ILst<T> {
    override fun get(idx: Int) = this@asMLst.get(idx)
    override fun set(idx: Int, item: T) { this@asMLst.set(idx, item) }

    override fun ins(idx: Int, item: T) = this@asMLst.add(idx, item)
    override fun del(idx: Int): T = this@asMLst.removeAt(idx)

    override val len: Int get() = this@asMLst.size
    override fun iterator() = this@asMLst.iterator()
    override fun contains(item: T) = this@asMLst.contains(item)
    override fun clr() = this@asMLst.clear()

    override val head = LstHead(this)
    override val tail = LstTail(this)
}

fun Int.pos(size: Int) = if(this < 0) size + this else this
fun Int.chk(min: Int, max: Int) = if(this < min || this > max) throw IndexOutOfBoundsException() else this




