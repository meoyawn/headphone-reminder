package domain

import adeln.boilerplate.R
import android.view.View
import android.view.ViewGroup
import common.view.byId
import common.view.clicks
import common.view.inflate
import common.view.push
import domain.record.drawAmplitude
import domain.record.mediaRecord
import domain.view.DrawingView
import flow.Flow
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

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
      val rv = v.byId<DrawingView>(R.id.recorder)
      mediaRecord()
          .subscribeOn(Schedulers.io())
          .unsubscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .takeUntil(detaches.filter { it === v })
          .drawAmplitude(rv)
      v
    }
  }
}