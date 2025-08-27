package com.example.ktor_test_client.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.api.dtos.Album
import com.example.ktor_test_client.api.dtos.BaseAlbum
import com.example.ktor_test_client.data.sources.PlaylistDataSource
import kotlinx.coroutines.launch

class AlbumViewModel(
    private val apiService: MusicApiService
) : ImagePaletteViewModel() {
    private lateinit var albumsPlaylist: PlaylistDataSource

    private val _album = mutableStateOf<Album?>(null)
    val album: State<Album?> = _album

    private val _otherAlbums = mutableStateOf<List<BaseAlbum>?>(listOf())
    val otherAlbums: State<List<BaseAlbum>?> = _otherAlbums


    fun loadAlbum(albumId: String) {
        viewModelScope.launch {
            apiService.getAlbum(albumId).onSuccess {
                _album.value = it

                album.value?.artists?.first()?.let { artist ->
                    loadOtherAlbums(artist.id)
                }
            }

            album.value?.let {
                albumsPlaylist = PlaylistDataSource(
                    tracksId = album.value!!.tracks.map { track ->
                        track.id
                    }
                )
            }
        }
    }

    private fun loadOtherAlbums(artistId: String) {
        viewModelScope.launch {
            apiService.getAlbumsByArtist(artistId).onSuccess {
                _otherAlbums.value = it
            }
        }
    }
}