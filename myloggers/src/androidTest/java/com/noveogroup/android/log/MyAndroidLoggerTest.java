package com.noveogroup.android.log;

import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Marek Langiewicz on 17.02.16.
 */
@RunWith(AndroidJUnit4.class)
public class MyAndroidLoggerTest {

    @Test
    public void testLog() throws Exception {
        MyAndroidLogger logger = new MyAndroidLogger("TestLogger");
        logger.print(Logger.Level.DEBUG, "some debug message", null);
    }
}