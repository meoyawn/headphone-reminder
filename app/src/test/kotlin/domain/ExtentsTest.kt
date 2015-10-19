package domain

import common.robolectric
import domain.intent.LaunchSource
import domain.intent.mainActivityExtent
import domain.intent.mainActivityIntent
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExtentsTest {
  @Test fun testMainActivity() {
    LaunchSource.values().forEach {
      assertThat(mainActivityExtent(mainActivityIntent(robolectric, it))).isEqualTo(it)
    }
  }
}