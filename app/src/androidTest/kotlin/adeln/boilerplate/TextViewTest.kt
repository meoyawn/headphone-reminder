package adeln.boilerplate

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.test.suitebuilder.annotation.LargeTest
import org.junit.Rule
import org.junit.Test

@LargeTest
class TextViewTest : AndroidTest() {
  @Rule fun rule() = NoAnimationsRule(javaClass<MainActivity>())

  @Test fun testText() {
    onView(withId(R.id.text)).check(matches(isDisplayed()))
  }
}