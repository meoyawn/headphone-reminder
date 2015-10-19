package common.context

import android.app.AlarmManager
import android.content.Context
import android.media.AudioManager
import java.io.File

fun Context.cacheDir(): File =
    externalCacheDir ?: cacheDir

fun Context.audios(): AudioManager =
    service(Context.AUDIO_SERVICE)

fun Context.alarms(): AlarmManager =
    service(Context.ALARM_SERVICE)

inline fun <reified T> Context.service(s: String): T =
    getSystemService(s) as T