package pl.mareklangiewicz.myutils

import io.reactivex.Observable
import io.reactivex.Observable.merge
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import pl.mareklangiewicz.pue.IPushee

/**
 * Created by Marek Langiewicz on 16.02.16.
 */

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}

fun Disposable.addTo(subscription: CompositeDisposable) = subscription.add(this)

fun <T> Observable<T>.lsubscribe(
        log: IPushee<MyLogEntry>,
        logOnError: String? = "error %s",
        logOnCompleted: String? = "completed",
        onNext: (T) -> Unit
) = subscribe(
        onNext,
        { error: Throwable -> logOnError?.let { log.e(it.format(error.message ?: ""), "ML", error) } },
        { logOnCompleted?.let { log.v(it) } }
)

fun <T> Observable<T>.lsubscribe(
        log: IPushee<MyLogEntry>,
        logOnError: String? = "error %s",
        logOnCompleted: String? = "completed",
        logOnNext: String? = "next %s"
) = lsubscribe(log, logOnError, logOnCompleted) { item -> logOnNext?.let { log.v(it.format(item)) } }


data class RxIf<T>(
        val source: Observable<T>,
        val condition: (T) -> Boolean
)

fun <T> Observable<T>.rxIf(condition: (T) -> Boolean) = RxIf(this, condition)

data class RxThen<T, R>(
        val source: Observable<T>,
        val condition: (T) -> Boolean,
        val onTrue: Observable<T>.() -> Observable<R>
)

infix fun <T, R> RxIf<T>.rxThen(onTrue: Observable<T>.() -> Observable<R>) =
        RxThen(source, condition, onTrue)

infix fun <T, R> RxThen<T, R>.rxElse(onFalse: Observable<T>.() -> Observable<R>) =
        source.publish {
            merge(it.filter { condition(it) }.onTrue(), it.filter { !condition(it) }.onFalse())
        }

private data class RxCase<T, R>(
        val condition: (T) -> Boolean,
        val transform: Observable<T>.() -> Observable<R>
)

class RxSwitch<T, R> {

    private val cases: MutableList<RxCase<T, R>> = mutableListOf()

    fun case(condition: (T) -> Boolean, transform: Observable<T>.() -> Observable<R>) =
            cases.add(RxCase(condition, transform))

    operator fun invoke(source: Observable<T>): Observable<R> = source.publish {
        val streams = cases.mapIndexed { idx, (condition, transform) ->
            val fullCondition = { item: T ->
                condition(item) && cases.take(idx).all { !it.condition(item) }
            }
            source.filter { fullCondition(it) }.transform()
        }
        merge(streams)
    }
}

fun <T, R> Observable<T>.switch(body: RxSwitch<T, R>.() -> Unit) =
        RxSwitch<T, R>().apply(body)(this)

