package domain.record

import common.rx.finally
import common.rx.last
import common.thread.assertWorkerThread
import okio.BufferedSink
import okio.Okio
import rx.Observable
import rx.Subscriber
import java.io.File
import java.io.RandomAccessFile

fun Observable<RecordBuffer>.writeF(file: File) =
    lift(writeOp(file))

private fun writeOp(file: File): Observable.Operator<File, RecordBuffer> =
    object : Observable.Operator<File, RecordBuffer> {
      override fun call(sub: Subscriber<in File>): Subscriber<in RecordBuffer> =
          writeSub(file, sub)
    }

private fun writeSub(file: File, sub: Subscriber<in File>): Subscriber<RecordBuffer> =
    object : Subscriber<RecordBuffer>(sub) {
      val sink by lazy {
        assertWorkerThread()

        Okio.buffer(Okio.sink(file))
            .apply {
              sub.finally { close() }
              write(ByteArray(44))
            }
      }

      var bytesWritten = 0

      override fun onStart() =
          request(Long.MAX_VALUE)

      override fun onCompleted() {
        sink.flush()
        sink.close()
        writeHeader(file, bytesWritten, channels)
        sub.last(file)
      }

      override fun onError(e: Throwable?) =
          sub.onError(e)

      override fun onNext(buf: RecordBuffer) {
        write(buf, sink, 0)
        bytesWritten += buf.read * 2
      }
    }

private fun writeHeader(file: File, bytesWritten: Int, channels: Short) {
  val raf = RandomAccessFile(file, "rw")
  raf.seek(0)

  raf.writeBytes("RIFF")
  raf.writeInt(Integer.reverseBytes(bytesWritten + 36))
  raf.writeBytes("WAVE")

  raf.writeBytes("fmt ")
  raf.writeInt(Integer.reverseBytes(16)); // format subchunk size
  raf.writeShort(shortReverseBytes(1))
  raf.writeShort(shortReverseBytes(channels))

  val bytesPerSec = (sampleBits + 7 ) / 8
  raf.writeInt(Integer.reverseBytes(frequency))
  raf.writeInt(Integer.reverseBytes(frequency * channels * bytesPerSec)); // byte rate

  raf.writeShort(shortReverseBytes((channels * bytesPerSec).toShort()))
  raf.writeShort(shortReverseBytes(sampleBits.toShort()))

  raf.writeBytes("data")
  raf.writeInt(Integer.reverseBytes(bytesWritten)); // data subchunk size

  raf.close()
}

private fun shortReverseBytes(s: Short): Int =
    java.lang.Short.reverseBytes(s).toInt()

private fun write(buf: RecordBuffer, sink: BufferedSink, i: Int) {
  if (i < buf.read) {
    val v = buf.buffer[i].toInt()
    sink.writeByte(v and 0x00FF)
    sink.writeByte(v shr 8)
    write(buf, sink, i + 1)
  }
}