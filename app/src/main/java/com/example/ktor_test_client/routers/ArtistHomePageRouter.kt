package com.example.ktor_test_client.routers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.ktor_test_client.controls.states.ErrorState
import com.example.ktor_test_client.controls.states.LoadingState
import com.example.ktor_test_client.pages.ArtistHomePage
import com.example.ktor_test_client.viewmodels.ArtistViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ArtistHomePageRouter(
    artistId: String?
) {
    val viewModel: ArtistViewModel = koinViewModel()

    LaunchedEffect(Unit) {
        artistId?.let {
            viewModel.loadArtist(it)
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
            ArtistHomePage(viewModel)
        }
    }
}