package com.example.ktor_test_client.player.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.ktor_test_client.R
import com.example.ktor_test_client.api.dtos.TrackFullDto
import com.example.ktor_test_client.controls.CircleButton
import com.example.ktor_test_client.controls.coloredscaffold.ColoredScaffold
import com.example.ktor_test_client.controls.coloredscaffold.rememberColoredScaffoldState
import com.example.ktor_test_client.helpers.formatMinuteTimer
import com.example.ktor_test_client.helpers.times
import com.example.ktor_test_client.player.AudioPlayer
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import kotlin.math.roundToInt

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
                    textOnSecondaryColorAnimated = onBackgroundColorAnimated,
                    currentTrackFullDto = currentTrack?.data,
                    onCollapseRequest = onCollapseRequest
                )

                Box(
                    modifier = Modifier
                        .heightIn(min = screenWidth)
                        .offset(y = (-20).dp)
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
                            foregroundColor = textOnPrimaryOrBackgroundColorAnimated,
                            onBackgroundColor = onBackgroundColorAnimated,
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
                            backgroundColor = backgroundColorAnimated,
                            onBackgroundColor = onBackgroundColorAnimated,
                            foregroundColor = onBackgroundColorAnimated,
                            isTrackLoading = isLoading,
                            isLastTrack = isLastTrack,
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

@Composable
private fun DoubleTapGestures(
    audioPlayerViewModel: AudioPlayerViewModel,
    secondaryColor: State<Color>
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val modifier = Modifier
            .background(secondaryColor.value)
            .fillMaxHeight()
            .width(100.dp)
            .weight(.4f)

        Box(
            modifier = modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { offset ->
                            audioPlayerViewModel.audioPlayer.seek(audioPlayerViewModel.audioPlayer.currentPosition.value - 5000)
                        }
                    )

                }
        )

        Box(
            modifier = modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { offset ->
                            audioPlayerViewModel.audioPlayer.seek(audioPlayerViewModel.audioPlayer.currentPosition.value + 5000)
                        }
                    )
                }
        )
    }
}

@Composable
private fun TopBar(
    textOnSecondaryColorAnimated: State<Color>,
    currentTrackFullDto: TrackFullDto?,
    onCollapseRequest: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                onCollapseRequest()
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_down_icon),
                contentDescription = "",
                modifier = Modifier
                    .size(30.dp),
                tint = textOnSecondaryColorAnimated.value
            )
        }

        Text(
            modifier = Modifier
                .weight(.6f)
                .basicMarquee(),
            text = "Плейлист \"${currentTrackFullDto?.album?.name ?: "unknown"}\"",
            fontWeight = FontWeight.W700,
            color = textOnSecondaryColorAnimated.value,
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        IconButton(
            onClick = { }
        ) {
            Icon(
                painter = painterResource(R.drawable.queue_music_icon),
                contentDescription = "",
                tint = textOnSecondaryColorAnimated.value
            )
        }
    }
}

@Composable
private fun TrackInfo(
    currentTrackFullDto: TrackFullDto?,
    foregroundColor: State<Color>,
    onBackgroundColor: State<Color>,
    isTrackLoading: State<Boolean>,
    onAlbumClicked: (albumId: String) -> Unit,
    onArtistClicked: (artistId: String) -> Unit
) {
    val density = LocalDensity.current

    val infiniteTransition = rememberInfiniteTransition("cycling animation transition")
    val textAlphaAnimated by infiniteTransition.animateFloat(
        initialValue = .3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "text alpha animation"
    )

    val textAlpha by remember {
        derivedStateOf {
            if (isTrackLoading.value) textAlphaAnimated else 1f
        }
    }

    var columnHeight by remember { mutableStateOf(0.dp) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AsyncImage(
            model = currentTrackFullDto?.album?.artists?.first()?.imageUrl,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .heightIn(max = columnHeight - 5.dp)
                .aspectRatio(1f)
                .clip(CircleShape)
                .clickable {
                    onArtistClicked(currentTrackFullDto?.album?.artists?.first()?.id ?: "")
                },
            contentDescription = "mini avatar"
        )

        Column(
            modifier = Modifier
                .onSizeChanged {
                    with(density) {
                        columnHeight = it.height.toDp()
                    }
                }
        ) {
            Text(
                text = currentTrackFullDto?.name ?: "",
                fontSize = 30.sp,
                fontWeight = FontWeight.W800,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        onAlbumClicked(currentTrackFullDto?.album?.id ?: "")
                    }
                    .alpha(textAlpha)
                    .basicMarquee(),
                color = foregroundColor.value
            )

            Text(
                text = currentTrackFullDto?.album?.artists?.firstOrNull()?.name ?: "",
                fontWeight = FontWeight.W600,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        onArtistClicked(currentTrackFullDto?.album?.artists?.first()?.id ?: "")
                    }
                    .alpha(textAlpha)
                    .basicMarquee(),
                color = onBackgroundColor.value
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerControls(
    currentPosition: MutableState<Long>,
    currentTrackDuration: Long,
    viewModel: AudioPlayerViewModel,
    isPlay: State<Boolean>,
    isSliding: MutableState<Boolean>,
    backgroundColor: State<Color>,
    onBackgroundColor: State<Color>,
    foregroundColor: State<Color>,
    isTrackLoading: State<Boolean>,
    isLastTrack: State<Boolean>,
    onNext: () -> Unit = { },
    onPrev: () -> Unit = { },
    onPlayPause: () -> Unit = { }
) {
    val secondaryColorWithLoadingState by remember {
        derivedStateOf {
            if (isTrackLoading.value) {
                foregroundColor.value.copy(.5f)
            } else {
                foregroundColor.value
            }
        }
    }

    val nextTrackLoadedColorState by remember {
        derivedStateOf {
            if (isLastTrack.value) {
                foregroundColor.value.copy(.3f)
            } else {
                foregroundColor.value
            }
        }
    }

    val currentPositionAnimated = animateFloatAsState(
        targetValue = (currentPosition.value / currentTrackDuration.toFloat()).coerceIn(0f..currentTrackDuration.toFloat()),
        animationSpec = if (isSliding.value) tween(0) else tween(300, easing = LinearEasing),
        label = "slider animation"
    )

    val semiTransparentForeground by remember {
        derivedStateOf {
            foregroundColor.value.copy(.65f)
        }
    }

    val fullyTransparentForeground by remember {
        derivedStateOf {
            foregroundColor.value.copy(.15f)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Slider(
                    value = currentPositionAnimated.value,
                    onValueChange = {
                        if (!isSliding.value) isSliding.value = true

                        currentPosition.value = (it * currentTrackDuration.toFloat()).toLong()
                    },
                    onValueChangeFinished = {
                        isSliding.value = false

                        viewModel.audioPlayer.seek(currentPosition.value)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    colors = SliderDefaults.colors(
                        activeTrackColor = semiTransparentForeground * 1.5f,
                        activeTickColor = semiTransparentForeground * 2f,
                        inactiveTrackColor = fullyTransparentForeground,

                        inactiveTickColor = semiTransparentForeground,
                        disabledThumbColor = semiTransparentForeground,
                        disabledActiveTrackColor = semiTransparentForeground,
                        disabledActiveTickColor = semiTransparentForeground,
                        disabledInactiveTickColor = semiTransparentForeground,
                        disabledInactiveTrackColor = semiTransparentForeground,
                    ),
                    thumb = {
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(30.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(semiTransparentForeground * 2f)
                        )
                    }
                )

                IconButton(
                    onClick = { /*TODO(лайк на трек)*/ }
                ) {
                    Icon(
                        imageVector = if (false)
                            Icons.Rounded.Favorite
                        else
                            Icons.Rounded.FavoriteBorder,
                        contentDescription = "play/pause icon",
                        tint = secondaryColorWithLoadingState,
                        modifier = Modifier
                            .size(26.dp)
                    )
                }
            }

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.W600,
                            fontSize = 18.sp
                        )
                    ) {
                        append(
                            formatMinuteTimer(
                                (currentPositionAnimated.value * currentTrackDuration.toFloat() / 1000).roundToInt().coerceIn(0..currentTrackDuration.toInt())
                            )
                        )
                    }

                    append("/")
                    append(formatMinuteTimer((currentTrackDuration / 1000).toInt()))
                },
                textAlign = TextAlign.Center,
                color = secondaryColorWithLoadingState
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {

            IconButton(
                onClick = { onPrev() }
            ) {
                Icon(
                    imageVector = Icons.Rounded.SkipPrevious,
                    contentDescription = "prev icon",
                    tint = secondaryColorWithLoadingState,
                    modifier = Modifier
                        .size(34.dp)
                )
            }

            CircleButton(
                containerColor = fullyTransparentForeground,
                size = 70.dp,
                onClick = {
                    onPlayPause()
                },
                content = {
                    Icon(
                        imageVector = if (isPlay.value)
                            Icons.Rounded.Pause
                        else
                            Icons.Rounded.PlayArrow,
                        contentDescription = "play/pause icon",
                        tint = foregroundColor.value,
                        modifier = Modifier
                            .size(36.dp)
                    )
                }
            )

            IconButton(
                onClick = { onNext() },
                enabled = isLastTrack.value.not()
            ) {
                Icon(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = "next icon",
                    tint = nextTrackLoadedColorState,
                    modifier = Modifier
                        .size(34.dp)
                )
            }
        }

        BottomControls(
            modifier = Modifier
                .fillMaxWidth(),
            foregroundColor = onBackgroundColor
        )
    }
}

@Composable
fun BottomControls(
    modifier: Modifier,
    foregroundColor: State<Color>,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = { }
        ) {
            Icon(
                imageVector = Icons.Rounded.Timer,
                contentDescription = "time icon",
                tint = foregroundColor.value
            )
        }

        IconButton(
            onClick = { }
        ) {
            Icon(
                imageVector = Icons.Rounded.Lyrics,
                contentDescription = "time icon",
                tint = foregroundColor.value
            )
        }

        IconButton(
            onClick = { }
        ) {
            Icon(
                imageVector = Icons.Rounded.Repeat,
                contentDescription = "time icon",
                tint = foregroundColor.value
            )
        }
    }
}

@Composable
private fun PlayerPageHeaderFadingGradientTop(
    modifier: Modifier,
    targetColor: State<Color>
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            .35f to Color.Transparent,
                            .8f to targetColor.value
                        )
                    )
                )
        )
    }
}