package com.example.ktor_test_client.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.api.dtos.Artist
import com.example.ktor_test_client.api.dtos.BaseAlbum
import com.example.ktor_test_client.api.dtos.BaseTrack
import kotlinx.coroutines.launch

class ArtistViewModel(
    private val apiService: MusicApiService
) : ImagePaletteViewModel() {
    private val _artist = mutableStateOf<Artist?>(null)
    val artist: State<Artist?> = _artist

    private val _topTracks = mutableStateOf<List<BaseTrack>?>(null)
    val topTracks: State<List<BaseTrack>?> = _topTracks

    private val _albums = mutableStateOf<List<BaseAlbum>?>(null)
    val albums: State<List<BaseAlbum>?> = _albums

    fun loadArtist(artistId: String) {
        viewModelScope.launch {
            apiService.getArtist(artistId).onSuccess {
                _artist.value = it.artist
            }
        }
    }

    fun loadTopTracks(artistId: String, limit: Int = 9) {
        viewModelScope.launch {
            apiService.getArtistTopTracks(artistId, limit).onSuccess {
                _topTracks.value = it
            }
        }
    }

    fun loadAlbums(artistId: String) {
        viewModelScope.launch {
            apiService.getAlbumsByArtist(artistId).onSuccess {
                _albums.value = it
            }
        }
    }
}