package domain

import adeln.boilerplate.R
import android.view.View
import android.view.ViewGroup
import common.view.byId
import common.view.inflate
import domain.record.audioRecord
import domain.view.RecordView
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

sealed class Screen {
  object Main : Screen() {
    fun setup(root: ViewGroup, detaches: Observable<View>): View = run {
      root.inflate(R.layout.activity_main)
    }
  }

  object Recorder : Screen() {
    fun setup(root: ViewGroup, detaches: Observable<View>): View = run {
      val v = root.inflate(R.layout.screen_recorder)
      val rv = v.byId<RecordView>(R.id.recorder)

      val destroys = detaches.filter { it === v }
      audioRecord()
          .subscribeOn(Schedulers.computation())
          .unsubscribeOn(Schedulers.computation())
          .takeUntil(destroys)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe { rv.data = it }
      v
    }
  }
}