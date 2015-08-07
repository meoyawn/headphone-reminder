package adeln.boilerplate

import android.support.test.rule.ActivityTestRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

public class FailingTest : AndroidTest() {
  public @Rule val rule: ActivityTestRule<MainActivity> = ActivityTestRule(javaClass<MainActivity>())

  public @Test fun testFail() {
    Assert.fail()
  }
}