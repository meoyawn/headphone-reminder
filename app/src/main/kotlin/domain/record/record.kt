package domain.record

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource
import common.rx.finally
import rx.Observable
import rx.Subscriber
import rx.schedulers.Schedulers
import timber.log.Timber

private val channel = AudioFormat.CHANNEL_IN_MONO
private val format = AudioFormat.ENCODING_PCM_16BIT

val blockSize = 512
val frequency = 16384

val sampleBits = 16
val channels: Short = 1

data class RecordBuffer(val buffer: ShortArray, var read: Int)

private fun RecordBuffer.read(ar: AudioRecorder): RecordBuffer =
    apply { read = ar.read(buffer, 0, blockSize) }

fun audioRecord(): Observable<RecordBuffer> =
    Observable.create(startRecording { AndroidAudioRecorder() })
        .retry(3)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())

interface AudioRecorder {
  fun isInitialized(): Boolean
  fun isRecording(): Boolean
  fun startRecording(): Unit
  fun release(): Unit
  fun read(sa: ShortArray, offset: Int, size: Int): Int
}

fun AndroidAudioRecorder(): AndroidAudioRecorder {
  val bufferSize = AudioRecord.getMinBufferSize(frequency, channel, format)
  Timber.d("buffer size $bufferSize")
  return AndroidAudioRecorder(AudioRecord(AudioSource.MIC, frequency, channel, format, bufferSize))
}

class AndroidAudioRecorder(val ar: AudioRecord) : AudioRecorder {
  override fun isInitialized(): Boolean =
      ar.state == AudioRecord.STATE_INITIALIZED

  override fun isRecording(): Boolean =
      ar.recordingState == AudioRecord.RECORDSTATE_RECORDING

  override fun read(sa: ShortArray, offset: Int, size: Int): Int =
      ar.read(sa, offset, size)

  override fun release(): Unit =
      ar.release()

  override fun startRecording(): Unit =
      ar.startRecording()
}

class FailedToInitialize(msg: String) : IllegalStateException(msg)
class FailedToStart(msg: String) : IllegalStateException(msg)

inline fun startRecording(crossinline lazy: () -> AudioRecorder) =
    { sub: Subscriber<in RecordBuffer> ->
      val ar = lazy()
      sub.finally { ar.release() }

      if (ar.isInitialized()) {
        ar.startRecording()
        if (ar.isRecording()) {
          readLoop(sub, RecordBuffer(ShortArray(blockSize), 0), ar)
        } else {
          sub.onError(FailedToStart("could not start the record"))
        }
      } else {
        sub.onError(FailedToInitialize("failed to initialize $ar"))
      }
    }

tailrec fun readLoop(sub: Subscriber<in RecordBuffer>, buf: RecordBuffer, ar: AudioRecorder) {
  if (!sub.isUnsubscribed) {
    val read = buf.read(ar)
    sub.onNext(read)
    readLoop(sub, buf, ar)
  }
}
