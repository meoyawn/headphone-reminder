package adeln.boilerplate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import domain.setIfNeeded

class BootCompleteReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    setIfNeeded(context)
  }
}