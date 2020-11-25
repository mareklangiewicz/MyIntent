package pl.mareklangiewicz.myutils

import org.junit.Test
import java.util.*
import pl.mareklangiewicz.pue.*

/**
 * Created by Marek Langiewicz on 30.05.16.
 */
class LstTest {

    private val log = MySystemLogger()

    @Test fun testConstr() {

        val lst1: ILst<String> = Lst<String>()
        lst1.add("bla")
        lst1.add("ble")

        val lst2: ILst<String> = Lst.of("bla", "ble")

        val lst3: ILst<String> = Lst.from(listOf("bla", "ble"))

        val lst4: ILst<String> = Lst.from(listOf("bla", "ble") as Iterable<String>)

        Assert That lst1 IsEqualTo lst2
        Assert That lst2 IsEqualTo lst1

        Assert That lst2 IsEqualTo lst3
        Assert That lst3 IsEqualTo lst2

        Assert That lst3 IsEqualTo lst4
        Assert That lst4 IsEqualTo lst3

        Assert That lst1 IsEqualTo lst4
        Assert That lst4 IsEqualTo lst1

        lst1[1] = "blu"

        Assert That lst1 IsNotEqualTo lst2
        Assert That lst1 IsNotEqualTo lst3
        Assert That lst1 IsNotEqualTo lst4
    }

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
        Assert That lst[0] IsEqualTo lst[-6]
        Assert That lst[1] IsEqualTo lst[-5]
        Assert That lst[2] IsEqualTo lst[-4]
        Assert That lst[3] IsEqualTo lst[-3]

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
                Assert That lst[j] IsEqualTo r[j]
            val idx = inserts.pop()
            Assert That lst[idx] IsEqualTo i.hex
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
                Assert That lst[j] IsEqualTo r[j]
            val idx = inserts.pop()
            val t = if(idx % 7 == 0) // special case: pull head (we were pushing head before)
                lst.head.pull(Unit)
            else if(idx % 7 == 1) // special case: pull tail (we were pushing tail before)
                lst.tail.pull(Unit)
            else
                lst.del(idx)
            Assert That t IsEqualTo i.hex
        }

    }
}
