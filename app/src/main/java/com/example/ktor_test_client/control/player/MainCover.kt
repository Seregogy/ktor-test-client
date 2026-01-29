package com.example.ktor_test_client.control.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ktor_test_client.control.coloredscaffold.ColoredScaffoldState
import com.example.ktor_test_client.viewmodel.AudioPlayerViewModel

const val animationsSpeed = 1200

@Composable
fun ColoredScaffoldState.MainCover(
    viewModel: AudioPlayerViewModel,
    isLyricsOpen: MutableState<Boolean>,
    screenWidth: Dp
) {
    val currentImage by viewModel.bitmap.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .height(screenWidth)
            .offset(y = (-20).dp)
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White,
                            Color.Transparent,
                        )
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
    ) {
        AnimatedVisibility(
            visible = isLyricsOpen.value,
            enter = fadeIn(tween()),
            exit = fadeOut(tween())
        ) {
            LyricsViewport(viewModel)
        }

        AnimatedVisibility(
            visible = !isLyricsOpen.value,
            enter = fadeIn(tween()),
            exit = fadeOut(tween())
        ) {
            currentImage?.let {
                AnimatedContent(
                    targetState = it,
                    transitionSpec = {
                        fadeIn(tween(animationsSpeed)) togetherWith fadeOut(
                            tween(
                                animationsSpeed
                            )
                        )
                    },
                    label = "image animation"
                ) { animatedBitmap ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    ) {
                        Image(
                            bitmap = animatedBitmap.asImageBitmap(),
                            contentDescription = "album image",
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}