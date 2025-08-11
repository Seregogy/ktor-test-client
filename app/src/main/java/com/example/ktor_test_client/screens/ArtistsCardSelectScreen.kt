package com.example.ktor_test_client.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktor_test_client.Library
import com.example.ktor_test_client.controls.ArtistCardState
import com.example.ktor_test_client.controls.SwipeableCardStack
import com.example.ktor_test_client.controls.artistSwipeableCard
import com.example.ktor_test_client.controls.card
import com.example.ktor_test_client.models.Artist

@Composable
fun ArtistsCardSwipeables(
    modifier: Modifier = Modifier,
    onCardClicked: (artist: Artist) -> Unit
) {
    val cardStates = List(Library.artists.size) { remember { mutableStateOf(ArtistCardState()) } }

    val cards = Library.artists.zip(cardStates) { artist, state ->
        artistSwipeableCard(artist, state) {
            onCardClicked(it)
        }
    }.toMutableList()

    val currentCardState = remember { mutableStateOf(ArtistCardState()) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        currentCardState.value.imageBitmap.value?.let {
            AnimatedContent(
                targetState = currentCardState.value.imageBitmap,
                transitionSpec = {
                    fadeIn(animationSpec = tween(1000)) togetherWith fadeOut(animationSpec = tween(1000))
                },
                label = "image crossfade"
            ) {
                Image(
                    bitmap = it.value!!.asImageBitmap(),
                    contentDescription = "backgroundImage",
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(2f)
                        .blur(200.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }

        SwipeableCardStack(
            modifier = Modifier
                .fillMaxSize(),
            maxVisibleSize = 3,
            cards = cards,
            onCardSelected = {
                currentCardState.value = cardStates[it.cardState.value.initialIndex].value
            }
        )

        Text(
            text = "Подборка исполнителей",
            modifier = modifier
                .align(Alignment.TopStart)
                .padding(10.dp),
            fontSize = 48.sp,
            fontWeight = FontWeight.W700,
            lineHeight = 32.sp
        )
    }
}
