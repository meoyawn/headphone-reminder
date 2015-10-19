package adeln.boilerplate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import common.activity.root
import common.kotlin.notNull
import common.view.first
import common.view.replaceTo
import domain.Screen
import domain.intent.LaunchSource
import domain.intent.mainActivityExtent
import flow.Flow
import flow.FlowDelegate
import flow.History
import rx.subjects.PublishSubject
import thirdparty.GsonParceler
import timber.log.Timber

class MainActivity : Activity() {
  companion object {
    val parceler = GsonParceler(Gson())
    val defaultHistory = History.single(Screen.Main)
  }

  val detaches = PublishSubject.create<View>()
  var flowSupport: FlowDelegate? = null

  fun flowOnCreate(state: Bundle?, dispatcher: (Flow.Traversal, Flow.TraversalCallback) -> Unit) =
      FlowDelegate.onCreate(lastNonConfigurationInstance as FlowDelegate.NonConfigurationInstance?,
                            intent,
                            state,
                            parceler,
                            defaultHistory,
                            dispatcher)

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    val s = when (mainActivityExtent(intent)) {
      LaunchSource.DEFAULT         -> Screen.Main
      LaunchSource.RECORD_SHORTCUT -> Screen.Recorder
    }
    Flow.get(this).replaceTo(s)
  }

  fun dispatch(c: Flow.TraversalCallback, t: Flow.Traversal) {
    val orig = t.origin
    val dest = t.destination
    val prev: Screen = orig.top()
    val current: Screen = dest.top()
    val root = root()
    val prevView = root.first()
    if (current != prev || prevView == null) {
      Timber.d("doing a traversal $prev to $current")
      val view = when (current) {
        is Screen.Main -> current.setup(root, detaches)
        is Screen.Recorder -> current.setup(root, detaches)
      }

      val dir: Flow.Direction = t.direction
      when (dir) {
        Flow.Direction.FORWARD -> {
          orig.currentViewState().save(prevView)
          replace(prevView, view)
        }
        Flow.Direction.BACKWARD -> {
          replace(prevView, view)
          dest.currentViewState().restore(view)
        }
        Flow.Direction.REPLACE -> replace(prevView, view)
      }
    }

    c.onTraversalCompleted()
  }

  fun replace(prevView: View?, nextView: View) {
    val root = root()
    if (prevView != null) {
      root.removeView(prevView)
      detaches.onNext(prevView)
    }
    root.addView(nextView)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    flowSupport = flowOnCreate(savedInstanceState) { t, c -> dispatch(c, t) }
  }

  override fun onResume() {
    super.onResume()
    flowSupport?.onResume()
  }

  override fun onPause() {
    flowSupport?.onPause()
    super.onPause()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    flowSupport?.onSaveInstanceState(outState)
  }

  override fun onDestroy() {
    root().first().notNull { detaches.onNext(it) }
    detaches.onCompleted()
    super.onDestroy()
  }

  override fun onRetainNonConfigurationInstance(): Any? =
      flowSupport?.onRetainNonConfigurationInstance()

  override fun onBackPressed(): Unit =
      if (!(flowSupport?.onBackPressed() ?: false)) super.onBackPressed()

  override fun getSystemService(name: String?): Any? =
      flowSupport?.getSystemService(name) ?: super.getSystemService(name)
}