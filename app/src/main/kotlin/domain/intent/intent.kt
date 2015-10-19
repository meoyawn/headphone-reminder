package domain.intent

import adeln.boilerplate.MainActivity
import android.content.Context
import android.content.Intent
import common.intent.intent

enum class LaunchSource {
  DEFAULT,
  RECORD_SHORTCUT
}

private val LAUNCH_SOURCE = "launch source"

fun mainActivityIntent(c: Context, src: LaunchSource): Intent =
    intent<MainActivity>(c).putExtra(LAUNCH_SOURCE, src.ordinal())

fun mainActivityExtent(i: Intent): LaunchSource =
    LaunchSource.values()[i.getIntExtra(LAUNCH_SOURCE, 0)]