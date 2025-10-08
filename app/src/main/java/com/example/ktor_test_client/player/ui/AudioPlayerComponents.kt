package com.example.ktor_test_client.player.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.ktor_test_client.R
import com.example.ktor_test_client.api.dtos.TrackFullDto
import com.example.ktor_test_client.controls.CircleButton
import com.example.ktor_test_client.controls.MarqueeText
import com.example.ktor_test_client.controls.coloredscaffold.ColoredScaffoldState
import com.example.ktor_test_client.helpers.formatMinuteTimer
import com.example.ktor_test_client.helpers.times
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
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
                        .background(onBackgroundColor.value)
                )
            }
        )

        IconButton(
            onClick = {  }
        ) {
            Icon(
                imageVector = if (false)
                    Icons.Rounded.Favorite
                else
                    Icons.Rounded.FavoriteBorder,
                contentDescription = "play/pause icon",
                tint = onBackgroundColor.value,
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
    val track by viewModel.audioPlayer.currentTrack.collectAsStateWithLifecycle()

    val haveLyrics by remember {
        derivedStateOf {
            isLyricsOpen.value = isLyricsOpen.value && track?.data?.lyrics != null

            track?.data?.lyrics != null
        }
    }

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
                tint = onBackgroundColor.value
            )
        }

        IconToggleButton(
            checked = isLyricsOpen.value,
            onCheckedChange = {
                isLyricsOpen.value = it
            },
            enabled = haveLyrics
        ) {
            Icon(
                painter = painterResource(R.drawable.lyrics_icon),
                contentDescription = "time icon",
                tint = if(haveLyrics) onBackgroundColor.value else onBackgroundColor.value.copy(.1f)
            )
        }

        IconButton(
            onClick = { }
        ) {
            Icon(
                painter = painterResource(R.drawable.repeat_icon),
                contentDescription = "time icon",
                tint = onBackgroundColor.value
            )
        }
    }
}