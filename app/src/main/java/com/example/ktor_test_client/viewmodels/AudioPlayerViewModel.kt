package com.example.ktor_test_client.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.ktor_test_client.api.KtorAPI
import com.example.ktor_test_client.api.methods.RandomTrackResponse
import com.example.ktor_test_client.api.methods.getRandomTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioPlayerViewModel : ImagePaletteViewModel() {
    var exoPlayer: ExoPlayer? = null

    var isAutoplay: Boolean = true

    private val _isInit: MutableState<Boolean> = mutableStateOf(false)
    val isInit: State<Boolean> = _isInit

    private val _currentTrackDuration: MutableState<Long> = mutableLongStateOf(1L)
    val currentTrackDuration: State<Long> = _currentTrackDuration

    private val _isPlay: MutableState<Boolean> = mutableStateOf(false)
    val isPlay: State<Boolean> = _isPlay

    private val _currentPlaybackState: MutableStateFlow<Int> = MutableStateFlow(Player.STATE_IDLE)
    var currentPlaybackState: StateFlow<Int> = _currentPlaybackState.asStateFlow()

    private var _currentTrack: MutableStateFlow<RandomTrackResponse?> = MutableStateFlow(null)
    val currentTrack: StateFlow<RandomTrackResponse?> = _currentTrack.asStateFlow()

    var onTrackEnd: () -> Unit = { }

    private lateinit var eventListener: Player.Listener

    fun initializePlayer(context: Context) {
        exoPlayer = ExoPlayer.Builder(context).build()

        eventListener = object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) { }

            override fun onPlaybackStateChanged(state: Int) {
                _isInit.value = true

                if (state != currentPlaybackState.value)
                    _currentPlaybackState.value = state

                when (state) {
                    Player.STATE_READY -> {
                        _currentTrackDuration.value = exoPlayer?.duration ?: 1L
                        exoPlayer?.prepare()

                        if (isAutoplay)
                            exoPlayer?.play()
                    }

                    Player.STATE_ENDED -> {
                        onTrackEnd()
                    }

                    Player.STATE_BUFFERING -> { }
                    Player.STATE_IDLE -> { }
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

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlay.value = isPlaying
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                println("playWhenReady changed ($playWhenReady)")
            }
        }

        exoPlayer?.addListener(eventListener)
    }

    //TODO: убрать отсюда апи и вынести получение треков в отдельный сервис
    fun getRandomTrack(api: KtorAPI, context: Context) {
        viewModelScope.launch {
            exoPlayer?.let {
                _currentTrack.value = api.getRandomTrack()

                fetchImageByUrl(context, currentTrack.value?.album?.imageUrl ?: "")
                setMediaFromUri(currentTrack.value?.track?.audioUrl ?: "")

                it.prepare()
            }
        }
    }

    fun setMediaFromUri(uri: String) {
        exoPlayer?.setMediaItem(MediaItem.fromUri(Uri.parse(uri)))
    }

    fun playPause() {
        exoPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    override fun onCleared() {
        releasePlayer()

        super.onCleared()
    }

    fun releasePlayer() {
        exoPlayer?.let {
            exoPlayer?.removeListener(eventListener)

            exoPlayer?.stop()
            exoPlayer?.release()
        }

        exoPlayer = null
    }
}