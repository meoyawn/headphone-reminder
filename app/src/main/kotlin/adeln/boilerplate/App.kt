package adeln.boilerplate

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.media.AudioManager

class App : Application() {
  override fun onCreate() {
    super.onCreate()
    val audio = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
  }
}
