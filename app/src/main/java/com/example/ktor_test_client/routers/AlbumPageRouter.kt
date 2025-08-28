package com.example.ktor_test_client.routers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import com.example.ktor_test_client.controls.states.ErrorState
import com.example.ktor_test_client.controls.states.LoadingState
import com.example.ktor_test_client.data.sources.PlaylistDataSource
import com.example.ktor_test_client.pages.AlbumPage
import com.example.ktor_test_client.viewmodels.AlbumViewModel
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import kotlinx.coroutines.async
import okhttp3.internal.wait
import org.koin.androidx.compose.koinViewModel

@Composable
fun AlbumPageRouter(
    albumId: String?,
    bottomPadding: Dp,
    playerViewModel: AudioPlayerViewModel,
    onArtistClicked: (artistId: String) -> Unit,
    onAlbumClicked: (otherAlbumId: String) -> Unit
) {
    val context = LocalContext.current
    val albumViewModel: AlbumViewModel = koinViewModel()

    LaunchedEffect(Unit) {
        albumId?.let { albumId ->
            async {
                albumViewModel.loadAlbum(albumId)
            }.invokeOnCompletion {
                async {
                    albumViewModel.album.value?.let { album ->
                        albumViewModel.fetchImageByUrl(context, album.imageUrl ?: "")
                    }
                }
            }
        }
    }

    when {
        albumId == null -> {
            ErrorState()
        }
        albumViewModel.album.value == null -> {
            LoadingState()
        }
        else -> {
            AlbumPage(
                viewModel = albumViewModel,
                bottomPadding = bottomPadding,
                onArtistClicked = onArtistClicked,
                onAlbumClicked = onAlbumClicked
            ) { clickedTrack ->
                albumViewModel.album.value?.let { album ->
                    playerViewModel.injectDataSource(PlaylistDataSource(
                        tracksId = album.tracks.map { it.id },
                        firstTrack = clickedTrack.indexInAlbum
                    ))

                    playerViewModel.exoPlayer?.prepare()
                }
            }
        }
    }
}