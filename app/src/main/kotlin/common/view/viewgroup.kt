package common.view

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun ViewGroup.inflate(@LayoutRes l: Int): View =
    LayoutInflater.from(context).inflate(l, this, false)

operator fun ViewGroup.get(i: Int): View? = getChildAt(i)
fun ViewGroup.first(): View? = this[0]