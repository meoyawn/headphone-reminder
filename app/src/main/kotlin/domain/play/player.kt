package domain.play

import android.media.AudioManager
import android.media.MediaPlayer
import common.rx.finally
import common.thread.assertWorkerThread
import rx.Observable
import rx.Subscriber
import java.io.File
import java.util.concurrent.TimeUnit

data class CurrentPosition(var currentPosition: Int, val duration: Int)

fun play(file: File): Observable<CurrentPosition> =
    Observable.create(stream(file))

private fun stream(file: File) = { sub: Subscriber<in CurrentPosition> ->
  val mp = MediaPlayer().apply {
    setDataSource(file.absolutePath)
    setAudioStreamType(AudioManager.STREAM_MUSIC)
  }
  sub.finally { mp.release() }

  assertWorkerThread()
  mp.prepare()
  mp.start()
  val cp = CurrentPosition(0, mp.duration)
  sub.add(Observable.interval(0, 16, TimeUnit.MILLISECONDS)
              .map { cp.apply { currentPosition = mp.currentPosition } }
              .takeUntil(complete(mp))
              .subscribe(sub))
}

private fun complete(mp: MediaPlayer): Observable<Unit> =
    Observable.create { sub ->
      sub.finally { mp.setOnCompletionListener(null) }
      mp.setOnCompletionListener { sub.onNext(Unit) }
    }