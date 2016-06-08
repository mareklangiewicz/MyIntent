package pl.mareklangiewicz.myutils


private val MIN_CAPACITY = 8 // Must be a power of 2

/**
 * Created by Marek Langiewicz on 28.05.16.
 * Pretty versatile collection implementation.
 */
class Lst<T>(initcap: Int = MIN_CAPACITY) : ILst<T> {

    private var arr: Array<Any?>
    private var beg: Int = 0
    private var end: Int = 0

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

    override fun clear() {
        val mask = arr.size - 1
        while(beg != end) {
            arr[beg] = null
            beg = beg + 1 and mask
        }
        beg = 0
        end = 0
    }

    // negative indices are translated like in python: idx = size + idx
    override fun get(idx: Int): T {
        val i = idx.pos(size).chk(0, size-1)
        @Suppress("UNCHECKED_CAST")
        return arr[beg + i and arr.size - 1] as T
    }

    // negative indices are translated like in python: idx = size + idx
    override fun set(idx: Int, item: T) {
        val i = idx.pos(size).chk(0, size-1)
        arr[beg + i and arr.size - 1] = item
    }

    override val size: Int
        get() = end - beg and arr.size - 1


    override val head = ILst.Head(this)
    override val tail = ILst.Tail(this)

    // negative indices are translated like in python: idx = size + idx
    override fun ins(idx: Int, item: T) {
        val i = idx.pos(size).chk(0, size)
        insert(beg + i and arr.size - 1, item)
    }

    // negative indices are translated like in python: idx = size + idx
    override fun del(idx: Int): T {
        val i = idx.pos(size).chk(0, size-1)
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
        fun <T> of(vararg items: T): Lst<T> {
            val lst = Lst<T>(items.size + 1)
            lst.end = items.size
            for(i in 0..items.size-1)
                lst.arr[i] = items[i]
            return lst
        }
    }
}
