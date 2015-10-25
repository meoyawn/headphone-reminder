package domain.record

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D
import rx.Observable

private tailrec fun copyLoop(rec: RecordBuffer, data: DoubleArray, i: Int) {
  val buffer = rec.buffer
  if (i < buffer.size && i < rec.read) {
    data[i] = buffer[i].toDouble() / 32768.0
    copyLoop(rec, data, i + 1)
  }
}

fun Observable<RecordBuffer>.visualize(): Observable<DoubleArray> =
    run {
      val fft = lazy { DoubleFFT_1D(blockSize) }
      val buffer = DoubleArray(blockSize)
      this
          .map {
            copyLoop(it, buffer, 0)
            fft.value.realForward(buffer)
            buffer
          }
    }