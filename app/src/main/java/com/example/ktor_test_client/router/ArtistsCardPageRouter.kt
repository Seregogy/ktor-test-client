package com.example.ktor_test_client.router

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.api.dtos.BaseArtist
import com.example.ktor_test_client.control.ArtistCardState
import com.example.ktor_test_client.control.artistSwipeableCard
import com.example.ktor_test_client.control.states.ErrorState
import com.example.ktor_test_client.control.states.LoadingState
import com.example.ktor_test_client.page.ArtistsCardPage
import dev.chrisbanes.haze.HazeState

@Composable
fun ArtistsCardPageRouter(
    modifier: Modifier = Modifier,
    musicApiService: MusicApiService,
    hazeState: HazeState,
    onCardClicked: (artist: BaseArtist) -> Unit
) {
    val artists: MutableState<List<BaseArtist>?> = remember { mutableStateOf(null) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        musicApiService.getTopArtists().onSuccess {
            artists.value = it
        }.onFailure {
            isError = true
        }
    }

    when {
        isError -> {
            ErrorState(onRequestReturn = {  })
        }
        artists.value == null -> {
            LoadingState()
        }
        else -> {
            val cardStates = List(artists.value!!.size) { remember { mutableStateOf(ArtistCardState()) } }
            val cards = artists.value!!.zip(cardStates) { artist, state ->
                artistSwipeableCard(artist, state) {
                    onCardClicked(it)
                }
            }.toMutableList()

            val currentCardState = cardStates.first()

            ArtistsCardPage(currentCardState, cards, cardStates, hazeState, modifier)
        }
    }
}