package common.kotlin

inline fun <T : Any> T?.notNull(f: (T) -> Unit): Unit =
    if (this != null) f(this)