package com.example.ktor_test_client.router

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import com.example.ktor_test_client.control.states.ErrorState
import com.example.ktor_test_client.control.states.LoadingState
import com.example.ktor_test_client.page.album.AlbumPage
import com.example.ktor_test_client.page.album.AlbumPageSkeleton
import com.example.ktor_test_client.viewmodel.AlbumViewModel
import com.example.ktor_test_client.viewmodel.AudioPlayerViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun AlbumPageRouter(
    albumId: String?,
    playerViewModel: AudioPlayerViewModel,
    innerPadding: PaddingValues,
    bottomPadding: Dp,
    hazeState: HazeState,
    onArtistClicked: (artistId: String) -> Unit,
    onAlbumClicked: (otherAlbumId: String) -> Unit,
    onBackRequest: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val albumViewModel: AlbumViewModel = koinInject()

    var isError by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        albumId?.let { albumId ->
            runCatching {
                albumViewModel.loadAlbum(context, albumId)
            }.onSuccess {
                albumViewModel.loadSingles()
                albumViewModel.loadTracks()
            }.onFailure {
                isError = true
            }
        }
    }


    var isRefreshing by remember { mutableStateOf(false) }
    PullToRefreshBox(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
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
            albumId == null || isError -> {
                ErrorState(onBackRequest)
            }

            albumViewModel.album.value == null -> {
                AlbumPageSkeleton()
            }

            else -> {
                AlbumPage(
                    viewModel = albumViewModel,
                    innerPadding = innerPadding,
                    bottomPadding = bottomPadding,
                    hazeState = hazeState,
                    onBackRequest = onBackRequest,
                    onArtistClicked = onArtistClicked,
                    onAlbumClicked = onAlbumClicked,
                ) { clickedTrack ->
                    albumViewModel.tracks.value?.let { tracks ->
                        coroutineScope.launch {
                            playerViewModel.audioPlayer.setPlaylist(
                                tracks = tracks.map { it.id },
                                startTrackIndex = clickedTrack.indexInAlbum
                            )
                        }
                    }
                }
            }
        }
    }
}