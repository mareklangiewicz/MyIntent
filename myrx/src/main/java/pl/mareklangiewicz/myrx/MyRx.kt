package pl.mareklangiewicz.myrx

import pl.mareklangiewicz.myutils.IMyLogger
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.subscriptions.CompositeSubscription

/**
 * Created by Marek Langiewicz on 16.02.16.
 */

operator fun CompositeSubscription.plusAssign(subscription: Subscription) = add(subscription)

inline fun <T> Observable<T>.lsubscribe(
        log: IMyLogger,
        logOnError: String? = "error %s",
        logOnCompleted: String? = "completed",
        crossinline onNext: (T) -> Unit
): Subscription {
    return subscribe(
            object : Observer<T> {
                override fun onError(e: Throwable?) { logOnError?.let { log.e(it.format(e?.message ?: ""), e) } }
                override fun onCompleted() { logOnCompleted?.let { log.v(it) } }
                override fun onNext(item: T) { onNext(item) }
            }
    )
}

fun <T> Observable<T>.lsubscribe(
        log: IMyLogger,
        logOnError: String? = "error %s",
        logOnCompleted: String? = "completed",
        logOnNext: String? = "next %s"
): Subscription = lsubscribe(log, logOnError, logOnCompleted) { item -> logOnNext?.let { log.v(it.format(item)) } }

