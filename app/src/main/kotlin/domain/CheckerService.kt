package domain

import android.app.Service
import android.content.Intent
import android.os.IBinder

class CheckerService : Service() {
  override fun onBind(intent: Intent): IBinder? = null
  override fun onCreate() {
    super.onCreate()
    stopSelf()
  }
}