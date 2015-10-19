package domain

import adeln.boilerplate.R
import android.view.View
import android.view.ViewGroup
import common.view.inflate
import rx.subjects.PublishSubject

sealed class Screen {
  object Main : Screen() {
    fun setup(root: ViewGroup, detaches: PublishSubject<View>): View = run {
      root.inflate(R.layout.activity_main)
    }
  }

  object Recorder : Screen() {
    fun setup(root: ViewGroup, detaches: PublishSubject<View>): View = run {
      root.inflate(R.layout.recorder)
    }
  }
}