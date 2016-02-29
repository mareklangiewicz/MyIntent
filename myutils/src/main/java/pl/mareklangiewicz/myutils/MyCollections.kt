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

interface IMyConsumer<in T> {
    fun accept(t: T)
}

class MyMultiConsumer<in T>(vararg val consumers: IMyConsumer<T>) : IMyConsumer<T> {
    override fun accept(t: T) {
        consumers.forEach { it.accept(t) }
    }
}


class MyFilteredConsumer<T>(private val consumer: IMyConsumer<T>,private val filter: (T) -> Boolean) : IMyConsumer<T> {
    override fun accept(t: T) {
        if(filter(t)) consumer.accept(t)
    }
}

fun <T> IMyConsumer<T>.acceptAll(iterable: Iterable<T>) {
    iterable.forEach { accept(it) }
}

fun <T> IMyConsumer<T>.andThen(after: IMyConsumer<T>) = MyMultiConsumer(this, after)

fun <T> IMyConsumer<T>.filter(filter: (T) -> Boolean): IMyConsumer<T> = MyFilteredConsumer(this, filter)


interface IMySupplier<out T> {
    fun get(): T
}


interface IMyBuffer<T> : IMyArray<T>, IMyConsumer<T>



class MyRingBuffer<T>(val capacity: Int = 256) : IMyBuffer<T> {

    private val array: ArrayList<T> = ArrayList(capacity)

    private var startpos: Int = 0


    override fun get(idx: Int): T = array[startpos + idx % size]

    override val size: Int
        get() = array.size

    override fun accept(t: T) {
        if(size < capacity)
            array.add(t)
        else {
            array[startpos] = t
            startpos = (startpos + 1) % size
        }
    }

}

