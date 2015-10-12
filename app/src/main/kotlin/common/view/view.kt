package common.view

import android.support.annotation.IdRes
import android.view.View
import android.view.ViewGroup

inline fun <reified T : View> View.maybe(@IdRes i: Int): T? =
    findViewById(i).let {
      when (it) {
        null -> null
        is T -> it as T
        else -> throw IllegalArgumentException()
      }
    }

inline fun <reified T : View> View.byId(@IdRes i: Int): T =
    findViewById(i).let {
      when (it) {
        is T -> it as T
        else -> throw IllegalArgumentException()
      }
    }

fun <T : View> T.matchParent() =
    apply {
      layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }