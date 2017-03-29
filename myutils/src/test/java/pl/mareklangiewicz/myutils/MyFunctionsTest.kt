package pl.mareklangiewicz.myutils

import org.junit.Test


class MyFunctionsTest {

    @Test
    fun testConfusingTernaryOperator() {
        assert that ((1 < 2) % 7 ?: 8) isEqualTo 7
        assert that ((1 > 2) % 7 ?: 8) isEqualTo 8
    }
}