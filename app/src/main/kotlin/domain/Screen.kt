package domain

import adeln.boilerplate.R
import android.view.View
import android.view.ViewGroup
import common.view.byId
import common.view.inflate
import domain.record.audioRecord
import domain.view.RecordView
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject

sealed class Screen {
  object Main : Screen() {
    fun setup(root: ViewGroup, detaches: PublishSubject<View>): View = run {
      root.inflate(R.layout.activity_main)
    }
  }

  object Recorder : Screen() {
    fun setup(root: ViewGroup, detaches: PublishSubject<View>): View = run {
      val v = root.inflate(R.layout.screen_recorder)
      val destroys = detaches.filter { it === v }
      val rv = v.byId<RecordView>(R.id.recorder)
      audioRecord()
          .subscribeOn(Schedulers.computation())
          .unsubscribeOn(Schedulers.computation())
          .takeUntil(destroys)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe {
            rv.data = it
          }
      v
    }
  }
}