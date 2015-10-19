package adeln.boilerplate

import android.app.Application
import android.os.StrictMode
import com.squareup.leakcanary.LeakCanary
import common.android.vmPolicy
import common.thread.threadPolicy
import timber.log.Timber
import kotlin.properties.Delegates

// open for subclassing the test application
class App : Application() {
  companion object {
    var app: Application by Delegates.notNull()
  }

  override fun onCreate(): Unit {
    super.onCreate()
    app = this

    if (BuildConfig.DEBUG) {
      StrictMode.setVmPolicy(vmPolicy())
      StrictMode.setThreadPolicy(threadPolicy())
    }

    LeakCanary.install(this)
    Timber.plant(Timber.DebugTree())
  }
}
