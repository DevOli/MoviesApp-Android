package com.oliver.musicplayerapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import kotlin.math.ln

class HeadphonesBroadcast: BroadcastReceiver() {



    override fun onReceive(context: Context?, intent: Intent?) {

        val audioManager: AudioManager = context?.getSystemService(AUDIO_SERVICE) as AudioManager

        if (intent?.action.equals(Intent.ACTION_HEADSET_PLUG)) {
            when (intent?.getIntExtra("state", -1)) {
                1 -> {
                    val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    Toast.makeText(context, "Headset Connected", Toast.LENGTH_LONG).show()
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max/2, AudioManager.FLAG_SHOW_UI)
                }
            }
        }
    }
}