package com.example.ktor_test_client.viewmodel

import android.app.Application
import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VideoPlayerViewModel(
    private val context: Application
) : ViewModel() {
    private val _aspectRatio = MutableStateFlow(1f)
    val aspectRatio: StateFlow<Float> = _aspectRatio

    val player by lazy {
        ExoPlayer.Builder(context).build().apply {

            setHandleAudioBecomingNoisy(false)
            setWakeMode(C.WAKE_MODE_NONE)

            volume = 0f
            repeatMode = Player.REPEAT_MODE_ALL

            addListener(object : Player.Listener {
                @OptIn(UnstableApi::class)
                override fun onPlaybackStateChanged(state: Int) {
                    super.onPlaybackStateChanged(state)

                    when (state) {
                        Player.STATE_READY -> {
                            _aspectRatio.value = videoFormat?.let { it.width.toFloat() / it.height.toFloat() } ?: 1f
                            play()
                        }
                    }
                }
            })
        }
    }

    fun playByUrl(url: String) {
        player.run {
            stop()

            Log.d("Video-player", url)

            setMediaItem(
                MediaItem.Builder()
                    .setUri(url)
                    .build()
            )

            prepare()
            play()
        }
    }

    fun pause() {
        player.pause()
    }

    override fun onCleared() {
        super.onCleared()

        player.run {
            stop()
            release()
        }
    }
}