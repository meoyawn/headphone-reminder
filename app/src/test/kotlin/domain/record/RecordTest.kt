package domain.record

import okio.BufferedSource
import okio.Okio
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import rx.Observable
import rx.observers.TestSubscriber
import java.io.InputStream

class RecordTest {
  @Test fun testRecording() {
    val ts = TestSubscriber<RecordBuffer>()
    val tar = TestAudioRecorder(javaClass.classLoader.getResourceAsStream("fuck"), true, true)
    Observable.create(startRecording { tar })
        .take(300)
        .subscribe(ts)
    ts.assertCompleted()
    ts.assertValueCount(300)
    assertThat(tar.released).isTrue()
  }
}

fun TestAudioRecorder(stream: InputStream,
                      initialized: Boolean,
                      recording: Boolean): TestAudioRecorder =
    TestAudioRecorder(Okio.buffer(Okio.source(stream)), initialized, recording)

class TestAudioRecorder(val bs: BufferedSource, val initialized: Boolean, val recording: Boolean) : AudioRecorder {
  var started = false
  var released = false

  override fun isInitialized(): Boolean = initialized
  override fun isRecording(): Boolean = recording
  override fun read(sa: ShortArray, offset: Int, size: Int): Int = readLoop(sa, size, 0)
  override fun release() {
    bs.close()
    released = true
  }

  override fun startRecording() {
    started = true
  }

  private tailrec fun readLoop(sa: ShortArray, size: Int, i: Int): Int =
      if (i < size) {
        sa[i] = bs.readShort()
        readLoop(sa, size, i + 1)
      } else i
}