package domain

import adeln.boilerplate.R
import android.content.Context
import android.content.Intent
import domain.intent.LaunchSource
import domain.intent.mainActivityIntent

val INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT"

fun installRecorderShortcut(c: Context): Unit {
  val recorder = mainActivityIntent(c, LaunchSource.RECORD_SHORTCUT)
  val iconRes = Intent.ShortcutIconResource.fromContext(c, R.mipmap.ic_launcher)
  val intent = Intent(INSTALL_SHORTCUT)
      .putExtra(Intent.EXTRA_SHORTCUT_INTENT, recorder)
      .putExtra(Intent.EXTRA_SHORTCUT_NAME, "Test")
      .putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes)
      .putExtra("duplicate", false)
  c.sendBroadcast(intent)
}