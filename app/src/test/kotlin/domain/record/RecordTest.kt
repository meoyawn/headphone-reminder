package domain.record

import common.stream
import okio.BufferedSource
import okio.Okio
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import rx.Observable
import rx.observers.TestSubscriber
import java.io.InputStream

@RunWith(RobolectricTestRunner::class)
class RecordTest {
  @Test fun testRecording() {
    val ts = TestSubscriber<RecordBuffer>()
    val tar = TestAudioRecorder(stream("robolectric.properties"), true, true)
    val cnt = 100
    Observable.create(startRecording { tar })
        .take(cnt)
        .subscribe(ts)

    ts.assertCompleted()
    ts.assertValueCount(cnt)
    assertThat(tar.released).isTrue()
  }

  @Test fun testInitError() {
    val ts = TestSubscriber<RecordBuffer>()
    val tar = TestAudioRecorder(stream("robolectric.properties"), false, true)
    Observable.create(startRecording { tar }).subscribe(ts)

    ts.assertError(FailedToInitializeException::class.java)
    assertThat(tar.triedToStart).isFalse()
    assertThat(tar.released).isTrue()
  }

  @Test fun testStartError() {
    val ts = TestSubscriber<RecordBuffer>()
    val tar = TestAudioRecorder(stream("robolectric.properties"), true, false)
    Observable.create(startRecording { tar }).subscribe(ts)

    ts.assertError(FailedToStartException::class.java)
    assertThat(tar.triedToStart).isTrue()
    assertThat(tar.released).isTrue()
  }
}

fun TestAudioRecorder(stream: InputStream,
                      initialized: Boolean,
                      recording: Boolean): TestAudioRecorder =
    TestAudioRecorder(Okio.buffer(Okio.source(stream)), initialized, recording)

class TestAudioRecorder(val bs: BufferedSource, val initialized: Boolean, val recording: Boolean) : AudioRecorder {
  var triedToStart = false
  var released = false

  override fun isInitialized(): Boolean = initialized
  override fun isRecording(): Boolean = recording
  override fun read(sa: ShortArray, offset: Int, size: Int): Int = readLoop(sa, size, 0)
  override fun release() {
    bs.close()
    released = true
  }

  override fun startRecording() {
    triedToStart = true
  }

  private tailrec fun readLoop(sa: ShortArray, size: Int, i: Int): Int =
      if (i < size) {
        sa[i] = bs.readShort()
        readLoop(sa, size, i + 1)
      } else i
}