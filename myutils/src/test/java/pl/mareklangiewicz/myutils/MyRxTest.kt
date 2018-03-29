package pl.mareklangiewicz.myutils

import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import junit.framework.Assert.assertEquals
import org.junit.Test

class MyRxTest {

    @Test
    fun testRxIf() {
        val source = Observable.fromIterable(List(100) { it })

        val ifObservable = source.rxIf { it % 2 == 0 } rxThen {
            map { it to 1 }
        } rxElse {
            rxIf { it % 3 == 0 } rxThen {
                map { it to 2 }
            } rxElse {
                rxIf { it % 5 == 0 } rxThen {
                    map { it to 3 }
                } rxElse {
                    map { it to 4 }
                }
            }
        }

        val ifObserver = TestObserver<Pair<Int, Int>>()
        ifObservable.subscribe(ifObserver)

        ifObserver.values().forEach { (value, cat) ->
            when {
                value % 2 == 0 -> assertEquals(1, cat)
                value % 3 == 0 -> assertEquals(2, cat)
                value % 5 == 0 -> assertEquals(3, cat)
                else -> assertEquals(4, cat)
            }
        }
    }

    @Test
    fun testRxSwitch() {
        val source = Observable.fromIterable(List(100) { it })

        val switchObservable = source.switch<Int, Pair<Int, Int>> {
            case({ it % 2 == 0 }) { map { it to 1 } }
            case({ it % 3 == 0 }) { map { it to 2 } }
            case({ it % 5 == 0 }) { map { it to 3 } }
            case({ true }) { map { it to 4 } }
        }

        val switchObserver = TestObserver<Pair<Int, Int>>()
        switchObservable.subscribe(switchObserver)

        switchObserver.values().forEach { (value, cat) ->
            when {
                value % 2 == 0 -> assertEquals(1, cat)
                value % 3 == 0 -> assertEquals(2, cat)
                value % 5 == 0 -> assertEquals(3, cat)
                else -> assertEquals(4, cat)
            }
        }
    }
}

