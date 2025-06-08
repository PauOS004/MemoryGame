package com.example.memorygame.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val binder = MusicBinder()
    var currentTrack: Int? = null
        private set
    private var isPaused = false
    private var currentVolume: Float = 1.0f

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        //
        currentTrack?.let { trackId ->
            playMusic(trackId)
            if (isPaused) {
                pauseMusic()
            }
        }
    }

    fun playMusic(resId: Int) {
        if (currentTrack == resId && mediaPlayer != null) {
            if (isPaused) {
                resumeMusic()
            }
            return
        }

        if (mediaPlayer != null) {
            mediaPlayer?.release()
        }
        mediaPlayer = MediaPlayer.create(this, resId)
        mediaPlayer?.isLooping = true
        mediaPlayer?.setVolume(currentVolume, currentVolume)
        mediaPlayer?.start()
        currentTrack = resId
        isPaused = false
    }

    fun playSound(resId: Int) {
        val soundPlayer = MediaPlayer.create(this, resId)
        soundPlayer.setOnCompletionListener { it.release() }
        soundPlayer.setVolume(currentVolume, currentVolume)
        soundPlayer.start()
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
        isPaused = true
    }

    fun resumeMusic() {
        mediaPlayer?.start()
        isPaused = false
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentTrack = null
        isPaused = false
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    fun setMusicVolume(volume: Float) {
        currentVolume = volume
        mediaPlayer?.setVolume(volume, volume)
        Log.d("MusicService", "Volumen ajustado a: $volume")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMusic()
    }
} 