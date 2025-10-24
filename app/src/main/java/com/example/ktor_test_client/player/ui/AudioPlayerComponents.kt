package com.example.ktor_test_client.player.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.ktor_test_client.R
import com.example.ktor_test_client.api.dtos.Lyrics
import com.example.ktor_test_client.api.dtos.TrackFullDto
import com.example.ktor_test_client.controls.CircleButton
import com.example.ktor_test_client.controls.MarqueeText
import com.example.ktor_test_client.controls.coloredscaffold.ColoredScaffoldState
import com.example.ktor_test_client.helpers.formatMinuteTimer
import com.example.ktor_test_client.helpers.times
import com.example.ktor_test_client.player.AudioPlayer
import com.example.ktor_test_client.player.Track
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ColoredScaffoldState.TopBar(
    modifier: Modifier = Modifier,
    currentTrackFullDto: TrackFullDto?,
    onCollapseRequest: () -> Unit
) {
    Row(
        modifier = modifier
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
                tint = onBackgroundColorAnimated.value
            )
        }

        MarqueeText(
            text = "Плейлист \"${currentTrackFullDto?.album?.name ?: "unknown"}\"",
            fontWeight = FontWeight.W700,
            color = onBackgroundColorAnimated.value,
            maxLines = 1,
            textAlign = Alignment.Center,
            containerModifier = Modifier
                .weight(.6f)
        )


        IconButton(
            onClick = { }
        ) {
            Icon(
                painter = painterResource(R.drawable.queue_music_icon),
                contentDescription = "",
                tint = onBackgroundColorAnimated.value
            )
        }
    }
}

@Composable
fun ColoredScaffoldState.MainContent(
    viewModel: AudioPlayerViewModel,
    isLyricsOpen: MutableState<Boolean>,
    currentPosition: MutableState<Long>,
    screenWidth: Dp,
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
            LyricsDrawer(viewModel)
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

@Composable
fun ColoredScaffoldState.TrackInfo(
    currentTrackFullDto: TrackFullDto?,
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

    var columnSize by remember { mutableStateOf(IntSize.Zero) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (columnSize.height != 0) {
            AsyncImage(
                model = currentTrackFullDto?.album?.artists?.first()?.imageUrl,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(with(density) { columnSize.height.toDp() })
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .clickable {
                        onArtistClicked(currentTrackFullDto?.album?.artists?.first()?.id ?: "")
                    },
                contentDescription = "mini avatar"
            )
        }

        Column(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .onSizeChanged {
                    columnSize = it
                }
        ) {
            MarqueeText(
                text = currentTrackFullDto?.name ?: "",
                fontSize = 30.sp,
                fontWeight = FontWeight.W800,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        onAlbumClicked(currentTrackFullDto?.album?.id ?: "")
                    }
                    .alpha(textAlpha),
                color = textOnPrimaryOrBackgroundColorAnimated.value
            )

            MarqueeText(
                text = currentTrackFullDto?.album?.artists?.firstOrNull()?.name ?: "",
                fontWeight = FontWeight.W600,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        onArtistClicked(currentTrackFullDto?.album?.artists?.first()?.id ?: "")
                    }
                    .alpha(textAlpha),
                color = onBackgroundColorAnimated.value
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoredScaffoldState.PlayerSlider(
    modifier: Modifier = Modifier,
    currentPosition: MutableState<Long>,
    currentTrackDuration: Long,
    viewModel: AudioPlayerViewModel,
    isSliding: MutableState<Boolean>,
) {
    val semiTransparentForeground by remember {
        derivedStateOf {
            onBackgroundColorAnimated.value.copy(.65f)
        }
    }

    val fullyTransparentForeground by remember {
        derivedStateOf {
            onBackgroundColorAnimated.value.copy(.15f)
        }
    }

    val currentPositionAnimated = animateFloatAsState(
        targetValue = (currentPosition.value / currentTrackDuration.toFloat()).coerceIn(0f..currentTrackDuration.toFloat()),
        animationSpec = if (isSliding.value) tween(0) else tween(300, easing = LinearEasing),
        label = "slider animation"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Slider(
            modifier = modifier
                .weight(1f),
            value = currentPositionAnimated.value,
            onValueChange = {
                if (!isSliding.value) isSliding.value = true

                currentPosition.value = (it * currentTrackDuration.toFloat()).toLong()
            },
            onValueChangeFinished = {
                isSliding.value = false

                viewModel.audioPlayer.seek(currentPosition.value)
            },
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
                        .background(onBackgroundColorAnimated.value)
                )
            }
        )

        IconButton(
            onClick = {

            }
        ) {
            Icon(
                imageVector = if (false)
                    Icons.Rounded.Favorite
                else
                    Icons.Rounded.FavoriteBorder,
                contentDescription = "play/pause icon",
                tint = onBackgroundColorAnimated.value,
                modifier = Modifier
                    .size(26.dp)
            )
        }
    }

}

enum class TimingTextState {
    CurrentTime,
    RemainingTime
}

@Composable
fun ColoredScaffoldState.TimingText(
    secondaryColorWithLoadingState: Color,
    currentPosition: MutableState<Long>,
    currentTrackDuration: Long,
    isSliding: MutableState<Boolean>
) {
    val currentPositionAnimated = animateFloatAsState(
        targetValue = (currentPosition.value / currentTrackDuration.toFloat()).coerceIn(0f..currentTrackDuration.toFloat()),
        animationSpec = if (isSliding.value) tween(0) else tween(300, easing = LinearEasing),
        label = "slider animation"
    )

    val fullyTransparentForeground by remember {
        derivedStateOf {
            onBackgroundColorAnimated.value.copy(.15f)
        }
    }

    var currentTextState by remember { mutableStateOf(TimingTextState.CurrentTime) }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(fullyTransparentForeground)
            .clickable {
                currentTextState = if (currentTextState == TimingTextState.CurrentTime) {
                    TimingTextState.RemainingTime
                } else {
                    TimingTextState.CurrentTime
                }
            }
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontSize = 13.sp
                    )
                ) {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.W800
                        )
                    ) {
                        append(
                            formatMinuteTimer(
                                if (currentTextState == TimingTextState.CurrentTime) {
                                    (currentPositionAnimated.value * currentTrackDuration.toFloat() / 1000)
                                } else {
                                    -(currentTrackDuration - currentPositionAnimated.value * currentTrackDuration.toFloat()) / 1000
                                }.roundToInt().coerceIn(-currentTrackDuration.toInt()..currentTrackDuration.toInt())
                            )
                        )
                    }

                    append(" / ")

                    append(formatMinuteTimer((currentTrackDuration / 1000).toInt()))
                }
            },
            textAlign = TextAlign.Center,
            color = secondaryColorWithLoadingState
        )
    }
}

@Composable
fun ColoredScaffoldState.PlayerNavigationButtons(
    modifier: Modifier = Modifier,
    secondaryColorWithLoadingState: Color,
    isPlay: State<Boolean>,
    isLastTrack: State<Boolean>,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onPlayPause: () -> Unit
) {
    val nextTrackLoadedColorState by remember {
        derivedStateOf {
            if (isLastTrack.value) {
                onBackgroundColorAnimated.value.copy(.3f)
            } else {
                onBackgroundColorAnimated.value
            }
        }
    }

    val fullyTransparentForeground by remember {
        derivedStateOf {
            onBackgroundColorAnimated.value.copy(.15f)
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
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
                    tint = onBackgroundColorAnimated.value,
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
}

@Composable
fun ColoredScaffoldState.BottomControls(
    modifier: Modifier,
    viewModel: AudioPlayerViewModel,
    isLyricsOpen: MutableState<Boolean>
) {
    val coroutineScope = rememberCoroutineScope()
    val repeatMode by viewModel.audioPlayer.repeatModeState.collectAsStateWithLifecycle()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = { }
        ) {
            Icon(
                painter = painterResource(R.drawable.timer_icon),
                contentDescription = "time icon",
                tint = onBackgroundColorAnimated.value
            )
        }

        IconToggleButton(
            checked = isLyricsOpen.value,
            onCheckedChange = {
                isLyricsOpen.value = !isLyricsOpen.value
                coroutineScope.launch {
                    viewModel.audioPlayer.getLyricsForCurrentTrack()
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.lyrics_icon),
                contentDescription = "time icon",
                tint = onBackgroundColorAnimated.value
            )
        }

        IconButton(
            onClick = {
                viewModel.audioPlayer.nextRepeatMode()
            }
        ) {
            Icon(
                painter = when(repeatMode) {
                    AudioPlayer.RepeatMode.Single -> painterResource(R.drawable.repeat_icon_1)
                    AudioPlayer.RepeatMode.Playlist -> painterResource(R.drawable.repeat_icon)
                    AudioPlayer.RepeatMode.None -> painterResource(R.drawable.repeat_off)
                },
                contentDescription = "time icon",
                tint = onBackgroundColorAnimated.value
            )
        }
    }
}

@Composable
fun ColoredScaffoldState.LyricsDrawer(
    viewModel: AudioPlayerViewModel
) {
    val density = LocalDensity.current
    val trackName by remember {
        derivedStateOf {
            viewModel.audioPlayer.currentTrack.value?.data?.name ?: "unknown"
        }
    }
    val lyrics by viewModel.audioPlayer.currentLyrics.collectAsStateWithLifecycle()

    when {
        lyrics == null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = onBackgroundColorAnimated.value
                )
            }
        }
        lyrics?.syncedText != null -> {
            var currentPosition by remember { viewModel.audioPlayer.currentPosition }
            val lazyListState = rememberLazyListState()
            val syncedTextPairs = remember {
                return@remember lyrics!!.syncedText!!.map { it.key to it.value.trim() }.toMutableList().apply {
                    add(Long.MAX_VALUE to "")
                }
            }
            val syncedTextSizes = remember { mutableMapOf<Long, IntSize>() }

            var columnSize by remember { mutableStateOf(IntSize.Zero) }
            var currentIndex by remember { mutableIntStateOf(-1) }

            LaunchedEffect(currentPosition) {
                currentIndex = findCurrentIndex(currentPosition, syncedTextPairs)
            }

            LaunchedEffect(currentIndex) {
                if (currentIndex in syncedTextPairs.indices) {
                    lazyListState.animateScrollToItem(currentIndex + 1,
                        -(columnSize.height / 2) + ((syncedTextSizes[syncedTextPairs[currentIndex].first]?.height ?: 0) / 2)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .onSizeChanged {
                        columnSize = it
                    },
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(70.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(Modifier.height(with(density) { columnSize.height.toDp() / 2 }))
                }

                itemsIndexed(
                    items = syncedTextPairs,
                    key = { _, item -> item.first }
                ) { index, item ->
                    Text(
                        text = item.second,
                        fontSize = 32.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.W700,
                        textAlign = TextAlign.Center,
                        color = if (currentIndex == index) bodyTextOnBackgroundAnimated.value else bodyTextOnBackgroundAnimated.value.copy(.2f),
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                viewModel.audioPlayer.seek(item.first)
                            },
                        onTextLayout = {
                            syncedTextSizes.put(item.first, it.size)
                        }
                    )
                }

                item {
                    Text(
                        "Lyrics provider ${lyrics?.provider} open library",
                        fontSize = 13.sp,
                        color = bodyTextOnBackgroundAnimated.value
                    )

                    Spacer(Modifier.height(100.dp))
                }
            }
        }

        lyrics?.plainText != null -> {
            Column(
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 100.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = trackName,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.W700,
                    color = bodyTextOnBackgroundAnimated.value
                )

                Text(
                    text = lyrics!!.plainText!!,
                    modifier = Modifier,
                    color = bodyTextOnBackgroundAnimated.value
                )

                Text(
                    "Lyrics provider ${lyrics?.provider} open library",
                    fontSize = 13.sp,
                    color = bodyTextOnBackgroundAnimated.value
                )
            }
        }
    }
}

private fun findCurrentIndex(position: Long, pairs: List<Pair<Long, String>>): Int {
    var low = 0
    var high = pairs.size - 2

    while (low <= high) {
        val mid = (low + high) / 2
        if (position in pairs[mid].first..pairs[mid + 1].first) {
            return mid
        } else if (position < pairs[mid].first) {
            high = mid - 1
        } else {
            low = mid + 1
        }
    }
    return -1
}