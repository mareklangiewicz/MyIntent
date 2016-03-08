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
}