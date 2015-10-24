package common.kotlin

inline fun <T : Any> T?.notNull(f: (T) -> Unit): Unit =
    when {
      this == null -> Unit
      else         -> f(this)
    }