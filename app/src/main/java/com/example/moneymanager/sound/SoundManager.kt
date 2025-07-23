package com.example.moneymanager.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.example.moneymanager.R

class SoundManager(context: Context) {
    init {
        val mediaPlayer = MediaPlayer.create(context, R.raw.keyboard)
        mediaPlayer.start()
    }

}
