package com.example.ktor_test_client.routers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.Dp
import com.example.ktor_test_client.api.dtos.BaseTrack
import com.example.ktor_test_client.controls.states.ErrorState
import com.example.ktor_test_client.controls.states.LoadingState
import com.example.ktor_test_client.pages.ArtistPage
import com.example.ktor_test_client.viewmodels.ArtistViewModel
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ArtistHomePageRouter(
    artistId: String?,
    playerViewModel: AudioPlayerViewModel,
    bottomPadding: Dp,
    onTrackClicked: (clickedTrack: BaseTrack) -> Unit,
    onAlbumClicked: (albumId: String) -> Unit
) {
    val viewModel: ArtistViewModel = koinViewModel()

    LaunchedEffect(Unit) {
        artistId?.let {
            viewModel.loadArtist(it)
            viewModel.loadTopTracks(it, 9)
            viewModel.loadAlbums(it)
        }
    }

    when {
        artistId == null -> {
            ErrorState()
        }
        viewModel.artist.value == null -> {
            LoadingState()
        }
        else -> {
            ArtistPage(
                viewModel = viewModel,
                onTrackClicked = onTrackClicked,
                bottomPadding = bottomPadding,
                onAlbumClicked = onAlbumClicked
            )
        }
    }
}