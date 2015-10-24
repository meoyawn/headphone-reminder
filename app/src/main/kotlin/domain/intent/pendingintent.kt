package domain.intent

import android.app.PendingIntent
import android.content.Context

fun pendingCheckerService(c: Context, flag: Int): PendingIntent? =
    PendingIntent.getService(c, 0, checkerServiceIntent(c), flag)

fun pendingRecorder(c: Context): PendingIntent =
    mainActivityIntent(c, LaunchSource.RECORD_SHORTCUT).let {
      PendingIntent.getActivity(c, 0, it, 0)
    }