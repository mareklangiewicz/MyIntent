package pl.mareklangiewicz.myutils

import java.util.*

/**
 * Created by Marek Langiewicz on 18.02.16.
 */

interface ISize { val size: Int }

interface IMyStack<T> : IPush<T>, IPull<T?> // I don't require the "size" property in all stacks on purpose.
interface IMyQueue<T> : IPush<T>, IPull<T?> // I don't require the "size" property in all queues on purpose.

interface IMyDeque<T> {
    val head: IMyStack<T>
    val tail: IMyStack<T>
}



// IMPORTANT: methods behavior for indicies: idx < 0 || idx >= size   is UNDEFINED here!
// Subclasses may define for example negative indicies count backwards from the end,
// but we do NOT promise anything like that here in IMyArray.
// Any contract enchantments should be documented in particular implementation.
interface IMyArray<T> : Iterable<T>, ISize {

    operator fun get(idx: Int): T
    operator fun set(idx: Int, t: T): Unit = throw UnsupportedOperationException()

    fun ins(idx: Int, t: T): Unit = throw UnsupportedOperationException()
    fun del(idx: Int): T = throw UnsupportedOperationException()

    override val size: Int

    override operator fun iterator(): Iterator<T> = Itor(this)

    private class Itor<T>(private val array: IMyArray<T>, private var idx: Int = 0) : Iterator<T> {
        override fun hasNext(): Boolean = idx < array.size
        override fun next(): T = array[idx++]
    }
}

internal fun pos(idx: Int, max: Int) = if(idx < 0) max - idx else idx
internal fun Int.chk(min: Int, max: Int) = if(this < min || this > max) throw IndexOutOfBoundsException() else this


/**
 * Something like Python list slicing (start idx is included, stop idx is excluded)
 * Negative start and stop indicies are also interpreted like in Python
 * IMPORTANT: this implementation assumes that underlying arra does not change its size
 * TODO SOMEDAY: third argument: step (also like in Python)
 * TODO LATER: test it!
 */
class MyArraySlice<T>(val array: IMyArray<T>, astart: Int, astop: Int) : IMyArray<T> by array {

    val stop : Int = pos(astop , array.size).chk(0, array.size)
    val start: Int = pos(astart, array.size).chk(0, stop)
    override val size: Int = stop - start

    override fun get(idx: Int      ) = array.get(start + pos(idx, size).chk(0, size-1)   )
    override fun set(idx: Int, t: T) = array.set(start + pos(idx, size).chk(0, size-1), t)
    override fun ins(idx: Int, t: T) = array.ins(start + pos(idx, size).chk(0, size  ), t)
    override fun del(idx: Int      ) = array.del(start + pos(idx, size).chk(0, size-1)   )
}

operator fun <T> IMyArray<T>.get(start: Int, stop: Int) = MyArraySlice(this, start, stop) // TODO LATER: test it



interface IMyBuffer<T> : IMyArray<T>, IPush<T>

interface IClear {
    fun clear()
}


open class MyALBuffer<T>(val capacity: Int = 16) : IMyBuffer<T>, IClear {

    private val array: ArrayList<T> = ArrayList(capacity)

    override fun get(idx: Int): T = array[idx]
    override fun set(idx: Int, t: T) { array[idx] = t }

    override val size: Int get() = array.size

    override val push: (T) -> Unit
        get() = { array.add(it) }

    override fun clear() { array.clear() }
}


open class MyRingBuffer<T>(val capacity: Int = 256) : IMyBuffer<T>, IClear {

    private val array: ArrayList<T> = ArrayList(capacity) // it never grows beyond capacity (never reallocates memory)

    private var startpos: Int = 0


    override fun get(idx: Int): T = array[(startpos + idx) % size ]
    override fun set(idx: Int, t: T) { array[(startpos + idx) % size ] = t }

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

interface IToDo : IMyBuffer<Function1<Unit, Unit>> {

    /**
     * invokes all accumulated tasks and clears the To Do buffer.
     */
    fun doItAll()
}

class ToDo(capacity: Int = 1) : MyALBuffer<Function1<Unit, Unit>>(capacity), IToDo {

    override fun doItAll() {
        for(task in this)
            task(Unit)
        clear()
    }
}


interface IMyList<T> : IMyArray<T>, IMyDeque<T>, IClear


