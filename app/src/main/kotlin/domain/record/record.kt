package domain.record

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource
import common.rx.finally
import common.thread.assertWorkerThread
import rx.Observable
import rx.Subscriber
import rx.schedulers.Schedulers

val blockSize = 256

private val frequency = 8000
private val channel = AudioFormat.CHANNEL_IN_MONO
private val format = AudioFormat.ENCODING_PCM_16BIT

data class RecordBuffer(val buffer: ShortArray, var read: Int)

private fun RecordBuffer.read(ar: AudioRecord): RecordBuffer =
    apply { read = ar.read(buffer, 0, blockSize) }

fun audioRecord(): Observable<RecordBuffer> =
    Observable.create(startRecording())
        .retry(3)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())

private tailrec fun read(sub: Subscriber<in RecordBuffer>, buf: RecordBuffer, ar: AudioRecord) {
  if (!sub.isUnsubscribed) {
    val read = buf.read(ar)
    sub.onNext(read)
    read(sub, buf, ar)
  }
}

private fun AudioRecord.isInitialized(): Boolean =
    state == AudioRecord.STATE_INITIALIZED

private fun AudioRecord.isRecording(): Boolean =
    recordingState == AudioRecord.RECORDSTATE_RECORDING

private fun startRecording() = { sub: Subscriber<in RecordBuffer> ->
  assertWorkerThread()

  val bufferSize = AudioRecord.getMinBufferSize(frequency, channel, format)
  val ar = AudioRecord(AudioSource.MIC, frequency, channel, format, bufferSize)
  sub.finally {
    assertWorkerThread()
    if (ar.isInitialized()) {
      ar.release()
    }
  }

  if (ar.isInitialized()) {
    ar.startRecording()
    if (ar.isRecording()) {
      read(sub, RecordBuffer(ShortArray(blockSize), 0), ar)
    } else {
      sub.onError(IllegalStateException("could not start the record"))
    }
  } else {
    sub.onError(IllegalStateException("failed to initialize $ar"))
  }
}