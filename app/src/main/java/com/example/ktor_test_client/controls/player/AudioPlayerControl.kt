package com.example.ktor_test_client.controls.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.palette.graphics.Palette
import com.example.ktor_test_client.R
import com.example.ktor_test_client.helpers.formatMinuteTimer
import com.example.ktor_test_client.helpers.times
import com.example.ktor_test_client.pages.TopAppContentBar.additionalHeight
import com.example.ktor_test_client.pages.TopAppContentBar.topPartWeight
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import kotlinx.coroutines.delay
import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.controls.CircleButton
import kotlin.math.roundToInt

val bottomGap = 110.dp
val additionalPlayerHeight = 3.dp

const val animationsSpeed = 1200

@Composable
fun FullAudioPlayer(
    viewModel: AudioPlayerViewModel,
    modifier: Modifier,
    onCollapseRequest: () -> Unit = { },
    onAlbumClicked: (albumId: String) -> Unit,
    onArtistClicked: (artistId: String) -> Unit
) {
    val context = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        viewModel.initializePlayer(context)

        viewModel.onTrackEnd = {
            viewModel.nextTrack(context)
        }
    }

    val currentTrack by viewModel.currentTrack.collectAsStateWithLifecycle()
    val bitmap by viewModel.bitmap.collectAsStateWithLifecycle()
    val currentTrackDuration by viewModel.currentTrackDuration

    val isPlay by remember { viewModel.isPlay }

    val isSliding = remember { mutableStateOf(false) }
    val currentPosition = remember { mutableLongStateOf(0L) }

    LaunchedEffect(viewModel.exoPlayer) {
        while (true) {
            delay(300)

            if (isSliding.value) continue
            currentPosition.longValue = viewModel.exoPlayer?.currentPosition ?: 0L
        }
    }

    val palette: MutableState<Palette?> = remember { mutableStateOf(null) }
    val primaryColor by remember {
        derivedStateOf {
            Color(palette.value?.dominantSwatch?.rgb ?: Color.Transparent.toArgb())
        }
    }

    val secondaryColor by remember {
        derivedStateOf {

            val colorValue: Int? = if (palette.value?.dominantSwatch?.rgb == palette.value?.vibrantSwatch?.rgb)
                palette.value?.dominantSwatch?.titleTextColor
            else
                palette.value?.vibrantSwatch?.rgb

            Color(colorValue ?: colorScheme.secondary.toArgb())
        }
    }

    val textOnSecondaryColor by remember {
        derivedStateOf {
            Color(palette.value?.vibrantSwatch?.titleTextColor ?: colorScheme.onSecondary.toArgb())
        }
    }

    val primaryColorAnimated = animateColorAsState(
        targetValue = primaryColor,
        animationSpec = tween(animationsSpeed),
        label = "primary color animation"
    )

    val secondaryColorAnimated = animateColorAsState(
        targetValue = secondaryColor,
        animationSpec = tween(animationsSpeed),
        label = "secondary color animation"
    )

    val textOnSecondaryColorAnimated = animateColorAsState(
        targetValue = textOnSecondaryColor,
        animationSpec = tween(animationsSpeed),
        label = "secondary color animation"
    )

    LaunchedEffect(Unit) {
        viewModel.palette.collect {
            palette.value = it
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.releasePlayer()
        }
    }


    Box {
        bitmap?.let {
            AnimatedContent(
                targetState = it,
                transitionSpec = { fadeIn(tween(animationsSpeed)) togetherWith fadeOut(tween(
                    animationsSpeed
                )) },
                label = "image animation"
            ) { animatedBitmap ->
                Image(
                    bitmap = animatedBitmap.asImageBitmap(),
                    contentDescription = "album image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((screenHeight * topPartWeight) + additionalHeight),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(modifier)
        ) {
            TopBar(
                textOnSecondaryColorAnimated,
                currentTrack,
                onCollapseRequest
            )

            PlayerPageHeaderFadingGradientTop(
                modifier = Modifier
                    .height(screenHeight * topPartWeight + additionalPlayerHeight),
                targetColor = primaryColorAnimated
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = screenHeight * topPartWeight - bottomGap)
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = bottomGap)
                        .fillMaxSize()
                        .background(primaryColorAnimated.value)
                )

                Column(
                    modifier = Modifier
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    TrackInfo(
                        currentTrack,
                        secondaryColor,
                        viewModel.isLoading,
                        onAlbumClicked,
                        onArtistClicked
                    )

                    PlayerControls(
                        currentPosition,
                        currentTrackDuration,
                        viewModel,
                        isPlay,
                        isSliding,
                        primaryColorAnimated,
                        secondaryColorAnimated,
                        viewModel.isLoading,
                        onNext = { viewModel.nextTrack(context) },
                        onPrev = { viewModel.prevTrack(context) },
                        onPlayPause = { viewModel.playPause() }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    textOnSecondaryColorAnimated: State<Color>,
    currentTrack: Track?,
    onCollapseRequest: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
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
            text = "Плейлист \"${currentTrack?.album?.name ?: "unknown"}\"",
            fontWeight = FontWeight.W700,
            color = textOnSecondaryColorAnimated.value
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
    currentTrack: Track?,
    secondaryColor: Color,
    isTrackLoading: State<Boolean>,
    onAlbumClicked: (albumId: String) -> Unit,
    onArtistClicked: (artistId: String) -> Unit
) {
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

    Column {
        Text(
            text = currentTrack?.name ?: "",
            fontSize = 80.sp,
            fontWeight = FontWeight.W800,
            lineHeight = 10.sp,
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable {
                    onAlbumClicked(currentTrack?.album?.id ?: "")
                }
                .padding(horizontal = 5.dp)
                .alpha(textAlpha)
                .basicMarquee(),
            color = secondaryColor
        )

        Text(
            text = currentTrack?.album?.artists?.firstOrNull()?.name ?: "",
            fontSize = 24.sp,
            fontWeight = FontWeight.W800,
            modifier = Modifier
                .offset(y = (-7).dp)
                .clip(MaterialTheme.shapes.small)
                .clickable {
                    onArtistClicked(currentTrack?.album?.artists?.first()?.id ?: "")
                }
                .padding(horizontal = 5.dp)
                .alpha(textAlpha)
                .basicMarquee(),
            color = secondaryColor
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.album_icon),
                contentDescription = "",
                modifier = Modifier
                    .size(25.dp)
                    .alpha(.7f)
                    .alpha(textAlpha),
                tint = secondaryColor
            )

            Text(
                text = currentTrack?.album?.name ?: "",
                fontWeight = FontWeight.W600,
                color = secondaryColor,
                modifier = Modifier
                    .alpha(textAlpha)
            )

            Text(
                text = "●",
                fontSize = 8.sp,
                color = secondaryColor,
                modifier = Modifier
                    .alpha(textAlpha)
            )

            //TODO: Добавить в response: album release date
            Text(
                text = (1970 /* + randomTrackResponse.album.releaseDate */ / (3600 * 24 * 31 * 12 * 50)).toString(),
                fontWeight = FontWeight.W600,
                color = secondaryColor,
                modifier = Modifier
                    .alpha(textAlpha)
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
    isPlay: Boolean,
    isSliding: MutableState<Boolean>,
    primaryColor: State<Color>,
    secondaryColor: State<Color>,
    isTrackLoading: State<Boolean>,
    onNext: () -> Unit = { },
    onPrev: () -> Unit = { },
    onPlayPause: () -> Unit = { }
) {
    val secondaryColorWithLoadingState by remember {
        derivedStateOf {
            if (isTrackLoading.value) {
                secondaryColor.value.copy(.7f)
            } else {
                secondaryColor.value
            }
        }
    }

    val currentPositionAnimated = animateFloatAsState(
        targetValue = (currentPosition.value / currentTrackDuration.toFloat()).coerceIn(0f..currentTrackDuration.toFloat()),
        animationSpec = if (isSliding.value) tween(0) else tween(300, easing = LinearEasing),
        label = "slider animation"
    )

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

                    viewModel.seekTo(currentPosition.value)
                },
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                colors = SliderColors(
                    thumbColor = (primaryColor.value) * 2.5f,
                    activeTrackColor = (primaryColor.value) * 2f,
                    inactiveTrackColor = (primaryColor.value) * .7f,
                    activeTickColor = primaryColor.value * 2.5f,
                    inactiveTickColor = primaryColor.value * .7f,
                    disabledThumbColor = primaryColor.value * .7f,
                    disabledActiveTrackColor = primaryColor.value * .7f,
                    disabledActiveTickColor = primaryColor.value * .7f,
                    disabledInactiveTickColor = primaryColor.value * .7f,
                    disabledInactiveTrackColor = primaryColor.value * .7f,
                ),
                thumb = {
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(30.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(primaryColor.value * 2.5f)
                    )
                },
                enabled = isTrackLoading.value.not()
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

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally)
        ) {

            IconButton(
                onClick = { onPrev() },
                enabled = isTrackLoading.value.not()
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
                containerColor = primaryColor.value * 2f,
                size = 70.dp,
                onClick = {
                    onPlayPause()
                },
                content = {
                    Icon(
                        imageVector = if (isPlay)
                            Icons.Rounded.Pause
                        else
                            Icons.Rounded.PlayArrow,
                        contentDescription = "play/pause icon",
                        tint = secondaryColor.value,
                        modifier = Modifier
                            .size(36.dp)
                    )
                }
            )

            IconButton(
                onClick = { onNext() },
                enabled = isTrackLoading.value.not()
            ) {
                Icon(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = "next icon",
                    tint = secondaryColorWithLoadingState,
                    modifier = Modifier
                        .size(34.dp)
                )
            }
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