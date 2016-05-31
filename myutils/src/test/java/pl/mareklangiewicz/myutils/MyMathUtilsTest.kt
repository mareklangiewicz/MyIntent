package pl.mareklangiewicz.myutils

import org.junit.After
import org.junit.Before
import org.junit.Test

import com.google.common.truth.Truth.assertThat

/**
 * Created by Marek Langiewicz on 04.11.15.
 */
class MyMathUtilsTest {

    private val log = MySystemLogger()

    @Before fun setUp() { }
    @After fun tearDown() { }

    @Test fun testScale0d() { }

    @Test fun testScale1d() { }

    @Test fun testScale2d() { }

    @Test fun testScale2d1() { }

    @Test fun testGetRandomInt() {
        for (i in 0..29999) {
            val r = RANDOM.nextInt(1, 5)
//            log.v("getRandomInt(1, 5): ${r.str}");
            assertThat(r).isAtLeast(1)
            assertThat(r).isLessThan(5)
        }
        for (i in 0..29999) {
            val r = RANDOM.nextInt(-10000, 0)
//            log.v("getRandomInt(-10000, 0): ${r.str}");
            assertThat(r).isAtLeast(-10000)
            assertThat(r).isLessThan(0)
        }
    }

    @Test fun testGetRandomPoint() { }

    @Test fun testScale0d1() { }

    @Test fun testScale1d1() { }

    @Test fun testScale2d2() { }

    @Test fun testScale2d3() { }

    @Test fun testGetRandomFloat() { }

    @Test fun testGetRandomPointF() { }

    @Test fun testGetRandomColor() { }

}