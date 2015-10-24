package common.either

interface Either<out A, out B>
data class Left<A>(val value: A) : Either<A, Nothing>
data class Right<B>(val value: B) : Either<Nothing, B>

fun Either<Any, Any>.isLeft() = this is Left
fun Either<Any, Any>.isRight() = this is Right

inline fun <A, B, C> Either<A, B>.fold(l: (Left<A>) -> C, r: (Right<B>) -> C): C =
    when (this) {
      is Left<A>  -> l(this)
      is Right<B> -> r(this)
      else        -> error("exhaust")
    }

inline fun <A, B, C> Either<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> =
    fold({ it }, { f(it.value) })

inline fun <A, B, C> Either<A, B>.flatMapLeft(f: (A) -> Either<C, B>): Either<C, B> =
    fold({ f(it.value) }, { it })

inline fun <A, B, C> Either<A, B>.map(f: (B) -> C): Either<A, C> =
    flatMap { Right(f(it)) }

inline fun <A, B, C> Either<A, B>.mapLeft(f: (A) -> C): Either<C, B> =
    flatMapLeft { Left(f(it)) }

fun <A, B> Either<A, B>.nullable(): B? =
    fold({ null }, { it.value })