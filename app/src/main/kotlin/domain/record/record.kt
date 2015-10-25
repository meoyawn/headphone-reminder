package domain.record

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource
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
    Observable.create(record())
        .retry(3)

private tailrec fun fillData(i: Int,
                             bufferedReadResult: Int,
                             data: DoubleArray,
                             buffer: ShortArray) {
  if (i < blockSize && i < bufferedReadResult) {
    data[i] = buffer[i].toDouble() / 32768.0
    fillData(i + 1, bufferedReadResult, data, buffer)
  }
}

private tailrec fun iterate(sub: Subscriber<in DoubleArray>,
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

private tailrec fun copy(ss: ShortArray, bs: ByteArray, i: Int) {
  if (i < ss.size) {
    val short = ss[i].toInt()
    bs[i * 2] = (short and 0x00FF).toByte()
    bs[i * 2 + 1] = (short shr 8).toByte()
    copy(ss, bs, i + 1)
  }
}

private fun record() = { sub: Subscriber<in DoubleArray> ->
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
    val audioBuffer = ShortArray(blockSize)
    val visualData = DoubleArray(blockSize)

    ar.startRecording()
    if (ar.isRecording()) {
      iterate(sub, ar, audioBuffer, visualData)
    } else {
      sub.onError(IllegalStateException("could not start the record"))
    }
  } else {
    sub.onError(IllegalStateException("failed to initialize $ar"))
  }
}

//fun writeData(o:Observable<DoubleArray>)