package domain.record

import common.rx.finally
import common.rx.last
import rx.Observable
import rx.Subscriber
import java.io.File
import java.io.FileOutputStream

private tailrec fun copy(ss: ShortArray, bs: ByteArray, i: Int) {
  if (i < ss.size) {
    val short = ss[i].toInt()
    bs[i * 2] = (short and 0x00FF).toByte()
    bs[i * 2 + 1] = (short shr 8).toByte()
    copy(ss, bs, i + 1)
  }
}

fun Observable<RecordBuffer>.writeF(f: File) =
    this
        .lift(object : Observable.Operator<File, RecordBuffer> {
          override fun call(sub: Subscriber<in File>): Subscriber<in RecordBuffer> =
              run {
                val fos = lazy { FileOutputStream(f) }
                val buffer = ByteArray(blockSize * 2)

                sub.finally { fos.value.close() }

                object : Subscriber<RecordBuffer>(sub) {
                  override fun onCompleted() {
                    fos.value.flush()
                    fos.value.close()
                    sub.last(f)
                  }

                  override fun onError(e: Throwable?) = sub.onError(e)

                  override fun onNext(buf: RecordBuffer) {
                    copy(buf.buffer, buffer, 0)
                    fos.value.write(buffer, 0, buf.read * 2)
                  }
                }
              }
        })