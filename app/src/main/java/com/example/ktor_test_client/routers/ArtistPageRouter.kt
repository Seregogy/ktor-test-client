package com.example.ktor_test_client.routers

import android.util.Log
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.Dp
import com.example.ktor_test_client.api.dtos.BaseTrack
import com.example.ktor_test_client.controls.states.ErrorState
import com.example.ktor_test_client.controls.states.LoadingState
import com.example.ktor_test_client.pages.ArtistPage
import com.example.ktor_test_client.viewmodels.ArtistViewModel
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ArtistPageRouter(
    artistId: String?,
    playerViewModel: AudioPlayerViewModel,
    innerPadding: PaddingValues,
    bottomPadding: Dp,
    hazeState: HazeState,
    onBackRequest: () -> Unit,
    onTrackClicked: (clickedTrack: BaseTrack) -> Unit,
    onAlbumClicked: (albumId: String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel: ArtistViewModel = koinViewModel()

    var isError by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        artistId?.let {
            runCatching {
                viewModel.loadArtist(it)
                viewModel.loadTopTracks(it, 9)
                viewModel.loadAlbums(it)
            }.onSuccess {
                Log.d("API", "Artist page loaded")
            }.onFailure {
                isError = true
            }
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
            artistId == null || isError -> {
                ErrorState(onBackRequest)
            }
            viewModel.artist.value == null -> {
                LoadingState()
            }
            else -> {
                ArtistPage(
                    viewModel = viewModel,
                    innerPadding = innerPadding,
                    onTrackClicked = onTrackClicked,
                    hazeState = hazeState,
                    bottomPadding = bottomPadding,
                    onBackRequest = onBackRequest,
                    onAlbumClicked = onAlbumClicked
                )
            }
        }
    }
}