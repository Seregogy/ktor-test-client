package com.example.ktor_test_client.player.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ktor_test_client.controls.coloredscaffold.ColoredScaffold
import com.example.ktor_test_client.controls.coloredscaffold.rememberColoredScaffoldState
import com.example.ktor_test_client.player.AudioPlayer
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel

const val animationsSpeed = 1200

@Composable
fun FullAudioPlayer(
    viewModel: AudioPlayerViewModel,
    modifier: Modifier,
    onCollapseRequest: () -> Unit = { },
    onAlbumClicked: (albumId: String) -> Unit,
    onArtistClicked: (artistId: String) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val currentTrack by viewModel.audioPlayer.currentTrack.collectAsStateWithLifecycle()
    val bitmap by viewModel.bitmap.collectAsStateWithLifecycle()
    val currentTrackDuration by viewModel.audioPlayer.currentTrackDuration.collectAsStateWithLifecycle()
    val state by viewModel.audioPlayer.currentState.collectAsStateWithLifecycle()
    val currentPosition = viewModel.audioPlayer.currentPosition

    val isLastTrack = viewModel.audioPlayer.isLastTrack

    val isPlay = remember {
        derivedStateOf {
            state == AudioPlayer.AudioPlayerState.Play
        }
    }
    val isLoading = remember {
        derivedStateOf {
            state == AudioPlayer.AudioPlayerState.Loading
        }
    }

    val isSliding = remember { mutableStateOf(false) }
    val isLyricsOpen = remember { mutableStateOf(false) }

    ColoredScaffold(
        state = rememberColoredScaffoldState(tween(animationsSpeed)) {
            viewModel.palette.collectAsStateWithLifecycle()
        }
    ) {
        AnimatedContent(
            targetState = currentPalette.value,
            transitionSpec = {
                fadeIn(tween(animationsSpeed)) togetherWith fadeOut(tween(animationsSpeed))
            },
            label = "image crossfade"
        ) { palette ->
            Column(
                modifier = Modifier
                    .background(backgroundColorAnimated.value)
                    .then(
                        if (palette?.swatches != null) {
                            Modifier.background(
                                brush = Brush.verticalGradient(
                                    colors = currentPalette.value!!.swatches.takeLast(3).mapNotNull { Color(it.rgb).copy(.2f) }
                                )
                            )
                        } else {
                            Modifier
                        }
                    )

                    .then(modifier)
            ) {
                TopBar(
                    currentTrackFullDto = currentTrack?.data,
                    onCollapseRequest = onCollapseRequest
                )

                Box(
                    modifier = Modifier
                        .heightIn(min = screenWidth, max = screenWidth)
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
                    val scroll = rememberScrollState()
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isLyricsOpen.value,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = currentTrack?.data?.lyrics ?: LoremIpsum().values.take(15).joinToString { it },
                            modifier = Modifier
                                .padding(horizontal = 40.dp)
                                .verticalScroll(scroll)
                                .padding(vertical = 100.dp)
                        )
                    }

                    androidx.compose.animation.AnimatedVisibility(
                        visible = !isLyricsOpen.value,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        bitmap?.let {
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

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = (-50).dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        TrackInfo(
                            currentTrackFullDto = currentTrack?.data,
                            isTrackLoading = isLoading,
                            onAlbumClicked = onAlbumClicked,
                            onArtistClicked = onArtistClicked
                        )

                        PlayerControls(
                            currentPosition = currentPosition,
                            currentTrackDuration = currentTrackDuration,
                            viewModel = viewModel,
                            isPlay = isPlay,
                            isSliding = isSliding,
                            isTrackLoading = isLoading,
                            isLastTrack = isLastTrack,
                            isLyricsOpen = isLyricsOpen,
                            onNext = { viewModel.audioPlayer.seekToNext() },
                            onPrev = { viewModel.audioPlayer.seekToPrev() },
                            onPlayPause = { viewModel.audioPlayer.playPause() }
                        )
                    }
                }
            }
        }
    }
}