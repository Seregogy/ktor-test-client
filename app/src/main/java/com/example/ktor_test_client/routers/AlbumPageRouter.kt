package com.example.ktor_test_client.routers

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import com.example.ktor_test_client.controls.states.ErrorState
import com.example.ktor_test_client.controls.states.LoadingState
import com.example.ktor_test_client.data.sources.PlaylistDataSource
import com.example.ktor_test_client.pages.AlbumPage
import com.example.ktor_test_client.viewmodels.AlbumViewModel
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun AlbumPageRouter(
    albumId: String?,
    bottomPadding: Dp,
    playerViewModel: AudioPlayerViewModel,
    onArtistClicked: (artistId: String) -> Unit,
    onAlbumClicked: (otherAlbumId: String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val albumViewModel: AlbumViewModel = koinViewModel()

    LaunchedEffect(Unit) {
        albumId?.let { albumId ->
            albumViewModel.loadAlbum(context, albumId)
        }
    }


    var isRefreshing by remember { mutableStateOf(false) }
    PullToRefreshBox(
        modifier = Modifier
            .fillMaxSize(),
        isRefreshing = isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                isRefreshing = true

                delay(2000)

                isRefreshing = false
            }
        }
    ) {
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
                        playerViewModel.injectDataSource(
                            PlaylistDataSource(
                                tracksId = album.tracks.map { it.id },
                                firstTrack = clickedTrack.indexInAlbum
                            )
                        )

                        playerViewModel.exoPlayer?.prepare()
                    }
                }
            }
        }
    }
}