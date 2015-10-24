package common.rx

import rx.Observable
import rx.Single
import rx.SingleSubscriber
import rx.subscriptions.Subscriptions

fun <T> SingleSubscriber<T>.finally(function: () -> Unit): Unit =
    add(Subscriptions.create(function))

fun <T> SingleSubscriber<T>.success(t: T): Unit =
    ifNotUnsubscribed { onSuccess(t) }

fun <T> SingleSubscriber<T>.error(e: Throwable): Unit =
    ifNotUnsubscribed { onError(e) }

inline fun <T> SingleSubscriber<in T>.ifNotUnsubscribed(f: () -> Unit): Unit =
    when {
      isUnsubscribed -> Unit
      else           -> f()
    }

fun <T> Single<T>.takeUntil(other: Observable<*>): Observable<T> =
    toObservable().takeUntil(other)