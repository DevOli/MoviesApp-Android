package com.oliver.musicplayerapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.oliver.musicplayerapp.databinding.ActivityDetailsBinding
import com.oliver.musicplayerapp.databinding.ActivityMainBinding

class DetailsActivity : AppCompatActivity() {

    companion object {
        const val PLAY_STATUS = "play_status"
    }
    lateinit var binding: ActivityDetailsBinding
    var isPlaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        isPlaying = intent.getBooleanExtra(PLAY_STATUS, false)
        updateButton()
        binding.bottomAppBar.menu.removeItem(R.id.more)

        binding.playButton.setOnClickListener {
            if (isPlaying) {
                val service = Intent(this, MusicPlayerService::class.java)
                service.putExtra(MusicPlayerService.MUSIC_STATE, MusicState.PAUSE)
                startService(service)
                isPlaying = false
                updateButton()
            } else {
                val service = Intent(this, MusicPlayerService::class.java)
                service.putExtra(MusicPlayerService.MUSIC_STATE, MusicState.PLAY)
                startService(service)
                isPlaying = true
                updateButton()
            }
        }

        binding.bottomAppBar.setNavigationOnClickListener {

            val service = Intent(this, MusicPlayerService::class.java)
            stopService(service)
            isPlaying = false
            binding.playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)

            closeDetails()
        }
    }

    fun updateButton() {
        if (isPlaying)
            binding.playButton.setImageResource(R.drawable.ic_baseline_pause_24)
        else
            binding.playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
    }

    fun closeDetails() {
        val intentResult = Intent()
        intentResult.putExtra(DetailsActivity.PLAY_STATUS, isPlaying)
        setResult(Activity.RESULT_OK, intentResult)

        finish()
    }

    override fun onBackPressed() {
        closeDetails()
    }
}