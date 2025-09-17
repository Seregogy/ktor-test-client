package com.example.ktor_test_client.data

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.collection.LruCache
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.example.ktor_test_client.api.dtos.TrackFullDto
import com.example.ktor_test_client.data.repositories.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class Track(
    val data: TrackFullDto,
    val mediaItem: MediaItem
)

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

class AudioPlayer(
    private val mediaController: MediaController,
    private val tracksCache: TracksCache,
    private val repository: Repository,
    private val context: Context
) {
    companion object {
        private val _currentlyPlayTrackId: MutableState<String?> = mutableStateOf(null)
        val currentlyPlayTrackId: State<String?> = _currentlyPlayTrackId
    }

    private val _playlist = MutableStateFlow<MutableList<Track>>(mutableListOf())
    val playlist: StateFlow<List<Track>> = _playlist

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack

    private val _currentTrackDuration = MutableStateFlow(1L)
    val currentTrackDuration: StateFlow<Long> = _currentTrackDuration

    private val _currentState: MutableStateFlow<AudioPlayerState> = MutableStateFlow(AudioPlayerState.Idle)
    val currentState: StateFlow<AudioPlayerState> = _currentState

    val currentPosition = MutableStateFlow<Long>(0)

    private val listener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)

            _currentTrack.value = _playlist.value.first { it.mediaItem == mediaItem }.also {
                _currentlyPlayTrackId.value = it.data.id
            }
        }

        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)

            when (state) {
                Player.STATE_READY -> {
                    _currentState.value = AudioPlayerState.Ready
                    _currentTrackDuration.value = mediaController.contentDuration
                }
                Player.STATE_ENDED -> {
                    _currentState.value = AudioPlayerState.Ended
                }
                Player.STATE_BUFFERING -> {
                    _currentState.value = AudioPlayerState.Loading
                }
                Player.STATE_IDLE -> {
                    _currentState.value = AudioPlayerState.Idle
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            Log.e("Player", "${error.message}\ncause: ${error.cause}\ncode:${error.errorCode}")
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                _currentState.value = AudioPlayerState.Play
            } else {
                _currentState.value = AudioPlayerState.Pause
            }
        }
    }

    init {
        mediaController.addListener(listener)

        CoroutineScope(Dispatchers.Main).launch {
            do {
                currentPosition.value = mediaController.currentPosition

                Log.d("Player", "${currentPosition.value.toString().padStart(6, '0')}/${currentTrackDuration.value}")
                delay(350)
            } while (_currentState.value != AudioPlayerState.Released)
        }
    }

    fun playPause() {
        if (mediaController.isPlaying)
            pause()
        else
            play()
    }

    fun seek(positionMs: Long) {
        mediaController.seekTo(positionMs)
    }

    fun seekToNext() {
        mediaController.seekToNext()
    }

    fun seekToPrev() {
        mediaController.seekToPrevious()
    }

    fun seekToIndex(index: Int): Boolean {
        if ((index in _playlist.value.indices).not()) return false

        mediaController.seekToDefaultPosition(index)
        return true
    }

    fun play() {
        mediaController.play()
    }

    fun pause() {
        mediaController.pause()
    }

    suspend fun loadPlaylist(trackIds: List<String>) {
        val cachedTracks = tracksCache.loadFromCache(trackIds)
        val newTracks = trackIds - cachedTracks.map { it.data.id }.toSet()

        tracksCache.putAll(
            newTracks.mapNotNull { trackId ->
            repository.getTrack(trackId)?.let { track ->
                Track(
                    data = track,
                    mediaItem = MediaItem.Builder()
                        .setMediaId(trackId)
                        .setUri(track.audioUrl)
                        .build().apply {
                            mediaMetadata.buildUpon()
                                .setTitle(track.name)
                                .setAlbumTitle(track.album.name)
                                .setDisplayTitle(track.name)
                                .setArtist(track.album.artists.joinToString(",") { track.name })
                                .setArtworkUri(Uri.parse(track.imageUrl))
                                .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                                .build()
                        }
                )
            }
        })
        val newCachedTracks = tracksCache.loadFromCache(trackIds)

        _playlist.value.addAll(newCachedTracks)

        mediaController.addMediaItems(newCachedTracks.map { it.mediaItem }.toMutableList())
        mediaController.prepare()
        mediaController.play()
    }

    suspend fun lazyClearPlaylist() {
        _currentState.first { it != AudioPlayerState.Play }

        mediaController.clearMediaItems()
        _playlist.value.clear()
    }

    fun release() {
        mediaController.stop()
        mediaController.release()
        mediaController.removeListener(listener)

        _currentState.value = AudioPlayerState.Released
    }

    enum class AudioPlayerState {
        Idle,
        Ready,
        Play,
        Pause,
        Ended,
        Loading,
        Released
    }
}

class TracksCache {
    private companion object {
        val lruCache: LruCache<String, Track> = LruCache(maxSize = 100)
    }

    fun putAll(tracks: List<Track>) {
        tracks.forEach {
            put(it)
        }

        Log.d("Cache", tracks.joinToString { it.data.name })
    }

    fun put(track: Track) {
        lruCache.put(track.data.id, track)
    }

    fun loadFromCache(trackIds: List<String>): List<Track> = trackIds.mapNotNull {
        lruCache[it]
    }
}

