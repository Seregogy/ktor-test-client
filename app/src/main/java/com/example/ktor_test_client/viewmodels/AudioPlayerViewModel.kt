package com.example.ktor_test_client.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.data.repositories.Repository
import com.example.ktor_test_client.data.sources.PlaylistDataSource

object DefaultPlayerConfig {
    var timeToPreviousTrack = 3000
    var isAutoplay: Boolean = false

    var backBufferMs = 120_000

    var minBufferMs = 5_000
    var maxBufferMs = 300_000
    var bufferForPlaybackMs = 5_000
    var bufferForPlaybackAfterRebuffedMs = 5_000

    var targetBufferBytesSize = 24 * 1024 * 1024
}

class AudioPlayerViewModel(
    private val repository: Repository,
    private val context: Context
) : ImagePaletteViewModel() {
    companion object {
        private val _currentlyPlayTrackId: MutableState<String?> = mutableStateOf(null)
        val currentlyPlayTrackId: State<String?> = _currentlyPlayTrackId
    }

    var exoPlayer: ExoPlayer? = null
        private set

    private val currentPlaybackState: MutableStateFlow<Int> = MutableStateFlow(Player.STATE_IDLE)

    private val _isInit: MutableState<Boolean> = mutableStateOf(false)
    val isInit: State<Boolean> = _isInit

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _currentTrackDuration: MutableState<Long> = mutableLongStateOf(1L)
    val currentTrackDuration: State<Long> = _currentTrackDuration

    private val _isPlay: MutableState<Boolean> = mutableStateOf(false)
    val isPlay: State<Boolean> = _isPlay

    private var _currentTrack: MutableStateFlow<Track?> = MutableStateFlow(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    var onTrackEnd: () -> Unit = { }

    private var eventListener: Player.Listener = getPlayerEventListener()

    fun initializePlayer() {
        viewModelScope.launch {
            currentTrack.collect {
                it?.let {
                    _currentlyPlayTrackId.value = it.id
                }
            }
        }

        initExoPlayer()
        exoPlayer!!.addListener(eventListener)

        viewModelScope.launch {
            repository.currentTrack()?.let {
                setTrack(it)
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun initExoPlayer() {
        val audioLoadControl = DefaultLoadControl.Builder()
            .setBackBuffer(DefaultPlayerConfig.backBufferMs, true)
            .setBufferDurationsMs(
                DefaultPlayerConfig.minBufferMs,
                DefaultPlayerConfig.maxBufferMs,
                DefaultPlayerConfig.bufferForPlaybackMs,
                DefaultPlayerConfig.bufferForPlaybackAfterRebuffedMs
            )
            .setTargetBufferBytes(DefaultPlayerConfig.targetBufferBytesSize)
            .build()

        exoPlayer = ExoPlayer.Builder(context)
            .setLoadControl(audioLoadControl)
            .build()
    }

    fun injectDataSource(dataSource: PlaylistDataSource) {
        repository.injectDataSource(dataSource)

        viewModelScope.launch {
            repository.currentTrack()?.let {
                setTrack(it)
            }
        }
    }

    fun prevTrack() {
        if ((exoPlayer?.currentPosition ?: 0L) > DefaultPlayerConfig.timeToPreviousTrack) {
            exoPlayer?.seekTo(0)

            return
        }

        viewModelScope.launch {
            repository.previousTrack()?.let {
                setTrack(it)
            }
        }
    }

    fun nextTrack() {
        viewModelScope.launch {
            repository.nextTrack()?.let {
                setTrack(it)
            }
        }
    }

    private suspend fun setTrack(track: Track) {
        fetchImageByUrl(context, track.album.imageUrl)
        setMediaFromUri(track.audioUrl)

        _currentTrack.value = track
        _currentlyPlayTrackId.value = _currentTrack.value?.id

        exoPlayer?.prepare()
    }

    private fun setMediaFromUri(uri: String) {
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

    private fun getPlayerEventListener(): Player.Listener {
        return object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) { }

            override fun onPlaybackStateChanged(state: Int) {
                _isInit.value = true

                if (state != currentPlaybackState.value)
                    currentPlaybackState.value = state


                when (state) {
                    Player.STATE_READY -> {
                        Log.d("Player", "STATE_READY")

                        _currentTrackDuration.value = exoPlayer?.duration ?: 1L
                        exoPlayer?.prepare()

                        if (DefaultPlayerConfig.isAutoplay)
                            exoPlayer?.play()

                        _isLoading.value = false
                    }

                    Player.STATE_ENDED -> {
                        Log.d("Player", "STATE_ENDED")

                        onTrackEnd()
                    }

                    Player.STATE_BUFFERING -> {
                        Log.d("Player", "STATE_BUFFERING")

                        _isLoading.value = true
                    }
                    Player.STATE_IDLE -> {
                        Log.d("Player", "STATE_IDLE")
                    }
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

                Log.e("Player", errorMessage)
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
                Log.d("Player","playWhenReady changed ($playWhenReady)")
            }
        }
    }
}