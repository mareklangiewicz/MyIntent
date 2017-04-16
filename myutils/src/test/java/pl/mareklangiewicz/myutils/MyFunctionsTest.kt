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

    @Test
    fun testMemoize() {
        var x = 1
        val list = mutableListOf(x)
        val addNext: () -> Unit = { list += ++x }
        addNext()
        Assert That list IsEqualTo listOf(1, 2)
        addNext()
        Assert That list IsEqualTo listOf(1, 2, 3)
        val addNexMemoized = memoize(addNext)
        addNexMemoized()
        Assert That list IsEqualTo listOf(1, 2, 3, 4)
        addNexMemoized()
        Assert That list IsEqualTo listOf(1, 2, 3, 4) // no change here
        addNexMemoized()
        Assert That list IsEqualTo listOf(1, 2, 3, 4) // no change here
        addNext()
        Assert That list IsEqualTo listOf(1, 2, 3, 4, 5)
    }

    @Test
    fun testMemoize1Arg() {
        var x = 1
        val list = mutableListOf(x)
        val addNext: (Unit) -> Unit = { list += ++x }
        addNext(Unit)
        Assert That list IsEqualTo listOf(1, 2)
        addNext(Unit)
        Assert That list IsEqualTo listOf(1, 2, 3)
        val addNexMemoized = memoize(addNext)
        addNexMemoized(Unit)
        Assert That list IsEqualTo listOf(1, 2, 3, 4)
        addNexMemoized(Unit)
        Assert That list IsEqualTo listOf(1, 2, 3, 4) // no change here
        addNexMemoized(Unit)
        Assert That list IsEqualTo listOf(1, 2, 3, 4) // no change here
        addNext(Unit)
        Assert That list IsEqualTo listOf(1, 2, 3, 4, 5)
    }
}