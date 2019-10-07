package com.chanhbc.iother

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer

class ISound private constructor(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun playSoundRaw(idResource: Int, isLoop: Boolean) {
        this.stop()
        mediaPlayer = MediaPlayer.create(context, idResource)
        mediaPlayer!!.isLooping = isLoop
        mediaPlayer!!.start()
    }

    fun stop() {
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: ISound? = null

        fun getInstance(context: Context): ISound {
            if (instance == null) {
                instance = ISound(context)
            }
            return instance!!
        }
    }
}
