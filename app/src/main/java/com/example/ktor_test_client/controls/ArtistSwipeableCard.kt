package com.example.ktor_test_client.controls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ktor_test_client.controls.card.SwipeableCard
import com.example.ktor_test_client.controls.card.SwipeableCardData
import com.example.ktor_test_client.models.Artist
import com.example.ktor_test_client.viewmodels.ArtistCardViewModel

@Composable
fun artistSwipeableCard(
    artist: Artist,
    artistCardState: MutableState<ArtistCardState>,
    onClick: (artist: Artist) -> Unit
) : SwipeableCard {
    val viewModel = ArtistCardViewModel()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchImageByUrl(context, artist.imagesUrl.first().first)
    }

    LaunchedEffect(Unit) {
        viewModel.bitmap.collect {
            artistCardState.value.imageBitmap.value = it
        }
    }

    LaunchedEffect(Unit) {
        viewModel.palette.collect {
            if (it?.swatches != null) {
                artistCardState.value.palette.value = it
            }
        }
    }

    return SwipeableCard(
        cardData = SwipeableCardData(
            cardVerticalOffset = 30.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(.8f)
                .aspectRatio(.7f)
                .clip(MaterialTheme.shapes.medium)
                .clickable {
                    onClick(artist)
                }
        ) {
            ArtistCard(
                artist,
                artistCardState,
                ArtistCardViewModel()
            )
        }
    }
}