package com.example.ktor_test_client.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.api.dtos.Artist
import com.example.ktor_test_client.api.dtos.BaseAlbum
import com.example.ktor_test_client.api.dtos.BaseTrackWithArtists
import kotlinx.coroutines.launch

class ArtistViewModel(
    private val artistId: String,
    private val apiService: MusicApiService
) : ImagePaletteViewModel() {
    private val _artist = mutableStateOf<Artist?>(null)
    val artist: State<Artist?> = _artist

    private val _latestRelease = mutableStateOf<Pair<BaseAlbum, Long>?>(null)
    val latestRelease: State<Pair<BaseAlbum, Long>?> = _latestRelease

    private val _topTracks = mutableStateOf<List<BaseTrackWithArtists>?>(null)
    val topTracks: State<List<BaseTrackWithArtists>?> = _topTracks

    private val _albums = mutableStateOf<List<BaseAlbum>?>(null)
    val albums: State<List<BaseAlbum>?> = _albums

    private val _singles = mutableStateOf<List<BaseAlbum>?>(null)
    val singles: State<List<BaseAlbum>?> = _singles

    fun loadArtist() {
        viewModelScope.launch {
            apiService.getArtist(artistId).onSuccess {
                _artist.value = it.artist
            }
        }
    }

    fun loadTopTracks(limit: Int = 9) {
        viewModelScope.launch {
            apiService.getArtistTopTracks(artistId, limit).onSuccess {
                _topTracks.value = it
            }
        }
    }

    fun loadAlbums() {
        viewModelScope.launch {
            apiService.getAlbumsByArtist(artistId).onSuccess {
                _albums.value = it
            }
        }
    }

    fun loadSingles() {
        viewModelScope.launch {
            apiService.getSinglesByArtist(artistId).onSuccess {
                _singles.value = it
            }
        }
    }

    fun loadLatestRelease() {
        viewModelScope.launch {
            apiService.getLastReleaseByArtist(artistId).onSuccess {
                _latestRelease.value = it
            }
        }
    }
}