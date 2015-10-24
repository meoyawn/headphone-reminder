package common.android

import android.os.Build
import android.os.StrictMode

fun vmPolicy(): StrictMode.VmPolicy =
    StrictMode.VmPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .penaltyDeath()
        .build()

inline fun <T> T.apiLevel(level: Int, f: T.() -> Unit): T =
    when {
      Build.VERSION.SDK_INT >= level -> apply(f)
      else                           -> this
    }