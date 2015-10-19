package common.intent

import android.content.Context
import android.content.Intent

inline fun <reified T : Context> intent(c: Context): Intent =
    Intent(c, T::class.java)