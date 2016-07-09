package pl.mareklangiewicz.myutils

import org.junit.Test
import java.util.*
import com.google.common.truth.Truth.assertThat

/**
 * Created by Marek Langiewicz on 30.05.16.
 */
class LstTest {

    private val log = MySystemLogger()

    @Test fun testBasics() {
        val lst = Lst<String>()
        lst.head.push("aaa")
        lst.head.push("bbb")
        lst.head.push("ccc")
        lst.tail.push("xxx")
        lst.tail.push("yyy")
        lst.tail.push("zzz")
        lst.del(1)
        lst.ins(1, "bbbbbb")
        lst[5] = "zzzzzzzzz"

        // negative indicies should count from the end
        assertThat(lst[0]).isEqualTo(lst[-6])
        assertThat(lst[1]).isEqualTo(lst[-5])
        assertThat(lst[2]).isEqualTo(lst[-4])
        assertThat(lst[3]).isEqualTo(lst[-3])

        log.i("Lst iteration:")

        for(a in lst)
            log.i(a)


        val s = StringBuilder()
        for(x in 1..20) {
            s.append("${(x + 64).toChar()}")
            log.i("tail push: $s")
            lst.tail.push(s.str)
            val p = lst.head.pull(Unit)
            log.i("head pull: $p")
        }

        log.i("Lst iteration:")
        for(a in lst)
            log.i(a)

        log.i("Lst.tail pull iteration (destructive):")

        for(a in lst.tail.pull)
            log.i(a)
    }

    val Int.hex: String get() = Integer.toHexString(this)

    @Test fun testInsDel() {

        val lst = Lst<String>() // we will test operation on this list.

        val inserts = ArrayDeque<Int>()
        val results = ArrayDeque<ArrayList<String>>()

        val rnd = Random() // we will use this generator to generate random stuff
        // and to be able repeat exactly the same random generations if some problems are detected.
        val SEED = 666L
        val COUNT = 8000
        rnd.setSeed(SEED)

        lst.tail.push("aaa")
        lst.tail.push("bbb")

        for(i in 1..COUNT) {
            val idx = rnd.nextInt(0, lst.len+1)
            lst.ins(idx, i.hex)
            inserts.push(idx)
            val r = ArrayList<String>()
            lst.forEach { r.add(it) }
            results.push(r)
        }

        // everything recorded. now deleting in reverse order and testing:
        for(i in COUNT downTo 1) {
            val r = results.pop()
            for(j in 0..r.size-1)
                assertThat(lst[j]).isEqualTo(r[j])
            val idx = inserts.pop()
            assertThat(lst[idx]).isEqualTo(i.hex)
            lst.del(idx)
        }


        // Again with some pushes and pulls..
        for(i in 1..COUNT) {
            val idx = rnd.nextInt(0, lst.len+1)
            val t = i.hex
            if(idx % 7 == 0) // special case: push head
                lst.head.push(t)
            else if(idx % 7 == 1) // special case: push tail
                lst.tail.push(t)
            else
                lst.ins(idx, t)
            inserts.push(idx)
            val r = ArrayList<String>()
            lst.forEach { r.add(it) }
            results.push(r)
        }

        // everything recorded. now deleting in reverse order and testing:
        for(i in COUNT downTo 1) {
            val r = results.pop()
            for(j in 0..r.size-1)
                assertThat(lst[j]).isEqualTo(r[j])
            val idx = inserts.pop()
            val t = if(idx % 7 == 0) // special case: pull head (we were pushing head before)
                lst.head.pull(Unit)
            else if(idx % 7 == 1) // special case: pull tail (we were pushing tail before)
                lst.tail.pull(Unit)
            else
                lst.del(idx)
            assertThat(t).isEqualTo(i.hex)
        }

    }
}
