package adeln.boilerplate

import android.app.Service
import android.content.Intent
import android.os.IBinder
import common.audio.isMusicActiveAndNotLoud
import common.context.audios
import timber.log.Timber

class CheckerService : Service() {
  override fun onBind(intent: Intent): IBinder? = null
  override fun onCreate() {
    super.onCreate()
    Timber.d("checking ${audios().isMusicActiveAndNotLoud()}")
    stopSelf()
  }
}