package common.either

import timber.log.Timber
import java.io.IOException

inline fun <reified E : Exception, R> tryCatch(f: () -> R, err: (E) -> R): R =
    try {
      f()
    } catch (e: E) {
      if (e is E) {
        err(e)
      } else {
        throw e
      }
    }

inline fun <reified E : Exception> tryCatchUnit(f: () -> Unit): Unit =
    tryCatch<E, Unit>(f, { Timber.e("caught", it) })

inline fun <reified E : Exception, R> tryEither(f: () -> R): Either<E, R> =
    tryCatch({ Right(f()) }, { e: E -> Left(e) })

inline fun <A> tryIo(f: () -> A): Either<IOException, A> =
    tryEither(f)

inline fun <A> tryException(f: () -> A): Either<Exception, A> =
    tryEither(f)

inline fun <A> catch(lazy: () -> A): Exception? =
    tryException(lazy).fold({ it.value }, { null })
