package adeln.boilerplate

import android.app.Activity
import android.support.test.rule.ActivityTestRule

class NoAnimationsRule<T : Activity>(cls: Class<T>?) : ActivityTestRule<T>(cls) {
  override fun afterActivityLaunched() {
    Animations.disableAll()
  }

  override fun afterActivityFinished() {
    Animations.enableAll()
  }
}