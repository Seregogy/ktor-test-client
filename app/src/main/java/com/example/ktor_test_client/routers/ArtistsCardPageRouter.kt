package com.example.ktor_test_client.routers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.api.dtos.BaseArtist
import com.example.ktor_test_client.controls.ArtistCardState
import com.example.ktor_test_client.controls.artistSwipeableCard
import com.example.ktor_test_client.pages.ArtistsCardPage

@Composable
fun ArtistsCardPageRouter(
    modifier: Modifier = Modifier,
    musicApiService: MusicApiService,
    onCardClicked: (artist: BaseArtist) -> Unit
) {
    val artists: MutableState<List<BaseArtist>?> = remember { mutableStateOf(null) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val result = musicApiService.getTopArtists().onSuccess {
            artists.value = it
        }.onFailure {
            isError = true
        }

        println(result)
    }

    when {
        isError -> {
            ErrorState()
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

            ArtistsCardPage(currentCardState, cards, cardStates, modifier)
        }
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.ErrorOutline,
                contentDescription = "something went wrong icon",
                modifier = Modifier
                    .size(120.dp)
            )

            Text(
                text = "Кажется что-то сломалось =(",
                fontWeight = FontWeight.W700
            )
        }
    }
}