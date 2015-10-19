package common.view

import domain.Screen
import flow.Flow
import flow.Flow.Direction
import flow.History

fun Flow.push(t: Screen): Unit =
    history.buildUpon()
        .push(t)
        .build()
        .let { setHistory(it, Direction.FORWARD) }

fun History.Builder.replace(s: Screen): History.Builder =
    run {
      pop()
      push(s)
    }

fun Flow.replaceTo(t: Screen): Unit =
    history.buildUpon()
        .replace(t)
        .build()
        .let { setHistory(it, Direction.REPLACE) }