package com.oliver.musicplayerapp

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.oliver.musicplayerapp.databinding.ActivityMainBinding

import android.view.View

import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts


class MainActivity : AppCompatActivity() {


    lateinit var headsetBroadcast: HeadphonesBroadcast
    lateinit var binding: ActivityMainBinding
    var selectedTrack: Int? = null
    var isPlaying: Boolean = false
    private val songs = mutableListOf<Int>(
        R.raw.theweeknd_blindinglights,
        R.raw.theweeknd_mothtoaflame,
        R.raw.theweeknd_save_your_tears,
        R.raw.theweeknd_cantfeelmyface,
        R.raw.theweeknd_takemybreath
    )

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intentFromDetails = result.data
            isPlaying = intentFromDetails?.getBooleanExtra(DetailsActivity.PLAY_STATUS, false) ?: false
            updateButton()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        headsetBroadcast = HeadphonesBroadcast()
        populateSongs()

        binding.playButton.setOnClickListener {
            Log.d("Tests", "PLay clicked")

            if (selectedTrack == null) return@setOnClickListener

            if (isPlaying) {
                val service = Intent(this, MusicPlayerService::class.java)
                service.putExtra(MusicPlayerService.MUSIC_STATE, MusicState.PAUSE)
                service.putExtra(MusicPlayerService.SONG, selectedTrack)
                startService(service)
                isPlaying = false
                updateButton()
            } else {
                val service = Intent(this, MusicPlayerService::class.java)
                service.putExtra(MusicPlayerService.MUSIC_STATE, MusicState.PLAY)
                service.putExtra(MusicPlayerService.SONG, selectedTrack)
                startService(service)
                isPlaying = true
                updateButton()
            }

        }

        binding.bottomAppBar.setNavigationOnClickListener {

            if (selectedTrack == null) return@setNavigationOnClickListener

            val service = Intent(this, MusicPlayerService::class.java)
            stopService(service)
            isPlaying = false
            binding.playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        }

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.more -> {
                    val intentDetails = Intent(this, DetailsActivity::class.java)
                    intentDetails.putExtra(DetailsActivity.PLAY_STATUS, isPlaying)
                    resultLauncher.launch(intentDetails)
                    true
                }
                else -> false
            }
        }

    }

    override fun onPause() {
        super.onPause()
        unregisterBroadcast()
    }

    override fun onResume() {
        super.onResume()
        registerBroadcast()
    }

    private fun populateSongs() {
        for (k in 0..4) {
            val textView = TextView(this)
            textView.id = k
            textView.setPadding(10, 10, 10, 10)
            textView.text = "Track id $k"
            textView.setOnClickListener { view -> selectSong(view) }
            binding.musicScrollView.addView(textView)
        }
    }

    private fun selectSong(view: View?) {

        if (view != null && selectedTrack == songs[view.id]) return

        for (i in 0 until binding.musicScrollView.childCount) {
            var v = binding.musicScrollView.getChildAt(i)
            v.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
        view?.setBackgroundColor(Color.parseColor("#808080"))

        if (view != null)
            selectedTrack = songs[view.id]

        val service = Intent(this, MusicPlayerService::class.java)
        service.putExtra(MusicPlayerService.MUSIC_STATE, MusicState.STOP)
        startService(service)

        service.putExtra(MusicPlayerService.MUSIC_STATE, MusicState.PLAY)
        service.putExtra(MusicPlayerService.SONG, selectedTrack)
        startService(service)
        isPlaying = true
        binding.playButton.setImageResource(R.drawable.ic_baseline_pause_24)
    }

    fun updateButton() {
        if (isPlaying)
            binding.playButton.setImageResource(R.drawable.ic_baseline_pause_24)
        else
            binding.playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
    }

    fun registerBroadcast() {
        val intentFilter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
        registerReceiver(headsetBroadcast, intentFilter)
    }

    fun unregisterBroadcast() {
        unregisterReceiver(headsetBroadcast)
    }

}