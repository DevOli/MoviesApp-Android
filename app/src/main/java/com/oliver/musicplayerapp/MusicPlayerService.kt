package com.oliver.musicplayerapp

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.ln

class MusicPlayerService: Service() {

    companion object {
        const val MUSIC_STATE = "music_state"
        const val SONG = "song_number"
    }

    var musicPLayer: MediaPlayer? = null
    val job = Job()
    var serviceScope = CoroutineScope(Dispatchers.IO + job)

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.d("Tests", "Created")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Tests", "Started")

        val state = intent?.getSerializableExtra(MUSIC_STATE) as MusicState
        val songNumber = intent.getIntExtra(SONG, 0)
        val volume = intent?.getSerializableExtra(MUSIC_STATE) as MusicState

        when (state) {
            MusicState.PLAY -> {
                playMusic(songNumber)
            }
            MusicState.PAUSE -> {
                musicPLayer?.pause()
            }
            MusicState.STOP -> {
               stopMusic()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    fun playMusic(songNumber: Int) {
        if (musicPLayer == null) {
            musicPLayer = MediaPlayer.create(baseContext, songNumber)
        }
        if (musicPLayer?.isPlaying == true) return
        musicPLayer?.start()
    }

    fun stopMusic() {
        musicPLayer?.let {
            it.stop()
            it.release()
        }
        musicPLayer = null
    }

    override fun onDestroy() {
        Log.d("Tests", "Destroyed")
        job.cancel()
        stopMusic()
        super.onDestroy()
    }

    fun setVolumeToHalf() {
        val maxVolume = 50
        val log1 = (ln((maxVolume - 25).toDouble()) / ln(maxVolume.toDouble())).toFloat()
        musicPLayer?.setVolume(log1, log1)
    }
}

enum class MusicState {
    PLAY, STOP, PAUSE
}