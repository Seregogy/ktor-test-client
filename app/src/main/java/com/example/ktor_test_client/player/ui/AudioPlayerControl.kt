package com.example.ktor_test_client.player.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ktor_test_client.control.coloredscaffold.ColoredScaffold
import com.example.ktor_test_client.control.coloredscaffold.ColoredScaffoldState
import com.example.ktor_test_client.player.AudioPlayer
import com.example.ktor_test_client.viewmodel.AudioPlayerViewModel
import io.github.vinceglb.confettikit.compose.ConfettiKit
import io.github.vinceglb.confettikit.core.Angle
import io.github.vinceglb.confettikit.core.Party
import io.github.vinceglb.confettikit.core.Position
import io.github.vinceglb.confettikit.core.Rotation
import io.github.vinceglb.confettikit.core.Spread
import io.github.vinceglb.confettikit.core.emitter.Emitter
import io.github.vinceglb.confettikit.core.models.Shape
import io.github.vinceglb.confettikit.core.models.Size
import kotlin.math.absoluteValue
import kotlin.time.Duration

const val animationsSpeed = 1200

@Composable
fun FullAudioPlayer(
    viewModel: AudioPlayerViewModel,
    modifier: Modifier,
    coloredScaffoldState: ColoredScaffoldState,
    onCollapseRequest: () -> Unit = { },
    onAlbumClicked: (albumId: String) -> Unit,
    onArtistClicked: (artistId: String) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val currentTrack by viewModel.audioPlayer.currentTrack.collectAsStateWithLifecycle()
    val currentLyrics by viewModel.audioPlayer.currentLyrics.collectAsStateWithLifecycle()
    val currentTrackDuration by viewModel.audioPlayer.currentTrackDuration.collectAsStateWithLifecycle()
    val state by viewModel.audioPlayer.currentState.collectAsStateWithLifecycle()
    val currentPosition = viewModel.audioPlayer.currentPosition

    val isLastTrack = viewModel.audioPlayer.isLastTrack

    val snowEnabled = remember { mutableStateOf(true) }

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

    LaunchedEffect(currentTrack) {
        isLyricsOpen.value = isLyricsOpen.value && currentLyrics != null
    }

    ColoredScaffold(coloredScaffoldState) {
        val secondaryColorWithLoadingState by remember {
            derivedStateOf {
                if (isLoading.value) {
                    onBackgroundColorAnimated.value.copy(.5f)
                } else {
                    onBackgroundColorAnimated.value
                }
            }
        }

        Column(
            modifier = Modifier
                .background(backgroundColorAnimated.value)
                .background(additionalVerticalGradientBrush.value)
                .then(modifier)
        ) {
            TopBar(
                currentTrackFullDto = currentTrack?.data,
                onCollapseRequest = onCollapseRequest
            )

            MainContent(
                viewModel = viewModel,
                isLyricsOpen = isLyricsOpen,
                currentPosition = currentPosition,
                screenWidth = screenWidth
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-50).dp)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    TrackInfo(
                        currentTrackFullDto = currentTrack?.data,
                        isTrackLoading = isLoading,
                        onAlbumClicked = onAlbumClicked,
                        onArtistClicked = onArtistClicked
                    )

                    Spacer(Modifier.height(10.dp))

                    PlayerSlider(
                        currentPosition = currentPosition,
                        currentTrackDuration = currentTrackDuration,
                        viewModel = viewModel,
                        isSliding = isSliding
                    )

                    TimingText(
                        secondaryColorWithLoadingState = secondaryColorWithLoadingState,
                        currentPosition = currentPosition,
                        currentTrackDuration =  currentTrackDuration,
                        isSliding = isSliding
                    )
                }

                PlayerNavigationButtons(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    secondaryColorWithLoadingState = secondaryColorWithLoadingState,
                    isPlay = isPlay,
                    isLastTrack = isLastTrack,
                    onNext = { viewModel.audioPlayer.seekToNext() },
                    onPrev = { viewModel.audioPlayer.seekToPrev() },
                    onPlayPause = { viewModel.audioPlayer.playPause() }
                )

                BottomControls(
                    modifier = Modifier
                        .fillMaxWidth(),
                    viewModel = viewModel,
                    snowEnabled = snowEnabled,
                    isLyricsOpen = isLyricsOpen
                )
            }
        }

        for (i in 0 until 1) {
            println("ass")
        }

        AnimatedVisibility(
            snowEnabled.value,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ConfettiKit(
                modifier = Modifier
                    .fillMaxSize(),
                parties = listOf(
                    Party(
                        speed = 0f,
                        maxSpeed = 10f,
                        damping = .15f,
                        timeToLive = 30000,
                        angle = Angle.BOTTOM,
                        spread = Spread.ROUND,
                        colors = listOf(Color.White.toArgb()),
                        shapes = listOf(Shape.Circle),
                        emitter = Emitter(Duration.INFINITE)
                            .perSecond(7),
                        position = Position.Relative(0.0, -0.1)
                            .between(Position.Relative(1.0, -0.1)),
                        rotation = Rotation.disabled(),
                        size = listOf(
                            Size(1),
                            Size(2)
                        )
                    )
                )
            )
        }

    }
}