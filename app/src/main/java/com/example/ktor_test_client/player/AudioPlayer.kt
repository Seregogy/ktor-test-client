package com.example.ktor_test_client.player

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
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

class AudioPlayer(
    private val mediaController: MediaController,
    private val mediaCache: MediaCache,
    private val repository: Repository,
    private val context: Context
) {
    companion object {
        private val _currentlyPlayTrackId: MutableState<String?> = mutableStateOf(null)
        val currentlyPlayTrackId: State<String?> = _currentlyPlayTrackId
    }

    private val _playlist = MutableStateFlow<MutableList<Track>>(mutableListOf())
    val playlist: StateFlow<List<Track>> = _playlist

    private val _isLastTrack = mutableStateOf(false)
    val isLastTrack: State<Boolean> = _isLastTrack

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack

    private val _currentTrackDuration = MutableStateFlow(1L)
    val currentTrackDuration: StateFlow<Long> = _currentTrackDuration

    private val _currentState: MutableStateFlow<AudioPlayerState> = MutableStateFlow(
        AudioPlayerState.Idle
    )
    val currentState: StateFlow<AudioPlayerState> = _currentState

    val currentPosition: MutableState<Long> = mutableLongStateOf(0L)

    private val listener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)

            _currentTrack.value = _playlist.value.first { it.mediaItem == mediaItem }.also {
                _currentlyPlayTrackId.value = it.data.id
                _isLastTrack.value = _playlist.value.indexOf(it) == _playlist.value.indices.last
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

    suspend fun loadPlaylist(tracks: List<String>) {
        val cachedTracks = preparePlaylistTracks(tracks)

        _playlist.value.addAll(cachedTracks)

        mediaController.apply {
            addMediaItems(cachedTracks.map { it.mediaItem })
            prepare()
            play()
        }
    }

    suspend fun setPlaylist(tracks: List<String>, startTrackIndex: Int = 0) {
        val cachedTracks = preparePlaylistTracks(tracks)

        _playlist.value.clear()
        _playlist.value.addAll(cachedTracks)

        mediaController.apply {
            stop()
            setMediaItems(cachedTracks.map { it.mediaItem })
            prepare()
            seekToDefaultPosition(startTrackIndex)
            play()
        }
    }

    private suspend fun preparePlaylistTracks(tracks: List<String>): List<Track> {
        val cachedTracks = mediaCache.loadFromCache(tracks)
        val uncachedTracks = tracks - cachedTracks.map { it.data.id }.toSet()

        repository.getTracks(uncachedTracks)?.zip(uncachedTracks) { track, trackId ->
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
        }?.let {
            mediaCache.putAll(it)
            Log.d("Player", "Put all success")
        }

        return mediaCache.loadFromCache(tracks)
    }

    suspend fun lazyClearPlaylist() {
        _currentState.first { it != AudioPlayerState.Play }

        mediaController.stop()
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