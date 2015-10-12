package common.view

import android.view.View
import android.widget.FrameLayout
import common.robolectric
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RecyclerViewTest {
  @Test fun testClicks() {
    var clicks = 0
    val v = View(robolectric())
    val a = RecyclerViewAdapter({ 1 },
                                { 1 },
                                { p, type -> v },
                                { vh, pos -> },
                                { clicks++ })
    a.onCreateViewHolder(FrameLayout(robolectric()), 1)
    v.performClick()
    assertThat(clicks).isEqualTo(1)
    v.performClick()
    v.performClick()
    assertThat(clicks).isEqualTo(3)
  }

  @Test fun testBinds() {
    var binds = 0
    val a = RecyclerViewAdapter({ 1 },
                                { 1 },
                                { p, type -> View(robolectric()) },
                                { vh, pos -> binds++ },
                                { })
    val vh = a.onCreateViewHolder(FrameLayout(robolectric()), 1)
    a.onBindViewHolder(vh, 1)
    assertThat(binds).isEqualTo(1)
    a.onBindViewHolder(vh, 2)
    a.onBindViewHolder(vh, 3)
    assertThat(binds).isEqualTo(3)
  }

  @Test fun testInflates() {

  }
}