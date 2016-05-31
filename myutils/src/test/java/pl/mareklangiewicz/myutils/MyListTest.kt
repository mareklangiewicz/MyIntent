package pl.mareklangiewicz.myutils

import org.junit.Test
import java.util.*
import com.google.common.truth.Truth.assertThat

/**
 * Created by Marek Langiewicz on 30.05.16.
 */
class MyListTest {

    private val log = MySystemLogger()

    @Test fun testBasics() {
        val list = MyList<String>()
        list.head.push("aaa")
        list.head.push("bbb")
        list.head.push("ccc")
        list.tail.push("xxx")
        list.tail.push("yyy")
        list.tail.push("zzz")
        list.del(1)
        list.ins(1, "bbbbbb")
        list[5] = "zzzzzzzzz"

        // negative indicies should count from the end
        assertThat(list[0]).isEqualTo(list[-6])
        assertThat(list[1]).isEqualTo(list[-5])
        assertThat(list[2]).isEqualTo(list[-4])
        assertThat(list[3]).isEqualTo(list[-3])

        log.i("MyList iteration:")

        for(a in list)
            log.i(a)


        val s = StringBuilder()
        for(x in 1..20) {
            s.append("${(x + 64).toChar()}")
            log.i("tail push: $s")
            list.tail.push(s.str)
            val p = list.head.pull(Unit)
            log.i("head pull: $p")
        }

        log.i("MyList iteration:")
        for(a in list)
            log.i(a)

        log.i("MyList.tail pull iteration (destructive):")

        for(a in list.tail.pull)
            log.i(a)
    }

    val Int.hex: String get() = Integer.toHexString(this)

    @Test fun testInsDel() {

        val list = MyList<String>() // we will test operation on this list.

        val inserts = ArrayDeque<Int>()
        val results = ArrayDeque<ArrayList<String>>()

        val rnd = Random() // we will use this generator to generate random stuff
        // and to be able repeat exactly the same random generations if some problems are detected.
        val SEED = 666L
        val COUNT = 8000
        rnd.setSeed(SEED)

        list.tail.push("aaa")
        list.tail.push("bbb")

        for(i in 1..COUNT) {
            val idx = rnd.nextInt(0, list.size+1)
            list.ins(idx, i.hex)
            inserts.push(idx)
            val r = ArrayList<String>()
            list.forEach { r.add(it) }
            results.push(r)
        }

        // everything recorded. now deleting in reverse order and testing:
        for(i in COUNT downTo 1) {
            val r = results.pop()
            for(j in 0..r.size-1)
                assertThat(list[j]).isEqualTo(r[j])
            val idx = inserts.pop()
            assertThat(list[idx]).isEqualTo(i.hex)
            list.del(idx)
        }


        // Again with some pushes and pulls..
        for(i in 1..COUNT) {
            val idx = rnd.nextInt(0, list.size+1)
            val t = i.hex
            if(idx % 7 == 0) // special case: push head
                list.head.push(t)
            else if(idx % 7 == 1) // special case: push tail
                list.tail.push(t)
            else
                list.ins(idx, t)
            inserts.push(idx)
            val r = ArrayList<String>()
            list.forEach { r.add(it) }
            results.push(r)
        }

        // everything recorded. now deleting in reverse order and testing:
        for(i in COUNT downTo 1) {
            val r = results.pop()
            for(j in 0..r.size-1)
                assertThat(list[j]).isEqualTo(r[j])
            val idx = inserts.pop()
            var t = if(idx % 7 == 0) // special case: pull head (we were pushing head before)
                list.head.pull(Unit)
            else if(idx % 7 == 1) // special case: pull tail (we were pushing tail before)
                list.tail.pull(Unit)
            else
                list.del(idx)
            assertThat(t).isEqualTo(i.hex)
        }

    }
}
