package com.example.ktor_test_client.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.lifecycle.ViewModel
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class AudioPlayerViewModel : ViewModel() {
    var exoPlayer: ExoPlayer? = null

    var currentDuration: MutableState<Long> = mutableLongStateOf(1L)
    var currentPlaybackState: MutableState<Int> = mutableIntStateOf(Player.STATE_IDLE)

    fun initializePlayer(context: Context) {
        exoPlayer = ExoPlayer.Builder(context).build()
        exoPlayer!!.prepare()

        exoPlayer?.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) { }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY && exoPlayer!!.duration != C.TIME_UNSET) {
                    currentDuration.value = exoPlayer!!.duration
                }
            }
        })
    }

    fun playFromUri(uri: Uri) {
        exoPlayer?.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer?.prepare()
    }

    fun playPause() {
        exoPlayer?.let {
            if (!exoPlayer!!.isPlaying) {
                play()
            } else {
                pause()
            }
        }
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun play() {
        exoPlayer?.play()
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