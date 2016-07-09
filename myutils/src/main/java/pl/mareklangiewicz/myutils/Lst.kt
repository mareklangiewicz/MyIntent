package pl.mareklangiewicz.myutils


private val MIN_CAPACITY = 8 // Must be a power of 2

/**
 * Created by Marek Langiewicz on 28.05.16.
 * Pretty versatile collection implementation.
 */
open class Lst<T>(initcap: Int = MIN_CAPACITY) : ILst<T> {

    private var arr: Array<Any?> // elements can be rotated (if end <= beg)
    private var beg: Int = 0 // idx in arr of first element
    private var end: Int = 0 // idx in arr right after last element

    init {
        var cap: Int = MIN_CAPACITY

        // make sure capacity is power of 2:
        while(cap < initcap && cap > 0)
            cap = cap shl 1

        if(cap < 0) // overflow
            cap = cap ushr 1

        arr = arrayOfNulls(cap)
    }


    // Like System.arraycpy, but it makes sure it does nothing at all if len <= 0
    // So I can safely call it with totally incorrect array positions (but only if len <= 0)
    private fun cpy(src: Array<Any?>, srcpos: Int, dst: Array<Any?>, dstpos: Int, len: Int) {
        if (len > 0) System.arraycopy(src, srcpos, dst, dstpos, len)
    }

    /**
     * reallocate array with capacity * 2
     * call only when full (when beg == end)
     */
    private fun boost() {
        val p = beg
        val n = arr.size
        val r = n - p // number of elements to the right of p
        val newcap = n shl 1
        if (newcap < 0)
            throw IllegalStateException("MyList is too big.")
        val a = arrayOfNulls<Any>(newcap)
        cpy(arr, p, a, 0, r)
        cpy(arr, 0, a, r, p)
        arr = a
        beg = 0
        end = n
    }

    override fun clr() {
        val mask = arr.size - 1
        while(beg != end) {
            arr[beg] = null
            beg = beg + 1 and mask
        }
        beg = 0
        end = 0
    }

    // negative indices are translated like in python: idx = len + idx
    override fun get(idx: Int): T {
        val i = idx.pos(len).chk(0, len-1)
        @Suppress("UNCHECKED_CAST")
        return arr[beg + i and arr.size - 1] as T
    }

    // negative indices are translated like in python: idx = len + idx
    override fun set(idx: Int, item: T) {
        val i = idx.pos(len).chk(0, len-1)
        arr[beg + i and arr.size - 1] = item
    }

    override val len: Int
        get() = end - beg and arr.size - 1


    override val head = LstHead(this)
    override val tail = LstTail(this)

    // negative indices are translated like in python: idx = len + idx
    override fun ins(idx: Int, item: T) {
        val i = idx.pos(len).chk(0, len)
        insert(beg + i and arr.size - 1, item)
    }

    // negative indices are translated like in python: idx = len + idx
    override fun del(idx: Int): T {
        val i = idx.pos(len).chk(0, len-1)
        return delete(beg + i and arr.size - 1)
    }



    // parameter idx here is index in underlying array: arr
    private fun insert(idx: Int, t: T) {

        var i = idx
        val mask = arr.size - 1

        if(i == beg) { // special case: we add element at the beginning without shifting any elements
            i = i - 1 and mask
            beg = i
        }
        else if(i == end) { // special case: we add element at the end withour shifting any elements
            end = i + 1 and mask
        }
        else if (i - beg and mask < end - i and mask) { // we are moving one step backward all elements before idx
            if (beg > 0 && beg < i)
                cpy(arr, beg, arr, beg - 1, i - beg)
            else { // Wrap around
                cpy(arr, beg, arr, beg - 1, arr.size - beg and mask)
                if(i > 0) {
                    arr[mask] = arr[0]
                    cpy(arr, 1, arr, 0, i - 1)
                }
            }
            beg = beg - 1 and mask
            i = i - 1 and mask
        } else { // we are moving one step forward all elements after idx (and old arr[idx] too)
            if (i < end)
                cpy(arr, i, arr, i + 1, end - i)
            else { // Wrap around
                cpy(arr, 0, arr, 1, end)
                arr[0] = arr[mask]
                cpy(arr, i, arr, i + 1, mask - i)
            }
            end = end + 1 and mask
        }
        arr[i] = t
        if (beg == end) boost()
    }

    // parameter idx here is index in underlying array: arr
    private fun delete(idx: Int): T {
        @Suppress("UNCHECKED_CAST")
        val result = arr[idx] as T
        val mask = arr.size - 1
        if (idx - beg and mask < end - idx and mask) { // we are moving forward elements before idx
            if (beg <= idx)
                cpy(arr, beg, arr, beg + 1, idx - beg)
            else { // Wrap around
                cpy(arr, 0, arr, 1, idx)
                arr[0] = arr[mask]
                cpy(arr, beg, arr, beg + 1, mask - beg)
            }
            arr[beg] = null
            beg = beg + 1 and mask
        } else { // we are moving backwards elements after idx
            if (idx < end)
                cpy(arr, idx + 1, arr, idx, end - idx) // We copy the null tail as well
            else { // Wrap around
                cpy(arr, idx + 1, arr, idx, mask - idx)
                arr[mask] = arr[0]
                cpy(arr, 1, arr, 0, end)
            }
            end = end - 1 and mask
        }
        return result
    }

    companion object {

        fun <T> of(vararg items: T): Lst<T> = from(items.asArr())

        fun <T> from(items: ICol<T>): Lst<T> {
            val lst = Lst<T>(items.len + 1)
            lst.end = items.len
            for((i, item) in items.withIndex()) lst.arr[i] = item
            return lst
        }
    }
}

// use it to easily implement the head property of your Lst implementation
class LstHead<T>(val lst: ILst<T>) : ISak<T> {
    override val push: (T) -> Unit = { lst.ins(0, it) }
    override val pull: (Unit) -> T? = { if(lst.len <= 0) null else lst.del(0) }
    override val peek: (Unit) -> T? = { if(lst.len <= 0) null else lst.get(0) }
}

// use it to easily implement the tail property of your Lst implementation
class LstTail<T>(val lst: ILst<T>) : ISak<T> {
    override val push: (T) -> Unit = { lst.ins(lst.len, it) }
    override val pull: (Unit) -> T? = { if(lst.len <= 0) null else lst.del(lst.len-1) }
    override val peek: (Unit) -> T? = { if(lst.len <= 0) null else lst.get(lst.len-1) }
}




/** Small protocol for transfering information about Lst changes */
interface  LstChg<out T>
data class LstSet<out T>(val idx: Int, val item: T ) : LstChg<T>
data class LstIns<out T>(val idx: Int, val item: T ) : LstChg<T>
data class LstDel<out T>(val idx: Int              ) : LstChg<T>
data class LstAdd<out T>(val item: T               ) : LstChg<T>
data class LstMov<out T>(val src: Int, val dst: Int) : LstChg<T>
     class LstClr<out T>(                          ) : LstChg<T>
     class LstOth<out T>(                          ) : LstChg<T> // some other unknown change happened. just sync all data.
// user can add and support additional change types.
// user should usually sync all data when he receives unknown change type.


fun <T> ILst<T>.asChgPushee(): (LstChg<T>) -> Unit = {
    when (it) {
        is LstSet<T> -> set(it.idx, it.item)
        is LstIns<T> -> ins(it.idx, it.item)
        is LstDel<T> -> del(it.idx)
        is LstAdd<T> -> add(it.item)
        is LstMov<T> -> mov(it.src, it.dst)
        is LstClr<T> -> clr()
        else -> throw ClassCastException("Unsupported lst change: $it")
    }
}

/** Wraps given lst so it emits all changes. Important: only changes made through this wrapper will be emited. */
fun <T> ILst<T>.withChgPusher() = LstWithChgPusher(this)



open class LstWithChgPusher<T>(private val lst: ILst<T> = Lst()) : ILst<T> by lst, IChanges<LstChg<T>> {

    override val changes = Relay<LstChg<T>>()

    private var quiet = false

    override fun clr() {
        lst.clr()
        if(!quiet) changes.push(LstClr<T>())
    }

    override fun set(idx: Int, item: T) {
        lst.set(idx, item)
        if(!quiet) changes.push(LstSet(idx, item))
    }

    override fun ins(idx: Int, item: T) {
        lst.ins(idx, item)
        if(!quiet) changes.push(LstIns(idx, item))
    }

    override fun del(idx: Int): T {
        val item = lst.del(idx)
        if(!quiet) changes.push(LstDel<T>(idx))
        return item
    }

    override fun add(item: T) {
        lst.add(item)
        if(!quiet) changes.push(LstAdd(item))
    }

    override fun mov(src: Int, dst: Int) {
        lst.mov(src, dst)
        if(!quiet) changes.push(LstMov<T>(src, dst))
    }

    /** runs a bunch of operations and emits only one change: "LstOth" at the end. */
    fun batch(block: LstWithChgPusher<T>.() -> Unit) {
        quiet = true
        block()
        changes.push(LstOth())
        quiet = false
    }

    override val head = LstHead(this) // have to redefine it so it calls our versions of ins or del
    override val tail = LstTail(this) // have to redefine it so it calls our versions of ins or del
}




fun <T> ILst<T>.withFilter(filter: (T) -> Boolean = { true }) = LstWithFilter(filter, this)

// TODO NOW: test it!
open class LstWithFilter<T>(filter: (T) -> Boolean = { true }, private val lst: ILst<T> = Lst()) : ILst<T> by lst {

    val out = LstWithChgPusher<T>()

    fun sync() = out.batch {
        clr()
        for(t in lst) if(filter(t)) add(t)
    }

    init { if(lst.len > 0) sync() }

    var filter = filter
        set(value) {
            field = value
            sync()
        }

    override fun clr() {
        lst.clr()
        if(out.len > 0) {
            out.clr()
        }
    }

    override fun set(idx: Int, item: T) {
        lst.set(idx, item)
        sync() // TODO SOMEDAY: optimize so we don't always have to sync all.
    }

    override fun ins(idx: Int, item: T) {
        lst.ins(idx, item)
        if(filter(item)) {
            when(idx) {
                0    -> out.ins(0, item)
                len  -> out.add(item)
                else -> sync() // TODO SOMEDAY: optimize so we don't always sync all when inserting in the middle
            }
        }
    }

    override fun del(idx: Int): T {
        val item = lst.del(idx)
        if(out.len > 0) when(idx) {
            0     -> if(out[0] === item) out.del(0)
            len-1 -> if(out[out.len-1] === item) out.del(out.len-1)
            else  -> sync() // TODO SOMEDAY: optimize so we don't always sync all when deleting in the middle
        }
        return item
    }

    override fun add(item: T) {
        lst.add(item)
        if(filter(item)) out.add(item)
    }

    override fun mov(src: Int, dst: Int) {
        lst.mov(src, dst)
        sync() // TODO SOMEDAY: optimize so we don't always sync.
    }

    override val head = LstHead(this) // have to redefine it so it calls our versions of ins or del
    override val tail = LstTail(this) // have to redefine it so it calls our versions of ins or del

}




fun <T> ILst<T>.withLimit(limit: Int) = LstWithLimit(limit, this)

/**
 * LstWithLimit is a Lst that has limited size.
 * It will drop last element when it's full.
 * (or first if new element is inserted at the end)
 * So it should never have to reallocate memory.
 */
open class LstWithLimit<T>(val limit: Int, private val lst: ILst<T> = Lst(limit)): ILst<T> by lst {

    override fun ins(idx: Int, item: T) {
        if(len < limit) // no dropping
            lst.ins(idx, item)
        else if(idx == len) { // drop from the beginning
            lst.del(0)
            lst.ins(idx-1, item)
        }
        else { // drop from the end
            lst.del(len-1)
            lst.ins(idx, item)
        }

    }

    override fun add(item: T) = ins(len, item)

    override fun mov(src: Int, dst: Int) {
        if(dst == src) return
        val item = del(src)
        ins(if(dst > src) dst-1 else dst, item)
    }

    override val head = LstHead(this) // have to redefine it so it calls our versions of ins or del
    override val tail = LstTail(this) // have to redefine it so it calls our versions of ins or del
}




