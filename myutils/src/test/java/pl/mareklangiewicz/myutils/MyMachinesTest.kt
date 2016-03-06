package pl.mareklangiewicz.myutils

import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by Marek Langiewicz on 22.02.16.
 */

fun db(x: Int) = x * 2

class MyMachinesTest {

    val ps = { s: String ->
        println(s)
    }
    val pi = { i: Int -> println(i) }
    val pni = { i: Int? -> println(i) }
    val pd = { d: Double -> println(d) }

    val len = { t: String -> t.length }

    val obj = object : Function1<Int, Int> {
        override operator fun invoke(x: Int) = x * 4
    }

    val divide = object : Function1<Int, Double> {
        override operator fun invoke(x: Int) = x.toDouble() / 1234
    }

    val xx = obj(7789)

    val xxx: (Unit) -> Unit = { println("xxx") }


    @Before
    fun setUp() {

    }

    @After
    fun tearDown() {

    }

    @Test
    fun test1() {

        1 { println("start") }
        3 (const("START!") % ps)

    }

    @Test
    fun testCrazyBasics() {

        3 (xxx)

        2 (const(660) * pi)
        2 (const(661) * obj * pi)
        2 (const(662) * obj % pi)
        2 (const(663) * obj % pi * ::db % pi)

        val f = len * ::db * { i: Int -> i * 3 } * obj * divide

        val g = nPulleeOf(1, 22, 555, 9999) * { i: Int? -> i.toString() } * f * pd

        3 (g)

        1 { println("bla") }

        1 { { -> println("ble") }() }

        val e = ePulleeOf("aaa", "bbb", "ccc").veff { println(it) }

        5 (e)

        val ee = ePulleeOf("aaa", "bbb", "ccc").veff { println(it) }

        for(i in ee.eiter())
            println(i)
    }

    @Test
    fun testPullees() {
        val npullee = (1..10).asNPullee()
                .vnmap { it * 2 }
                .vnzip((1..10).asNPullee())

        for(i in npullee.niter())
            println(i)


        val epullee = (1..10).asNPullee()
                .vnmap { it * 2 }
                .vmap { if(it === null) Completed<Int>() else Item(it) }
                .vemap(divide)

        for(i in epullee.eiter())
            println(i)


        val npullee2 = (1..30).asNPullee()
                .vnfilter { it % 3 == 0 }

        for(i in npullee2.niter())
            println(i)

        val npullee3 = (1..30).asEPullee()
                .veff { println(it) }
                .vefilter { it % 3 == 0 }

        for(i in npullee3.eiter())
            println(i)

    }

    @Test
    fun testPushees() {
        val pushee = { s: String -> println("xxx $s") }
                .aeff { println("yyy $it") }
                .afilter { it.length > 4 }
                .aeff { println("zzz $it") }

        pushee("a")
        pushee("aa")
        pushee("aaa")
        pushee("aaaa")
        pushee("aaaaa")
        pushee("aaaaaa")
        pushee("aaaaaaa")
        pushee("aaaaaaaa")


        val pushee2 = { s: String -> Unit }
                .aeff(pushee)
                .aeff(pushee)

        pushee2("b")
        pushee2("bb")
        pushee2("bbb")
        pushee2("bbbb")
        pushee2("bbbbb")
        pushee2("bbbbbb")
        pushee2("bbbbbbb")
        pushee2("bbbbbbbb")

        val pushee3 = { i: Int -> println(i) }
                .amap<Int, Unit, Int> { it * 2 }

        pushee3(2)
        pushee3(4)

        val npushee = { i: Int? -> println(i) }
                .anmap<Int, Unit, Int> { it * 2 }

        npushee(12)
        npushee(14)
    }





    @Test
    fun testFlat() {

        val pullee = nPulleeOf(
                nPulleeOf(1, 2, 3),
                nPulleeOf(4, 5, 6),
                (11..20).asNPullee().vnmap { it * 10 }
        )
                .vnflat()
                .vnmap { it * 2 }

        val pp = pullee.veff(pni)
        for (x in pp.niter())
            println(x)
    }

    @Test
    fun testStepPusher() {

        val ctl = StepPusher(3)
                .leff { println("xxx $it")}
                .ltake(5)
                .leff { println("yyy $it") }
                .lmap { it + 100 }
                .invoke { println("zzz $it") }

        ctl(Start)
        ctl(Step)
        ctl(Step)
        ctl(Step)
        ctl(Step)
        ctl(Step)
        ctl(Tick)
        ctl(Stop)
        ctl(Cancel)

    }

    // TODO SOMEDAY: more machine tests

}




