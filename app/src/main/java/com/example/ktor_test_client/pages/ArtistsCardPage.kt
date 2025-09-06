package com.example.ktor_test_client.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktor_test_client.controls.ArtistCardState
import com.example.ktor_test_client.controls.card.SwipeableCardStack
import com.example.ktor_test_client.controls.card.SwipeableCard
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource

@Composable
fun ArtistsCardPage(
    currentCardState: MutableState<ArtistCardState>,
    cards: MutableList<SwipeableCard>,
    cardStates: List<MutableState<ArtistCardState>>,
    hazeState: HazeState,
    modifier: Modifier
) {
    Box(
        modifier = Modifier
            .hazeSource(state = hazeState)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        currentCardState.value.imageBitmap.value?.let {
            AnimatedContent(
                targetState = currentCardState.value.palette.value,
                transitionSpec = {
                    fadeIn(tween(1000)) togetherWith fadeOut(tween(1000))
                },
                label = "image crossfade"
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                it?.swatches?.take(3)?.map { swatch ->
                                    Color(swatch.rgb)
                                } ?: listOf(
                                    Color.Black
                                )
                            )
                        )
                )

                Text(
                    text = "Подборка исполнителей",
                    modifier = modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    color = Color(it?.dominantSwatch?.titleTextColor ?: MaterialTheme.colorScheme.onBackground.toArgb()),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.W700,
                    lineHeight = 36.sp
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
    }
}