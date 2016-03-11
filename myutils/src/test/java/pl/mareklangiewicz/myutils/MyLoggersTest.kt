package pl.mareklangiewicz.myutils

import org.junit.Test

/**
 * Created by Marek Langiewicz on 07.03.16.
 */

class MyLoggersTest {
    @Test
    fun test1() {
        val entry1 = MyLogEntry("fdjksal")
        val entry2 = MyLogEntry("fdjksal")
        val entry3 = MyLogEntry("fdjksal", MyLogLevel.DEBUG, "TAG", IllegalStateException("some error"))
        println(entry1)
        println(entry2)
        println(entry3)

    }


    @Test
    fun test2() {
        val log = MySystemLogger(MyLogLevel.DEBUG, MyLogLevel.ERROR)
                .trace(8)
        log.v("some verbose")
        log.d("some debug")
        log.i("some info")
        log.w("some warning")
        log.e("some error")
        log.a("some assert")
        log.q("some quiet - this should just be ignored...")
        log.i("some info", "SOME OTHER CUSTOM TAG")
        log.a("some assert", "SOME CUSTOM TAG")
    }
}