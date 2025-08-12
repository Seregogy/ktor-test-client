package com.example.ktor_test_client.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer

class AudioPlayerViewModel : ImagePaletteViewModel() {
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

            override fun onPlayerError(error: PlaybackException) {
                val errorMessage = when (error.errorCode) {
                    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> "Network connection failed"
                    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> "Network timeout"
                    PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS -> "HTTP error: ${error.message}"
                    PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> "File not found"
                    PlaybackException.ERROR_CODE_IO_NO_PERMISSION -> "No permission"
                    PlaybackException.ERROR_CODE_IO_INVALID_HTTP_CONTENT_TYPE -> "Invalid content type"
                    PlaybackException.ERROR_CODE_IO_READ_POSITION_OUT_OF_RANGE -> "Read position out of range"
                    PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED -> "Malformed media"
                    PlaybackException.ERROR_CODE_PARSING_MANIFEST_MALFORMED -> "Malformed manifest"
                    PlaybackException.ERROR_CODE_DECODER_INIT_FAILED -> "Decoder init failed"
                    PlaybackException.ERROR_CODE_DECODER_QUERY_FAILED -> "Decoder query failed"
                    PlaybackException.ERROR_CODE_DECODING_FAILED -> "Decoding failed"
                    PlaybackException.ERROR_CODE_AUDIO_TRACK_INIT_FAILED -> "Audio track init failed"
                    PlaybackException.ERROR_CODE_AUDIO_TRACK_WRITE_FAILED -> "Audio track write failed"
                    PlaybackException.ERROR_CODE_REMOTE_ERROR -> "Remote error"
                    else -> "Unknown playback error: ${error.errorCodeName}"
                }

                Log.e("Player error", errorMessage)
                exoPlayer?.currentMediaItem?.let {
                    val currentPosition = exoPlayer?.currentPosition

                    exoPlayer!!.setMediaItem(it)

                    exoPlayer!!.prepare()
                    exoPlayer!!.seekTo(currentPosition ?: 0L)
                    exoPlayer!!.play()
                    exoPlayer!!.playWhenReady = true
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