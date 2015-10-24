package domain.record

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource
import common.rx.finally
import common.thread.assertWorkerThread
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D
import rx.Observable
import rx.Subscriber
import timber.log.Timber

val blockSize = 256

val frequency = 8000
val channel = AudioFormat.CHANNEL_IN_MONO
val format = AudioFormat.ENCODING_PCM_16BIT
val bufferSize = AudioRecord.getMinBufferSize(frequency, channel, format)
val fft by lazy {
  assertWorkerThread()
  DoubleFFT_1D(blockSize)
}

fun audioRecord(): Observable<DoubleArray> =
    Observable.create { sub: Subscriber<in DoubleArray> ->
      assertWorkerThread()

      val ar = AudioRecord(AudioSource.MIC, frequency, channel, format, bufferSize)

      val buffer = ShortArray(blockSize)
      val data = DoubleArray(blockSize)

      ar.startRecording()
      sub.finally {
        assertWorkerThread()
        ar.release()
      }

      while (!sub.isUnsubscribed) {
        val bufferedReadResult = ar.read(buffer, 0, blockSize)

        var i = 0
        while (i < blockSize && i < bufferedReadResult) {
          data[i] = buffer[i].toDouble() / 32768.0
          i++
        }

        fft.realForward(data)
        sub.onNext(data)
      }
    }