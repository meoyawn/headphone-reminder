package common.activity

import android.R
import android.app.Activity
import android.view.ViewGroup
import common.lifecycle.Create
import common.lifecycle.Destroy
import common.lifecycle.Lifecycle
import common.lifecycle.Pause
import common.lifecycle.Resume
import common.lifecycle.SaveInstanceState
import common.lifecycle.Start
import common.lifecycle.Stop
import common.lifecycle.lifecycleImpl
import rx.Observable

fun Activity.lifecycle(): Observable<Lifecycle> = lifecycleImpl(this)
inline fun <reified T : Lifecycle> Activity.lifecycleOfType(): Observable<T> =
    lifecycle().ofType(T::class.java)

fun Activity.creates() = lifecycleOfType<Create>()
fun Activity.starts() = lifecycleOfType<Start>()
fun Activity.resumes() = lifecycleOfType<Resume>()
fun Activity.saveInstanceStates() = lifecycleOfType<SaveInstanceState>()
fun Activity.pauses() = lifecycleOfType<Pause>()
fun Activity.stops() = lifecycleOfType<Stop>()
fun Activity.destroys() = lifecycleOfType<Destroy>()

fun Activity.root(): ViewGroup = findViewById(R.id.content) as ViewGroup