package domain

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.SystemClock
import common.context.alarms
import domain.intent.pendingCheckerService
import timber.log.Timber
import java.util.concurrent.TimeUnit


fun setIfNeeded(c: Context): Unit =
    if (alarmIsSet(c)) {
      Timber.d("alarm already set")
    } else {
      val fifteen = TimeUnit.SECONDS.toMillis(5)
      c.alarms().setInexactRepeating(
          AlarmManager.ELAPSED_REALTIME,
          SystemClock.elapsedRealtime() + fifteen,
          fifteen,
          pendingCheckerService(c, 0))
      Timber.d("fresh alarm set")
    }

fun alarmIsSet(c: Context): Boolean =
    pendingCheckerService(c, PendingIntent.FLAG_NO_CREATE) != null