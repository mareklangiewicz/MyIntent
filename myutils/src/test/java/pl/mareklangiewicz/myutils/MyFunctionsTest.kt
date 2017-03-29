package pl.mareklangiewicz.myutils

import org.junit.Test
import pl.mareklangiewicz.mytests.*


class MyFunctionsTest {

    @Suppress("DEPRECATION")
    @Test
    fun testConfusingTernaryOperator() {
        val i = true  % 7 ?: 8
        val j = false % 7 ?: 8
        Assert That i IsEqualTo 7
        Assert That j IsEqualTo 8
    }
}