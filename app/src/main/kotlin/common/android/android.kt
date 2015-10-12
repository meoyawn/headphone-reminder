package common.android

import android.os.Build
import android.os.StrictMode

fun vmPolicy(): StrictMode.VmPolicy =
    StrictMode.VmPolicy.Builder()
        .detectLeakedClosableObjects()
        .apiLevel(16) {
          detectLeakedRegistrationObjects()
        }
        .apiLevel(18) {
          detectFileUriExposure()
        }
        .detectLeakedSqlLiteObjects()
        .penaltyLog()
        .penaltyDeath()
        .build()

inline fun <T> T.apiLevel(level: Int, f: T.() -> Unit): T =
    when {
      Build.VERSION.SDK_INT >= level -> apply(f)
      else                           -> this
    }