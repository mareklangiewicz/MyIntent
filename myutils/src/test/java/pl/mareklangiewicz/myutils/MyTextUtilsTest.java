package pl.mareklangiewicz.myutils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Marek Langiewicz on 01.10.15.
 */
public class MyTextUtilsTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testStr() throws Exception {
        List<String> list = Arrays.asList("bla", "ble");
        System.out.println(MyTextUtils.str(list));
    }

    @Test
    public void testToShortStr() throws Exception { //TODO SOMEDAY
    }


    @Test
    public void testToLongStr() throws Exception { //TODO SOMEDAY
    }

    @Test
    public void testToVeryLongStr() throws Exception { //TODO SOMEDAY
    }
}

