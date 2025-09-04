package com.example.ktor_test_client.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.api.dtos.Album
import com.example.ktor_test_client.api.dtos.BaseAlbum
import com.example.ktor_test_client.data.sources.PlaylistDataSource

class AlbumViewModel(
    private val apiService: MusicApiService
) : ImagePaletteViewModel() {
    private lateinit var albumsPlaylist: PlaylistDataSource

    private val _album = mutableStateOf<Album?>(null)
    val album: State<Album?> = _album

    private val _otherAlbums = mutableStateOf<List<BaseAlbum>>(listOf())
    val otherAlbums: State<List<BaseAlbum>> = _otherAlbums

    private val _singles = mutableStateOf<List<BaseAlbum>>(listOf())
    val singles: State<List<BaseAlbum>> = _singles

    suspend fun loadAlbum(context: Context, albumId: String) {
        apiService.getAlbum(albumId).onSuccess {
            _album.value = it

            album.value?.artists?.first()?.let { artist ->
                loadOtherAlbums(artist.id)
            }

            fetchImageByUrl(context, album.value?.imageUrl ?: "")
        }

        album.value?.let {
            albumsPlaylist = PlaylistDataSource(
                tracksId = album.value!!.tracks.map { track ->
                    track.id
                }
            )
        }
    }

    suspend fun loadSingles() {
        album.value?.artists?.first()?.id?.let { artistId ->
            apiService.getSinglesByArtist(artistId).onSuccess {
                Log.d("API", "singles: $it")
            }.onFailure {
                Log.e("API", "throws: $it")
            }
        }
    }

    private suspend fun loadOtherAlbums(artistId: String) {
        apiService.getAlbumsByArtist(artistId).onSuccess {
            _otherAlbums.value = it
        }
    }
}