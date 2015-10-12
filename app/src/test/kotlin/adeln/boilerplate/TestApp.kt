package adeln.boilerplate

import com.squareup.leakcanary.RefWatcher

class TestApp : App() {
  override fun installLeakCanary(): RefWatcher? = null
}