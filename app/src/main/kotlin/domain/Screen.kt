package domain

import adeln.boilerplate.R
import android.view.View
import android.view.ViewGroup
import common.context.cacheDir
import common.view.byId
import common.view.clicks
import common.view.inflate
import common.view.push
import domain.record.audioRecord
import domain.record.visualize
import domain.record.writeF
import domain.view.RecordView
import flow.Flow
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
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
          .visualize()
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe {
            rv.data = it
          }

      record
          .writeF(File(root.context.cacheDir(), "file.pcm"))
          .subscribe {
            Timber.d("written to $it")
          }
      v
    }
  }
}