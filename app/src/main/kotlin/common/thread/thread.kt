package common.thread

import android.os.Looper
import android.os.StrictMode

fun isMainThread(): Boolean =
    Looper.myLooper() == Looper.getMainLooper()

fun assertWorkerThread(): Unit =
    when {
      isMainThread() -> error("should be on a background thread")
      else           -> Unit
    }

fun assertMainThread(): Unit =
    when {
      isMainThread() -> Unit
      else           -> error("should be on the main thread")
    }

fun threadPolicy(): StrictMode.ThreadPolicy =
    StrictMode.ThreadPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .penaltyDeath()
        .build()