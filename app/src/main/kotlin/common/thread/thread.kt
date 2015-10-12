package common.thread

import android.os.Looper
import android.os.StrictMode

fun isMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()
fun assertWorkerThread(): Unit = if (isMainThread()) throw exception()
fun assertMainThread(): Unit = if (!isMainThread()) throw exception()
private fun exception(): RuntimeException = RuntimeException("wrong thread, buddy")

fun threadPolicy(): StrictMode.ThreadPolicy =
    StrictMode.ThreadPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .penaltyDeath()
        .build()

fun vmPolicy(): StrictMode.VmPolicy =
    StrictMode.VmPolicy.Builder()
        .detectLeakedClosableObjects()
        .detectFileUriExposure()
        .detectLeakedRegistrationObjects()
        .detectLeakedSqlLiteObjects()
        .penaltyLog()
        .penaltyDeath()
        .build()