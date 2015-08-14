package adeln.boilerplate

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.SmallTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class TextViewTest {
  @Rule fun rule() = NoAnimationsRule(javaClass<MainActivity>())

  @Test fun testText() {
    onView(withId(R.id.text)).check(matches(isDisplayed()))
  }
}