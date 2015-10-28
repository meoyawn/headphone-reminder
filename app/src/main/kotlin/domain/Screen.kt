package domain

import adeln.boilerplate.R
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import common.view.byId
import common.view.clicks
import common.view.inflate
import common.view.push
import domain.play.play
import domain.record.audioRecord
import domain.record.visualize
import domain.record.writeF
import domain.view.RecordView
import flow.Flow
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber
import java.io.File

sealed class Screen {
  object Main : Screen() {
    fun setup(root: ViewGroup, detaches: Observable<View>): View = run {
      val v = root.inflate(R.layout.screen_main)
      val destroys = detaches.filter { it === v }
      v.byId<View>(R.id.fab_record)
          .clicks()
          .takeUntil(destroys)
          .subscribe { Flow.get(v).push(Recorder) }
      v
    }
  }

  object Recorder : Screen() {
    fun setup(root: ViewGroup, detaches: Observable<View>): View = run {
      val v = root.inflate(R.layout.screen_recorder)
      val rv = v.byId<RecordView>(R.id.recorder)

      val destroys = detaches.filter { it === v }.share()
      val record = audioRecord()
          .takeUntil(destroys)
          .share()

      record
          .map(visualize())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe {
            rv.data = it
          }

      val file = File(Environment.getExternalStorageDirectory(), "file.pcm")
          .apply { if (exists()) delete() }

      record
          .writeF(file)
          .flatMap { play(it).subscribeOn(Schedulers.io()) }
          .subscribe({ Timber.d("written to $it") }, { Timber.e("", it) })
      v
    }
  }
}