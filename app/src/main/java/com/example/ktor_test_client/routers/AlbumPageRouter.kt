package com.example.ktor_test_client.routers

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.api.dtos.Album
import com.example.ktor_test_client.api.dtos.BaseAlbum
import com.example.ktor_test_client.data.sources.PlaylistDataSource
import com.example.ktor_test_client.pages.AlbumPage
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel

@Composable
fun AlbumPageRouter(
    albumId: String,
    apiService: MusicApiService,
    navController: NavHostController,
    playerViewModel: AudioPlayerViewModel,
    bottomPadding: Dp,
    context: Context
) {
    var album: Album? by remember { mutableStateOf(null) }
    var otherAlbums: List<BaseAlbum> by remember { mutableStateOf(listOf()) }

    LaunchedEffect(Unit) {
        apiService.getAlbum(albumId).onSuccess { respondAlbum ->
            album = respondAlbum
        }

        apiService.getAlbumsByArtist(album?.artists?.first()?.id ?: "")
            .onSuccess { respondOtherAlbums ->
                otherAlbums = respondOtherAlbums.albums
            }
    }

    when {
        album == null -> Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }

        else -> {
            AlbumPage(
                album = album!!,
                otherAlbums = otherAlbums,
                bottomPadding = bottomPadding,
                onArtistClicked = { artistId ->
                    navController.navigate("ArtistPage/?id=$artistId")
                },
                onAlbumClicked = { otherAlbumId ->
                    navController.navigate("AlbumPage/?id=$otherAlbumId")
                }
            ) { clickedTrack ->
                album?.let { album ->
                    playerViewModel.injectDataSource(
                        context, PlaylistDataSource(
                            tracksId = album.tracks.map { track ->
                                track.id
                            },
                            firstTrack = clickedTrack.indexInAlbum
                        )
                    )

                    playerViewModel.exoPlayer?.prepare()
                }
            }
        }
    }
}