package domain.record

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource
import android.support.annotation.WorkerThread

val blockSize = 256

@WorkerThread
fun audioRecord() {
  val freq = 8000
  val channel = AudioFormat.CHANNEL_IN_MONO
  val format = AudioFormat.ENCODING_DEFAULT
  val bufferSize = AudioRecord.getMinBufferSize(freq, channel, format)
  AudioRecord(AudioSource.MIC, freq, channel, format, bufferSize)
}