package common.rx

import rx.Observable
import rx.Single
import rx.SingleSubscriber
import rx.subscriptions.Subscriptions

fun <T> SingleSubscriber<T>.finally(function: () -> Unit): Unit =
    add(Subscriptions.create(function))

fun <T> SingleSubscriber<T>.error(e: Throwable): Unit =
    if (!isUnsubscribed) {
      onError(e)
    }

fun <T> SingleSubscriber<T>.success(t: T): Unit =
    if (!isUnsubscribed) {
      onSuccess(t)
    }

fun <T> Single<T>.takeUntil(other: Observable<*>): Observable<T> =
    toObservable().takeUntil(other)