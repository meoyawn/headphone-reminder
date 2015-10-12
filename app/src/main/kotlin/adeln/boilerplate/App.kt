package adeln.boilerplate

import android.app.Application
import android.os.StrictMode
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import common.android.vmPolicy
import common.thread.threadPolicy
import timber.log.Timber
import kotlin.properties.Delegates

// open for subclassing the test application
open class App : Application() {
  companion object {
    var app: Application by Delegates.notNull()
  }

  override fun onCreate(): Unit {
    app = this
    super.onCreate()

    if (BuildConfig.DEBUG) {
      StrictMode.setVmPolicy(vmPolicy())
      StrictMode.setThreadPolicy(threadPolicy())
    }

    installLeakCanary()
    Timber.plant(Timber.DebugTree())
  }

  // open for not invoking in tests
  open fun installLeakCanary(): RefWatcher? =
      LeakCanary.install(this)
}
