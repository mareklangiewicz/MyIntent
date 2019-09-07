package pl.mareklangiewicz.myutils

import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by Marek Langiewicz on 22.02.16.
 */

fun db(x: Int) = x * 2

class PueTest {

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

        33345 / { it * 10 } / Int::toString / ps
        33345 / { it * 10 } / Int::toString / len / pi

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

        for(i in ee) // we call IPullee<R?>.iterator() extension function here.
            println("next: $i")
    }

    @Test
    fun testPullees() {
        val npullee = (1..10).asNPullee()
                .vnmap { it * 2 }
                .vnzip((1..10).asNPullee())

        for(i in npullee)
            println(i) // we call IPullee<R?>.iterator() extension function here.


        val epullee = (1..10).asNPullee()
                .vnmap { Item(it * 2) }
                .vemap(divide)

        for(i in epullee) // we call IPullee<R?>.iterator() extension function here.
            println(i)


        val npullee2 = (1..30).asNPullee()
                .vfilter { it === null || it % 3 == 0 }

        for(i in npullee2) // we call IPullee<R?>.iterator() extension function here.
            println("next: $i")

        val npullee3 = (1..30).asEPullee()
                .vpeek { println("vpeeeeek $it") }
                .vfilter { it === null || it is Item && it.item % 3 == 0 }

        for(i in npullee3) // we call IPullee.iterator() extension function here
            println("next: $i")

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


        val pushee2 = { _: String -> Unit }
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

        for (x in pp) // we call IPullee.iterator() extension function here
            println(x)
    }

    @Test
    fun testStepPusher() {

        val ctl = StepPusher(3)
                .lapeek { println("xxx $it")}
                .latake(5)
                .lapeek { println("yyy $it") }
                .lamap { it + 100 }
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

    @Test
    fun testMerge() {

        val r1 = Relay<String>()
        val r2 = Relay<String>()

        val mergedS = merge(r1, r2)

        val ctl = mergedS(ps)

        r1.push("1 1")
        r1.push("2 1")
        r1.push("3 1")
        r2.push("4 2")
        r2.push("5 2")
        r1.push("6 11")
        r1.push("7 11")
        r2.push("8 222")
        r1.push("9 111")
        ctl(Cancel)
        r1.push("10 11111111111111 - after Cancel")
        r2.push("11 22222222222222 - after Cancel")
    }

    @Test
    fun testScan() {

        val r = Relay<String>()

        val scanS = r.scan("seed") { acc,t -> "$acc.$t" }

        val ctl = scanS(ps)

        r.push("1")
        r.push("2")
        r.push("3")
        r.push("4")

    }

    // TODO SOMEDAY: more pue tests

}




