package pl.mareklangiewicz.myutils;

import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.MyHandler;
import com.noveogroup.android.log.MyLogger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by Marek Langiewicz on 04.11.15.
 */
public class MyMathUtilsTest {

    private static final MyLogger log = new MyLogger("UT");

    @Before
    public void setUp() throws Exception {
        MyHandler.sPrintLnLevel = Logger.Level.VERBOSE;
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testScale0d() throws Exception {

    }

    @Test
    public void testScale1d() throws Exception {

    }

    @Test
    public void testScale2d() throws Exception {

    }

    @Test
    public void testScale2d1() throws Exception {

    }

    @Test
    public void testGetRandomInt() throws Exception {
        for(int i = 0; i < 30000; ++i) {
            int r = MyMathUtils.getRandomInt(1, 5);
//            log.v("getRandomInt(1, 5): %d", r);
            assertThat(r).isAtLeast(1);
            assertThat(r).isLessThan(5);
        }
        for(int i = 0; i < 30000; ++i) {
            int r = MyMathUtils.getRandomInt(-10000, 0);
//            log.v("getRandomInt(-10000, 0): %d", r);
            assertThat(r).isAtLeast(-10000);
            assertThat(r).isLessThan(0);
        }
    }

    @Test
    public void testGetRandomPoint() throws Exception {

    }

    @Test
    public void testScale0d1() throws Exception {

    }

    @Test
    public void testScale1d1() throws Exception {

    }

    @Test
    public void testScale2d2() throws Exception {

    }

    @Test
    public void testScale2d3() throws Exception {

    }

    @Test
    public void testGetRandomFloat() throws Exception {

    }

    @Test
    public void testGetRandomPointF() throws Exception {

    }

    @Test
    public void testGetRandomColor() throws Exception {

    }
}