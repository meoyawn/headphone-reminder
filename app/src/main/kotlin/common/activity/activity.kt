package common.activity

import android.R
import android.app.Activity
import android.view.ViewGroup
import common.lifecycle.Lifecycle
import common.lifecycle.lifecycleImpl
import rx.Observable

fun Activity.lifecycle(): Observable<Lifecycle> = lifecycleImpl(this)
inline fun <reified T : Lifecycle> Activity.lifecycleOfType(): Observable<T> =
    lifecycle().ofType(T::class.java)

fun Activity.creates() = lifecycleOfType<Lifecycle.Create>()
fun Activity.starts() = lifecycleOfType<Lifecycle.Start>()
fun Activity.resumes() = lifecycleOfType<Lifecycle.Resume>()
fun Activity.saveInstanceStates() = lifecycleOfType<Lifecycle.SaveInstanceState>()
fun Activity.pauses() = lifecycleOfType<Lifecycle.Pause>()
fun Activity.stops() = lifecycleOfType<Lifecycle.Stop>()
fun Activity.destroys() = lifecycleOfType<Lifecycle.Destroy>()

fun Activity.root(): ViewGroup = findViewById(R.id.content) as ViewGroup