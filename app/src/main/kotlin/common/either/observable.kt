package common.either

import rx.Observable

data class EitherObservable<A, B>(val o: Observable<Either<A, B>>)

fun <A, B> Observable<Either<A, B>>.lift(): EitherObservable<A, B> =
    EitherObservable(this)

inline fun <A, B, C> EitherObservable<A, B>.map(crossinline f: (B) -> C): EitherObservable<A, C> =
    o.map { it.map(f) }.lift()

inline fun <A, B, C> EitherObservable<A, B>.flatMap(crossinline f: (B) -> EitherObservable<A, C>): EitherObservable<A, C> =
    o.flatMap { it.fold({ Observable.just(it) }, { f(it.value).o }) }.lift()