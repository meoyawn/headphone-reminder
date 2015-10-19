package adeln.boilerplate

import android.app.Application

class TestApp : Application() {
  override fun onCreate() {
    super.onCreate()
    App.app = this
  }
}