package adeln.boilerplate

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.test.suitebuilder.annotation.LargeTest
import org.junit.Rule
import org.junit.Test

@LargeTest
class TextViewTest : AndroidTest() {
  @Rule fun rule() = ActivityTestRule(javaClass<MainActivity>())

  @Test fun testText() {
    onView(withText("Fuck")).check(matches(isDisplayed()))
  }
}