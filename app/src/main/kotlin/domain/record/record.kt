package domain.record

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource
import common.rx.error
import common.rx.finally
import common.thread.assertWorkerThread
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D
import rx.Observable
import rx.Subscriber

private val blockSize = 256

private val frequency = 8000
private val channel = AudioFormat.CHANNEL_IN_MONO
private val format = AudioFormat.ENCODING_PCM_16BIT

private val fft by lazy {
  assertWorkerThread()
  DoubleFFT_1D(blockSize)
}

fun audioRecord(): Observable<DoubleArray> =
    Observable.create<DoubleArray> { record(it) }
        .retry(3)

tailrec private fun fillData(i: Int,
                             bufferedReadResult: Int,
                             data: DoubleArray,
                             buffer: ShortArray) {
  if (i < blockSize && i < bufferedReadResult) {
    data[i] = buffer[i].toDouble() / 32768.0
    fillData(i + 1, bufferedReadResult, data, buffer)
  }
}

tailrec private fun iterate(sub: Subscriber<in DoubleArray>,
                            ar: AudioRecord,
                            buffer: ShortArray,
                            data: DoubleArray) {
  if (!sub.isUnsubscribed) {
    val bufferedReadResult = ar.read(buffer, 0, blockSize)

    fillData(0, bufferedReadResult, data, buffer)

    fft.realForward(data)
    sub.onNext(data)
    iterate(sub, ar, buffer, data)
  }
}

private fun AudioRecord.isInitialized(): Boolean =
    state == AudioRecord.STATE_INITIALIZED

private fun AudioRecord.isRecording(): Boolean =
    recordingState == AudioRecord.RECORDSTATE_RECORDING

private fun record(sub: Subscriber<in DoubleArray>) {
  assertWorkerThread()

  val bufferSize = AudioRecord.getMinBufferSize(frequency, channel, format)
  val ar = AudioRecord(AudioSource.MIC, frequency, channel, format, bufferSize)
  sub.finally {
    if (ar.isInitialized()) {
      assertWorkerThread()
      ar.release()
    }
  }

  if (ar.isInitialized()) {
    val buffer = ShortArray(blockSize)
    val data = DoubleArray(blockSize)
    ar.startRecording()
    if (ar.isRecording()) {
      iterate(sub, ar, buffer, data)
    } else {
      sub.error(IllegalStateException("could not start the record"))
    }
  } else {
    sub.error(IllegalStateException("failed to initialize $ar"))
  }
}