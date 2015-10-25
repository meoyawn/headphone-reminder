package adeln.boilerplate

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatCallback
import android.support.v7.app.AppCompatDelegate
import android.support.v7.view.ActionMode
import android.support.v7.widget.Toolbar
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
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

class MainActivity : Activity(), AppCompatCallback {
  companion object {
    val parceler = GsonParceler(Gson())

    fun screen(intent: Intent): Screen =
        when (mainActivityExtent(intent)) {
          LaunchSource.DEFAULT         -> Screen.Main
          LaunchSource.RECORD_SHORTCUT -> Screen.Recorder
        }
  }

  val delegate by lazy { AppCompatDelegate.create(this, this) }

  val detaches = PublishSubject.create<View>()
  var flowSupport: FlowDelegate? = null

  fun flowOnCreate(state: Bundle?, dispatcher: (Flow.Traversal, Flow.TraversalCallback) -> Unit) =
      FlowDelegate.onCreate(lastNonConfigurationInstance as FlowDelegate.NonConfigurationInstance?,
                            intent,
                            state,
                            parceler,
                            History.single(screen(intent)),
                            dispatcher)

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    Flow.get(this).replaceTo(screen(intent))
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
        is Screen.Main     -> current.setup(root, detaches)
        is Screen.Recorder -> current.setup(root, detaches)
      }

      val dir: Flow.Direction = t.direction
      when (dir) {
        Flow.Direction.FORWARD  -> {
          orig.currentViewState().save(prevView)
          replace(prevView, view)
        }
        Flow.Direction.BACKWARD -> {
          replace(prevView, view)
          dest.currentViewState().restore(view)
        }
        Flow.Direction.REPLACE  -> replace(prevView, view)
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
    delegate.installViewFactory()
    delegate.onCreate(savedInstanceState)
    super.onCreate(savedInstanceState)
    flowSupport = flowOnCreate(savedInstanceState) { t, c -> dispatch(c, t) }
  }

  fun getSupportActionBar(): ActionBar? = delegate.supportActionBar
  fun setSupportActionBar(tb: Toolbar?) = delegate.setSupportActionBar(tb)

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    delegate.onPostCreate(savedInstanceState)
  }

  override fun getMenuInflater(): MenuInflater? = delegate.menuInflater

  override fun setContentView(layoutResID: Int) {
    delegate.setContentView(layoutResID)
  }

  override fun setContentView(view: View?) {
    delegate.setContentView(view)
  }

  override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
    delegate.setContentView(view, params)
  }

  override fun addContentView(view: View?, params: ViewGroup.LayoutParams?) {
    delegate.addContentView(view, params)
  }

  override fun onConfigurationChanged(newConfig: Configuration?) {
    super.onConfigurationChanged(newConfig)
    delegate.onConfigurationChanged(newConfig)
  }

  override fun onResume() {
    super.onResume()
    flowSupport?.onResume()
  }

  override fun onPostResume() {
    super.onPostResume()
    delegate.onPostResume()
  }

  override fun onPause() {
    flowSupport?.onPause()
    super.onPause()
  }

  override fun onStop() {
    super.onStop()
    delegate.onStop()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    flowSupport?.onSaveInstanceState(outState)
  }

  override fun onDestroy() {
    root().first().notNull { detaches.onNext(it) }
    detaches.onCompleted()
    super.onDestroy()
    delegate.onDestroy()
  }

  override fun onTitleChanged(title: CharSequence?, color: Int) {
    super.onTitleChanged(title, color)
    delegate.setTitle(title)
  }

  override fun invalidateOptionsMenu() {
    delegate.invalidateOptionsMenu()
  }

  override fun onRetainNonConfigurationInstance(): Any? =
      flowSupport?.onRetainNonConfigurationInstance()

  override fun onBackPressed(): Unit =
      when {
        flowSupport?.onBackPressed() ?: false -> Unit
        else                                  -> super.onBackPressed()
      }

  override fun getSystemService(name: String?): Any? =
      flowSupport?.getSystemService(name) ?: super.getSystemService(name)

  override fun onSupportActionModeFinished(mode: ActionMode?) = Unit
  override fun onSupportActionModeStarted(mode: ActionMode?) = Unit
  override fun onWindowStartingSupportActionMode(callback: ActionMode.Callback?): ActionMode? = null
}