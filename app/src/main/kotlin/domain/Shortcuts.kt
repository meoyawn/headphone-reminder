package domain

import android.content.Context
import android.content.Intent

object Shortcuts {
  fun install(c: Context): Unit {
    val recorder = Intent(c, RecorderActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    //    val iconRes = Intent.ShortcutIconResource.fromContext(c, R.mipmap.recorder)
    val intent = Intent("com.android.launcher.action.INSTALL_SHORTCUT")
        .putExtra(Intent.EXTRA_SHORTCUT_INTENT, recorder)
        .putExtra(Intent.EXTRA_SHORTCUT_NAME, "Test")
        //        .putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes)
        .putExtra("duplicate", false)
    c.sendBroadcast(intent)
  }
}