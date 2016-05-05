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
        println("ps $s")
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

        1 { println("111111111111") }

        val f = len * ::db * { i: Int -> i * 3 } * obj * divide

        val g = nPulleeOf(1, 22, 555, 9999) * { i: Int? -> i.toString() } * f * pd

        3 (g)

        2 { println("22222222222") }

        1 { { println("ble") }() }

        val e = ePulleeOf("aaa", "bbb", "ccc").vpeek { println(it) }

        5 (e)

        3 { println("333333333333333") }

        val ee = ePulleeOf("aaa", "bbb", "ccc").vpeek { println("vpeeeeek $it") }

        for(i in ee.niter())
            println("niter $i")
        // NOTE: vpeek will print 2 items before for loop will start to print any. this is correct.
        // it is because niter has to pull items in advance to now if iterator "hasNext"
    }

    @Test
    fun testPullees() {
        val npullee = (1..10).asNPullee()
                .vnmap { it * 2 }
                .vnzip((1..10).asNPullee())

        for(i in npullee.niter())
            println(i)


        val epullee = (1..10).asNPullee()
                .vnmap { Item(it * 2) }
                .vemap(divide)

        for(i in epullee.niter())
            println(i)


        val npullee2 = (1..30).asNPullee()
                .vfilter { it === null || it % 3 == 0 }

        for(i in npullee2.niter())
            println("niter $i")

        val npullee3 = (1..30).asEPullee()
                .vpeek { println("vpeeeeek $it") }
                .vfilter { it === null || it is Item && it.item % 3 == 0 }

        for(i in npullee3.niter())
            println("niter $i")

    }

    @Test
    fun testPushees() {
        val pushee = { s: String -> println("xxx $s") }
                .apeek { println("yyy $it") }
                .afilter { it.length > 4 }
                .apeek { println("zzz $it") }

        pushee("a")
        pushee("aa")
        pushee("aaa")
        pushee("aaaa")
        pushee("aaaaa")
        pushee("aaaaaa")
        pushee("aaaaaaa")
        pushee("aaaaaaaa")


        val pushee2 = { s: String -> Unit }
                .apeek(pushee)
                .apeek(pushee)

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

        val pp = pullee
//        val pp = pullee.vpeek(pni)

        for (x in pp.niter())
            println(x)
    }

    @Test
    fun testStepPusher() {

        val ctl = StepPusher(3)
                .lpeek { println("xxx $it")}
                .ltake(5)
                .lpeek { println("yyy $it") }
                .lmap { it + 100 }
                .invoke { println("zzz $it") } // this is like subscribe in rxjava

        ctl(Start)
        ctl(Step)
        ctl(Step)
        ctl(Step)
        ctl(Step)
        ctl(Step)
        ctl(Inject(666L))
        ctl(Tick)
        ctl(Inject(667L))
        ctl(Tick)
        ctl(Inject(668L))
        ctl(Tock)
        ctl(Inject(669L))
        ctl(Stop)
        ctl(Cancel)

    }

    // TODO SOMEDAY: more machine tests

}




