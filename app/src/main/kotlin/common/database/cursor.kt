package common.database

import android.database.Cursor
import android.database.MatrixCursor

fun emptyCursor(): MatrixCursor =
    MatrixCursor(emptyArray(), 0)

fun firstString(c: Cursor): String? =
    when (c.count) {
      0    -> null
      else -> {
        c.moveToFirst()
        c.getString(0).apply { c.close() }
      }
    }