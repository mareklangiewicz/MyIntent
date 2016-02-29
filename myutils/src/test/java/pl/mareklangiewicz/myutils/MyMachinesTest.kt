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
        3 ( const("START!") % ps )

    }

    @Test
    fun testCrazyBasics() {

        3 ( xxx )

        2 ( const(660) * pi )
        2 ( const(661) * obj * pi )
        2 ( const(662) * obj % pi )
        2 ( const(663) * obj % pi * ::db % pi )

        val f = len * ::db * { i: Int -> i * 3 } * obj * divide

        val g = NPulleeOf(1, 22, 555, 9999) * { i: Int? -> i.toString() } * f * pd

        3 ( g )

        1 { println("bla") }

        1 { { -> println("ble") }() }

    }

    @Test
    fun testEngine() {
        LoopEngine()
                .limit(10)
                .veff { println("engine move") }
                .attrt()
    }

    // TODO NOW: more engine tests (with timers too)

    @Test
    fun testFlat() {

//        val pullee = NPulleeOf(1, 2, 3)

        val pullee = NPulleeOf(
                NPulleeOf(1, 2, 3),
                NPulleeOf(4, 5, 6),
                NPulleeOf(7, 8, 9, 10),
                (11..20).asNPullee().vnmap { it * 10 }
        ).nflat().vnmap { it * 2 }

        val pp = pullee % n(pi)

//        npull(pp)

        NPuller<Int>().attrt(pp)
    }


}