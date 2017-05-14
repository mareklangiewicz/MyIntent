package pl.mareklangiewicz.myutils

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.junit.Test
import kotlin.coroutines.experimental.suspendCoroutine


class CoroutinesTest {

    private val exscheduler = ExecutorScheduler()
    private val intervals = { _: Unit -> 300L }.vntake(1)
    private val timer = Timer(exscheduler, intervals).autoStart()
    private val npushee = { i: Long? -> println(i) }

    @Test
    fun test1() {
        launch(CommonPool) {
            delay(1000L)
            println("World!")
        }
        println("Hello,")
        Thread.sleep(2000L)
    }

    @Test
    fun testTimer() {
        timer(npushee)
        Thread.sleep(2000L)
    }

    @Test
    fun testAwaitForOnePush() {
        launch(CommonPool) {
            println("inside: before await")
            val value = timer.awaitForOnePush()
            println("inside: after await: value = $value")
        }
        println("outside: before sleeping")
        Thread.sleep(2000L)
        println("outside: after sleeping")
    }
}

// TODO LATER: test this autoStart function and move it to Pue.kt
fun <T> IPusher<T, ICommand>.autoStart() = object : IPusher<T, ICommand> {
    override fun invoke(pushee: IPushee<T>): IPushee<ICommand> = this@autoStart(pushee).also { it(Start) }
}

// experiment - no idea if it makes any sense.. (yet)
// note: this only waits for one push - the second one would call cont.resume again and it should crash
suspend fun <T> IPusher<T, Nothing>.awaitForOnePush(): T = suspendCoroutine { cont -> this { cont.resume(it) } }
