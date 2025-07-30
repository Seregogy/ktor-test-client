package com.example.ktor_test_client.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class TrackScreenViewModel: ViewModel() {
    var exoPlayer: ExoPlayer? = null

    fun initializePlayer(context: Context) {
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri("https://example.com/audio.mp3") // URL или локальный файл
            setMediaItem(mediaItem)
            prepare()
        }
    }

    fun playPause() {
        exoPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun onCleared() {
        releasePlayer()
        super.onCleared()
    }
}