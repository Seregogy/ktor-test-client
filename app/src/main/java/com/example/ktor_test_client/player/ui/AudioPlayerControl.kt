package com.example.ktor_test_client.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ktor_test_client.control.coloredscaffold.ColoredScaffold
import com.example.ktor_test_client.control.coloredscaffold.ColoredScaffoldState
import com.example.ktor_test_client.player.AudioPlayer
import com.example.ktor_test_client.viewmodel.AudioPlayerViewModel

const val animationsSpeed = 1200

@Composable
fun FullAudioPlayer(
    viewModel: AudioPlayerViewModel,
    modifier: Modifier,
    topPadding: Dp,
    coloredScaffoldState: ColoredScaffoldState,
    onCollapseRequest: () -> Unit = { },
    onAlbumClicked: (albumId: String) -> Unit,
    onArtistClicked: (artistId: String) -> Unit
) {
    val density = LocalDensity.current

    val screenHeight = with(density) {
        LocalWindowInfo.current.containerSize.height.toDp()
    }
    val screenWidth = with(density) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }

    val currentTrack by viewModel.audioPlayer.currentTrack.collectAsStateWithLifecycle()
    val currentLyrics by viewModel.audioPlayer.currentLyrics.collectAsStateWithLifecycle()
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

    LaunchedEffect(currentTrack) {
        isLyricsOpen.value = isLyricsOpen.value && currentLyrics != null
    }

    val snapPosition = object : SnapPosition {
        override fun position(
            layoutSize: Int,
            itemSize: Int,
            beforeContentPadding: Int,
            afterContentPadding: Int,
            itemIndex: Int,
            itemCount: Int,
        ): Int {
            return beforeContentPadding / 2
        }
    }
    val lazyListState = rememberLazyListState()
    val snapLayoutInfoProvider = SnapLayoutInfoProvider(lazyListState, snapPosition)

    var topControlsHeight by remember { mutableStateOf(0.dp) }

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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColorAnimated.value)
                .background(additionalVerticalGradientBrush.value)
        )

        Box {
            LazyColumn(
                modifier = Modifier
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .drawWithContent {
                        drawContent()

                        drawRect(
                            brush = Brush.verticalGradient(
                                0f to Color.Transparent,
                                with(density) {
                                    (topControlsHeight.roundToPx() + topPadding.roundToPx()) / screenHeight.roundToPx()
                                        .toFloat()
                                } to Color.Black
                            ),
                            blendMode = BlendMode.DstIn
                        )
                    },
                state = lazyListState,
                flingBehavior = rememberSnapFlingBehavior(snapLayoutInfoProvider)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .height(screenHeight)
                            .then(modifier)
                    ) {
                        Spacer(Modifier.height(topControlsHeight))

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
                                isLyricsOpen = isLyricsOpen
                            )
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .height(screenHeight)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        lastPaletteColorAnimated.value
                                    )
                                )
                            )
                    )
                }
            }

            Box(Modifier.align(Alignment.TopCenter)) {
                TopBar(
                    modifier = modifier
                        .onSizeChanged {
                            topControlsHeight = with(density) {
                                it.height.toDp()
                            }
                        },
                    currentTrackFullDto = currentTrack?.data,
                    onCollapseRequest = onCollapseRequest
                )
            }
        }
    }
}