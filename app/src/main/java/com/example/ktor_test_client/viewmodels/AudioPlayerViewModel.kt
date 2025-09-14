package com.example.ktor_test_client.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.IntState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.source.preload.PreloadManagerListener
import androidx.media3.session.MediaController
import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.data.repositories.Repository
import com.example.ktor_test_client.data.sources.DataSource
import com.example.ktor_test_client.data.sources.LazyPlaylistDataSource
import com.example.ktor_test_client.data.sources.PlaylistDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
    val mediaController: MediaController,
    val context: Context
) : ImagePaletteViewModel() {
    companion object {
        private val _currentlyPlayTrackId: MutableState<String?> = mutableStateOf(null)
        val currentlyPlayTrackId: State<String?> = _currentlyPlayTrackId
    }

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _currentTrackDuration: MutableState<Long> = mutableLongStateOf(1L)
    val currentTrackDuration: State<Long> = _currentTrackDuration

    private val _isPlay: MutableState<Boolean> = mutableStateOf(false)
    val isPlay: State<Boolean> = _isPlay

    private var _currentTrack: MutableStateFlow<Track?> = MutableStateFlow(null)

    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val _currentPlaylist: MutableMap<MediaItem, Track> = mutableMapOf()
    val currentPlaylist: Map<MediaItem, Track> = _currentPlaylist

    private var _currentIndexInPlaylist: MutableIntState = mutableIntStateOf(0)
    val currentIndexInPlaylist: IntState = _currentIndexInPlaylist


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

        mediaController.addListener(eventListener)

        viewModelScope.launch {
            prepareCurrentTrack()
            prepareNextTrack()
        }
    }

    fun injectDataSource(dataSource: DataSource) {
        viewModelScope.launch {
            repository.injectDataSource(dataSource)

            if (dataSource is LazyPlaylistDataSource) {
                mediaController.setMediaItems(dataSource.tracksId.map { MediaItem.Builder()
                    .setUri(Uri.parse(it))
                    .build()
                })
            }

            prepareCurrentTrack()
            prepareNextTrack()
        }
    }

    fun prevTrack() {
        if ((mediaController.currentPosition) > DefaultPlayerConfig.timeToPreviousTrack) {
            mediaController.seekTo(0)
        } else {
            viewModelScope.launch {
                mediaController.seekToPrevious()
                _currentIndexInPlaylist.intValue--
            }

        }
    }

    fun nextTrack() {
        viewModelScope.launch {
            if (repository.dataSource is LazyPlaylistDataSource) {
                mediaController.seekToNext()

                _currentIndexInPlaylist.intValue++
            } else {
                prepareNextTrack()
                mediaController.seekToNext()
                _currentIndexInPlaylist.intValue++
            }
        }
    }

    private suspend fun prepareCurrentTrack() {
        repository.currentTrack()?.let {
            mediaController.setMediaItem(prepareTrack(it))
            mediaController.prepare()
        }
    }

    private suspend fun prepareNextTrack() {
        repository.nextTrack()?.let {
            mediaController.addMediaItem(prepareTrack(it))
        }
    }

    private fun prepareTrack(track: Track): MediaItem {
        return MediaItem.Builder()
            .setUri(Uri.parse(track.audioUrl))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(track.name)
                    .setAlbumTitle(track.album.name)
                    .setTrackNumber(track.indexInAlbum)
                    .setArtworkUri(Uri.parse(track.imageUrl))
                    .setArtist(track.album.artists.joinToString(",") { it.name })
                    .build()
            )
            .build().also {
                _currentPlaylist[it] = track
            }
    }

    fun playPause() {
        mediaController.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    fun seekTo(position: Long) {
        mediaController.seekTo(position)
    }

    override fun onCleared() {
        super.onCleared()

        releasePlayer()
    }

    fun releasePlayer() {
        mediaController.removeListener(eventListener)

        mediaController.stop()
        mediaController.release()
    }

    private fun getPlayerEventListener(): Player.Listener {
        return object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)

                _currentTrack.value = _currentPlaylist[mediaItem]
                _currentTrackDuration.value = mediaController.contentDuration.coerceIn(1..Long.MAX_VALUE)

                viewModelScope.launch {
                    prepareNextTrack()
                    fetchImageByUrl(context, mediaItem?.mediaMetadata?.artworkUri.toString())
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_READY -> {
                        _currentTrackDuration.value = mediaController.duration

                        if (DefaultPlayerConfig.isAutoplay)
                            mediaController.play()

                        _isLoading.value = false
                    }

                    Player.STATE_ENDED -> {
                        onTrackEnd()
                    }
                    Player.STATE_BUFFERING -> {
                        _isLoading.value = true
                    }

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

                Log.e("Player", errorMessage)

                //TODO: сомнительно, нужно рассмотреть смысл этой конструкции
                mediaController.currentMediaItem?.let {
                    mediaController.setMediaItem(it)

                    mediaController.prepare()
                    mediaController.seekTo(mediaController.currentPosition)
                    mediaController.play()
                    mediaController.playWhenReady = true
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlay.value = isPlaying
            }
        }
    }
}