package pl.mareklangiewicz.myutils

import java.util.*

/**
 * Created by Marek Langiewicz on 18.02.16.
 */

interface IMyArray<T> : Iterable<T> {

    operator fun get(idx: Int): T

    val size: Int

    override operator fun iterator(): Iterator<T> = Itor(this)

    private class Itor<T>(private val array: IMyArray<T>, private var idx: Int = 0) : Iterator<T> {
        override fun hasNext(): Boolean = idx < array.size
        override fun next(): T = array[idx++]
    }
}

interface IMyMutableArray<T> : IMyArray<T> {
    operator fun set(idx: Int, t: T)
}


interface IMyBuffer<T> : IMyArray<T>, Function1<T, Unit> // SOMEDAY: use Pushee<T> when we have type aliases

interface IClear {
    fun clear()
}


open class MyALBuffer<T>(val capacity: Int = 16) : IMyBuffer<T>, IClear {

    private val array: ArrayList<T> = ArrayList(capacity)

    override fun get(idx: Int): T = array[idx]

    override val size: Int
        get() = array.size

    override fun invoke(t: T) { array.add(t) }
    override fun clear() { array.clear() }
}


open class MyRingBuffer<T>(val capacity: Int = 256) : IMyBuffer<T>, IClear {

    private val array: ArrayList<T> = ArrayList(capacity)

    private var startpos: Int = 0


    override fun get(idx: Int): T = array[(startpos + idx) % size ]

    override val size: Int
        get() = array.size

    override fun invoke(t: T) {
        if(size < capacity)
            array.add(t)
        else {
            array[startpos] = t
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
     * invokes all accumulated tasks and clears to do buffer.
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


