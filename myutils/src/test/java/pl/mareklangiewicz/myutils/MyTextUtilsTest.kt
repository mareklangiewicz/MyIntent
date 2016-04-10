package pl.mareklangiewicz.myutils

import org.junit.After
import org.junit.Before
import org.junit.Test
import pl.mareklangiewicz.mycorelib.MyClass

import java.util.Arrays

/**
 * Created by Marek Langiewicz on 01.10.15.
 */
class MyTextUtilsTest {

    @Before fun setUp() { }

    @After fun tearDown() { }

    @Test fun testStr() {
        val list = Arrays.asList("bla", "ble")
        println(list.str)
    }

    @Test fun testToShortStr() { }

    @Test fun testToLongStr() { }

    @Test fun testToVeryLongStr() { }

    @Test fun testMyCoreLibBuild() {
        MyClass.bla()
    }
}

