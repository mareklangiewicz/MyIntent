package pl.mareklangiewicz.myutils

import org.junit.Test
import pl.mareklangiewicz.mytests.assert
import pl.mareklangiewicz.mytests.isAtLeast
import pl.mareklangiewicz.mytests.isLessThan
import pl.mareklangiewicz.mytests.that

/**
 * Created by Marek Langiewicz on 04.11.15.
 */
class MyMathUtilsTest {

    @Test fun testGetRandomInt() {
        for (i in 0..29999) {
            val r = RANDOM.nextInt(1, 5)
            assert that r isAtLeast 1
            assert that r isLessThan 5
        }
        for (i in 0..29999) {
            val r = RANDOM.nextInt(-10000, 0)
            assert that r isAtLeast -10000
            assert that r isLessThan 0
        }
    }

    @Test fun testRandomAsInts() {
        val ints = RANDOM.asInts(1, 5)
        for (i in 0..29999) {
            val r = ints(Unit)
            assert that r isAtLeast 1
            assert that r isLessThan 5
        }
    }

    @Test fun testRandomAsFloats() {
        val floats = RANDOM.asFloats(1.1f, 5.5f)
        for (i in 0..29999) {
            val r = floats(Unit)
            assert that r isAtLeast 1.1f
            assert that r isLessThan 5.5f
        }
    }

    @Test fun testRandomAsDoubles() {
        val floats = RANDOM.asDoubles(1.1, 5.5)
        for (i in 0..29999) {
            val r = floats(Unit)
            assert that r isAtLeast 1.1
            assert that r isLessThan 5.5
        }
    }

    // can not test random points and colors here - it is android stuff...
}