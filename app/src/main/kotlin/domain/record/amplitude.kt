package domain.record

import android.graphics.Color
import android.graphics.Paint
import android.media.MediaRecorder
import android.media.MediaRecorder.AudioEncoder
import android.media.MediaRecorder.AudioSource
import android.media.MediaRecorder.OutputFormat
import android.os.Environment
import common.either.tryCatchUnit
import common.rx.finally
import domain.view.DrawingView
import rx.Observable
import rx.Subscription
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

val src = AudioSource.MIC
val fmt = OutputFormat.AMR_WB
val enc = AudioEncoder.AMR_WB

fun mediaRecord(): Observable<Int> =
    Observable.create { sub ->
      val mr = MediaRecorder().apply {
        setAudioSource(src)
        setOutputFormat(fmt)
        setOutputFile(File(Environment.getExternalStorageDirectory(), "fuck").absolutePath)
        setAudioEncoder(enc)
      }
      sub.finally {
        tryCatchUnit<IllegalStateException> { mr.stop() }
        tryCatchUnit<IllegalStateException> { mr.reset() }
        tryCatchUnit<IllegalStateException> { mr.release() }
      }
      mr.prepare()
      mr.start()
      sub.add(Observable.interval(0, 16, TimeUnit.MILLISECONDS)
                  .map { mr.maxAmplitude }
                  .subscribe(sub))
    }

fun Observable<Int>.drawAmplitude(dv: DrawingView): Subscription =
    run {
      var amp = 0
      val paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.RED
      }
      dv.drawF = {
        it.drawCircle(dv.width / 2f, dv.height / 2f, amp.toFloat(), paint)
      }
      subscribe {
        Timber.d("drawing $it amp")
        amp = it
        dv.invalidate()
      }
    }