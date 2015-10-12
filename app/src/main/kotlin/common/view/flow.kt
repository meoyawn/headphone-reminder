package common.view

import flow.Flow
import flow.History

fun <T> Flow.push(t: T): History =
    history.buildUpon().push(t).build()