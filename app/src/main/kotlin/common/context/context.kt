package common.context

import android.content.Context
import java.io.File

fun Context.cacheDir(): File = externalCacheDir ?: cacheDir