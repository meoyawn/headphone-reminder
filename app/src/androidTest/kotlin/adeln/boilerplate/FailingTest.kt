package adeln.boilerplate

import android.support.test.rule.ActivityTestRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class FailingTest : AndroidTest() {
  @Rule val rule = ActivityTestRule(javaClass<MainActivity>())

  @Test fun testFail() {
    Assert.fail()
  }
}