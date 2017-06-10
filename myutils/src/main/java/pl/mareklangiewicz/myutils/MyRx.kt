package pl.mareklangiewicz.myutils

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

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

