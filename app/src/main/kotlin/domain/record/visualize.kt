package domain.record

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D

private tailrec fun copyLoop(rec: RecordBuffer, data: DoubleArray, i: Int) {
  val buffer = rec.buffer
  if (i < buffer.size && i < rec.read) {
    data[i] = buffer[i].toDouble() / 32768.0
    copyLoop(rec, data, i + 1)
  }
}

private fun fft(buffer: DoubleArray, fft: Lazy<DoubleFFT_1D>): (RecordBuffer) -> DoubleArray = {
  copyLoop(it, buffer, 0)
  fft.value.realForward(buffer)
  buffer
}

fun visualize(): (RecordBuffer) -> DoubleArray =
    fft(DoubleArray(blockSize), lazy { DoubleFFT_1D(blockSize) })

