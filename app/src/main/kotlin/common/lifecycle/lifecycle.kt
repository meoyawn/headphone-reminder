package common.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import common.rx.emit
import common.rx.finally
import common.rx.last
import rx.Observable
import rx.Subscriber
import java.util.*


interface Lifecycle
data class Create(val savedInstanceState: Bundle?) : Lifecycle

object Start : Lifecycle

object Resume : Lifecycle

object Pause : Lifecycle

object Stop : Lifecycle

data class SaveInstanceState(val outState: Bundle) : Lifecycle
data class Destroy(val isFinishing: Boolean) : Lifecycle

val lifecycleCache: HashMap<Activity, Observable<Lifecycle>> = hashMapOf()

fun lifecycleImpl(a: Activity): Observable<Lifecycle> =
    lifecycleCache[a] ?: Observable.create(onSubscribe(a)).share().apply {
      lifecycleCache.put(a, this)
    }

fun onSubscribe(a: Activity): (Subscriber<in Lifecycle>) -> Unit =
    { sub ->
      val alc = object : Application.ActivityLifecycleCallbacks {
        fun Activity.emit(l: Lifecycle): Unit =
            when (a) {
              this -> sub.emit(l)
              else -> Unit
            }

        fun Activity.last(l: Lifecycle): Unit =
            when (a) {
              this -> sub.last(l)
              else -> Unit
            }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?): Unit =
            activity.emit(Create(savedInstanceState))

        override fun onActivityStarted(activity: Activity): Unit =
            activity.emit(Start)

        override fun onActivityResumed(activity: Activity): Unit =
            activity.emit(Resume)

        override fun onActivityPaused(activity: Activity): Unit =
            activity.emit(Pause)

        override fun onActivityStopped(activity: Activity): Unit =
            activity.emit(Stop)

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle): Unit =
            activity.emit(SaveInstanceState(outState))

        override fun onActivityDestroyed(activity: Activity): Unit =
            activity.last(Destroy(activity.isFinishing))
      }

      val app = a.application
      sub.finally {
        app.unregisterActivityLifecycleCallbacks(alc)
        lifecycleCache -= a
      }
      app.registerActivityLifecycleCallbacks(alc)
    }