package com.example.ktor_test_client.routers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.Dp
import com.example.ktor_test_client.controls.states.ErrorState
import com.example.ktor_test_client.controls.states.LoadingState
import com.example.ktor_test_client.pages.AlbumPage
import com.example.ktor_test_client.viewmodels.AlbumViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AlbumPageRouter(
    albumId: String?,
    bottomPadding: Dp,
    onArtistClicked: (artistId: String) -> Unit,
    onAlbumClicked: (otherAlbumId: String) -> Unit
) {
    val albumViewModel: AlbumViewModel = koinViewModel()

    LaunchedEffect(Unit) {
        albumId?.let {
            albumViewModel.loadAlbum(it)
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
            ) { _ ->  }
        }
    }
}