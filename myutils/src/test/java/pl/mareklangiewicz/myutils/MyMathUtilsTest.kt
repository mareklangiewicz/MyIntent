package pl.mareklangiewicz.myutils

import org.junit.Test
import pl.mareklangiewicz.upue.Assert
import pl.mareklangiewicz.upue.IsAtLeast
import pl.mareklangiewicz.upue.IsLessThan
import pl.mareklangiewicz.upue.That

/**
 * Created by Marek Langiewicz on 04.11.15.
 */
class MyMathUtilsTest {

    @Test fun testGetRandomInt() {
        for (i in 0..29999) {
            val r = RANDOM.nextInt(1, 5)
            Assert That r IsAtLeast 1
            Assert That r IsLessThan 5
        }
        for (i in 0..29999) {
            val r = RANDOM.nextInt(-10000, 0)
            Assert That r IsAtLeast -10000
            Assert That r IsLessThan 0
        }
    }

    @Test fun testRandomAsInts() {
        val ints = RANDOM.asInts(1, 5)
        for (i in 0..29999) {
            val r = ints(Unit)
            Assert That r IsAtLeast 1
            Assert That r IsLessThan 5
        }
    }

    @Test fun testRandomAsFloats() {
        val floats = RANDOM.asFloats(1.1f, 5.5f)
        for (i in 0..29999) {
            val r = floats(Unit)
            Assert That r IsAtLeast 1.1f
            Assert That r IsLessThan 5.5f
        }
    }

    @Test fun testRandomAsDoubles() {
        val floats = RANDOM.asDoubles(1.1, 5.5)
        for (i in 0..29999) {
            val r = floats(Unit)
            Assert That r IsAtLeast 1.1
            Assert That r IsLessThan 5.5
        }
    }

    // can not test random points and colors here - it is android stuff...
}