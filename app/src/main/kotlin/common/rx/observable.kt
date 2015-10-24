package common.rx

import rx.Observable
import rx.Single
import rx.Subscriber
import rx.subscriptions.Subscriptions

fun <T> Subscriber<in T>.emit(t: T): Unit =
    ifNotUnsubscribed {
      onNext(t)
    }

fun <T> Subscriber<in T>.last(t: T): Unit =
    ifNotUnsubscribed {
      onNext(t)
      onCompleted()
    }

fun <T> Subscriber<in T>.error(e: Throwable): Unit =
    ifNotUnsubscribed {
      onError(e)
    }

fun <T> Subscriber<in T>.complete(): Unit =
    ifNotUnsubscribed {
      onCompleted()
    }

inline fun <T> Subscriber<in T>.ifNotUnsubscribed(f: () -> Unit): Unit =
    when {
      isUnsubscribed -> Unit
      else           -> f()
    }

fun <T> Subscriber<in T>.finally(f: () -> Unit): Unit =
    add(Subscriptions.create(f))

operator fun <T> Observable<T>.plus(that: Observable<T>): Observable<T> =
    Observable.concat(this, that)

fun <T> Observable<Observable<T>>.flatten() = Observable.merge(this)

inline fun <A, B : Any> Observable<A>.filterMap(crossinline f: (A) -> B?): Observable<B> =
    flatMap {
      val b = f(it)
      when (b) {
        null -> Observable.empty<B>()
        else -> Observable.just(b)
      }
    }

inline fun <A, B> Observable<A>.flatMapSingle(crossinline f: (A) -> Single<B>): Observable<B> =
    flatMap { f(it).toObservable() }