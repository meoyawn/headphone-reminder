package adeln.boilerplate

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner

@RunWith(RobolectricGradleTestRunner::class)
class MainActivityTest {
  @Test fun testPass() {
    assertThat(2).isEqualTo(1 + 1)
  }
}