package common.audio

import android.media.AudioManager

fun AudioManager.isMusicActiveAndNotLoud(): Boolean =
    isMusicActive && !isSpeakerphoneOn