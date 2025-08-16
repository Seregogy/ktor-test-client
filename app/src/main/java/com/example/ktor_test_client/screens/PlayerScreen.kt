package com.example.ktor_test_client.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ktor_test_client.R
import com.example.ktor_test_client.api.KtorAPI
import com.example.ktor_test_client.api.methods.RandomTrackResponse
import com.example.ktor_test_client.controls.CircleButton
import com.example.ktor_test_client.helpers.formatMinuteTimer
import com.example.ktor_test_client.helpers.times
import com.example.ktor_test_client.screens.TopAppContentBar.additionalHeight
import com.example.ktor_test_client.screens.TopAppContentBar.topPartWeight
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

val bottomGap = 110.dp
const val animationsSpeed = 1200

@Composable
fun BottomSheetPlayerPage(
    yCurrentOffset: State<Float>,
    miniPlayerHeight: Dp,
    innerPadding: PaddingValues,
    api: KtorAPI,
    viewModel: AudioPlayerViewModel,
    modifier: Modifier,
    onExpandRequest: () -> Unit = { },
    onCollapseRequest: () -> Unit = { },
    onAlbumClicked: (albumId: String) -> Unit,
    onArtistClicked: (artistId: String) -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val targetMiniPlayerAlpha by remember {
        derivedStateOf {
            yCurrentOffset.value / screenHeight
        }
    }

    Box {
        Box(
            modifier = Modifier.alpha(1f - targetMiniPlayerAlpha)
        ) {
            PlayerPage(api, viewModel, modifier, onCollapseRequest, onAlbumClicked, onArtistClicked)
        }

        Box(
            modifier = Modifier
                .alpha(targetMiniPlayerAlpha)
                .align(Alignment.TopCenter)
                .then(
                    if (targetMiniPlayerAlpha < .8f)
                        Modifier.pointerInteropFilter { return@pointerInteropFilter false }
                    else
                        Modifier
                )
        ) {
            MiniPlayer(viewModel, miniPlayerHeight, innerPadding, onExpandRequest)
        }
    }
}

@Composable
fun MiniPlayer(
    viewModel: AudioPlayerViewModel,
    miniPlayerHeight: Dp,
    scaffoldInnerPadding: PaddingValues,
    onExpandRequest: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val colorScheme = MaterialTheme.colorScheme

    val currentTrack by viewModel.currentTrack.collectAsStateWithLifecycle()
    val bitmap by viewModel.bitmap.collectAsStateWithLifecycle()
    val palette by viewModel.palette.collectAsStateWithLifecycle()

    val backgroundColor by remember {
        derivedStateOf {
            Color(palette?.vibrantSwatch?.rgb ?: colorScheme.background.toArgb())
        }
    }

    val foregroundColor by remember {
        derivedStateOf {
            Color(palette?.vibrantSwatch?.titleTextColor ?: colorScheme.onBackground.toArgb())
        }
    }

    val artistsNames by remember {
        derivedStateOf {
            currentTrack?.artists?.joinToString(",") { it.name } ?: "unknown"
        }
    }

    val isPlay by remember { viewModel.isPlay }

    Box(
        modifier = Modifier
            .background(Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = scaffoldInnerPadding.calculateBottomPadding())
                .height(miniPlayerHeight)
                .fillMaxWidth()
                .padding(15.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(backgroundColor)
                .clickable {
                    onExpandRequest()
                },
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedContent(
                targetState = bitmap,
                transitionSpec = { fadeIn(tween(animationsSpeed)) togetherWith fadeOut(tween(animationsSpeed)) },
                label = "mini player bitmap crossfade"
            ) {
                it?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(10.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(5.dp)),
                        contentDescription = "mini player image",
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column {
                Text(
                    text = currentTrack?.track?.name ?: "Unknown",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W700,
                    color = foregroundColor,
                    maxLines = 1,
                    modifier = Modifier
                        .basicMarquee()
                )
                Text(
                    text = artistsNames,
                    fontSize = 13.sp,
                    color = foregroundColor
                )
            }
        }

        IconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 30.dp),
            onClick = {
                viewModel.playPause()
            },
            interactionSource = interactionSource
        ) {
            Icon(
                painter = if (isPlay)
                    painterResource(R.drawable.play_icon)
                else
                    painterResource(R.drawable.pause_icon),
                contentDescription = "play/pause icon",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(28.dp),
                tint = foregroundColor
            )
        }
    }
}

@Composable
fun PlayerPage(
    api: KtorAPI,
    viewModel: AudioPlayerViewModel,
    modifier: Modifier,
    onCollapseRequest: () -> Unit = { },
    onAlbumClicked: (albumId: String) -> Unit,
    onArtistClicked: (artistId: String) -> Unit
) {
    val context = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    LaunchedEffect(Unit) {
        viewModel.initializePlayer(context)
        viewModel.getRandomTrack(api, context)

        viewModel.onTrackEnd = {
            viewModel.getRandomTrack(api, context)
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

    val primaryColor: MutableState<Color> = remember { mutableStateOf(Color.Transparent) }
    val secondaryColor: MutableState<Color> = remember { mutableStateOf(Color.Transparent) }
    val textOnSecondaryColor: MutableState<Color> = remember { mutableStateOf(Color.Transparent) }

    val primaryColorAnimated = animateColorAsState(
        targetValue = primaryColor.value,
        animationSpec = tween(animationsSpeed),
        label = "primary color animation"
    )

    val secondaryColorAnimated = animateColorAsState(
        targetValue = secondaryColor.value,
        animationSpec = tween(animationsSpeed),
        label = "secondary color animation"
    )

    val textOnSecondaryColorAnimated = animateColorAsState(
        targetValue = textOnSecondaryColor.value,
        animationSpec = tween(animationsSpeed),
        label = "secondary color animation"
    )

    LaunchedEffect(Unit) {
        viewModel.palette.collect {
            it?.let {
                primaryColor.value = Color(it.dominantSwatch?.rgb ?: Color.Transparent.toArgb())
                secondaryColor.value = Color(it.mutedSwatch?.rgb ?: Color.Transparent.toArgb())
                textOnSecondaryColor.value = Color(it.mutedSwatch?.titleTextColor ?: Color.Transparent.toArgb())
            }
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
                transitionSpec = { fadeIn(tween(animationsSpeed)) togetherWith fadeOut(tween(animationsSpeed)) },
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
                    .height(screenHeight * topPartWeight),
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
                        onNext = { viewModel.nextTrack(api, context) },
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
    currentTrack: RandomTrackResponse?,
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
    currentTrack: RandomTrackResponse?,
    secondaryColor: MutableState<Color>,
    onAlbumClicked: (albumId: String) -> Unit,
    onArtistClicked: (artistId: String) -> Unit
) {
    Column {
        Text(
            text = currentTrack?.track?.name ?: "",
            fontSize = 80.sp,
            fontWeight = FontWeight.W800,
            lineHeight = 10.sp,
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable {
                    onAlbumClicked(currentTrack?.album?.id ?: "")
                }
                .padding(horizontal = 5.dp)
                .basicMarquee(),
            color = secondaryColor.value
        )

        Text(
            text = currentTrack?.artists?.firstOrNull()?.name ?: "",
            fontSize = 24.sp,
            fontWeight = FontWeight.W800,
            modifier = Modifier
                .offset(y = (-7).dp)
                .clip(MaterialTheme.shapes.small)
                .clickable {
                    onArtistClicked(currentTrack?.artists?.first()?.id ?: "")
                }
                .padding(horizontal = 5.dp)
                .basicMarquee(),
            color = secondaryColor.value
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
                    .alpha(.7f),
                tint = secondaryColor.value
            )

            Text(
                text = currentTrack?.album?.name ?: "",
                fontWeight = FontWeight.W600,
                color = secondaryColor.value
            )

            Text(
                text = "●",
                fontSize = 8.sp,
                color = secondaryColor.value
            )

            //TODO: Добавить в response: album release date
            Text(
                text = (1970 /* + randomTrackResponse.album.releaseDate */ / (3600 * 24 * 31 * 12 * 50)).toString(),
                fontWeight = FontWeight.W600,
                color = secondaryColor.value
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
    onNext: () -> Unit = { },
    onPrev: () -> Unit = { },
    onPlayPause: () -> Unit = { }
) {
    val currentPositionAnimated = animateFloatAsState(
        targetValue = (currentPosition.value / currentTrackDuration.toFloat()).coerceIn(0f..currentTrackDuration.toFloat()),
        animationSpec = if (isSliding.value) tween(0) else tween(300, easing = LinearEasing),
        label = "slider animation"
    )

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
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
                    inactiveTickColor = primaryColor.value,
                    disabledThumbColor = primaryColor.value,
                    disabledActiveTrackColor = primaryColor.value,
                    disabledActiveTickColor = primaryColor.value,
                    disabledInactiveTickColor = primaryColor.value,
                    disabledInactiveTrackColor = primaryColor.value,
                ),
                thumb = {
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(30.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(primaryColor.value * 2.5f)
                    )
                }
            )

            CircleButton(
                containerColor = primaryColor.value * 2f,
                onClick = {
                    onPlayPause()
                },
                content = {
                    Icon(
                        painter = if (isPlay)
                            painterResource(R.drawable.play_icon)
                        else
                            painterResource(R.drawable.pause_icon),
                        contentDescription = "play/pause icon",
                        tint = secondaryColor.value,
                        modifier = Modifier
                            .size(30.dp)
                    )
                }
            )
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
            color = secondaryColor.value
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.End)
        ) {
            CircleButton(
                containerColor = primaryColor.value * 2f,
                onClick = {
                    onPrev()
                },
                content = {
                    Icon(
                        painter = painterResource(R.drawable.previous_icon),
                        contentDescription = "prev icon",
                        tint = secondaryColor.value,
                        modifier = Modifier
                            .size(30.dp)
                    )
                }
            )

            CircleButton(
                containerColor = primaryColor.value * 2f,
                onClick = {
                    onNext()
                },
                content = {
                    Icon(
                        painter = painterResource(R.drawable.next_icon),
                        contentDescription = "next icon",
                        tint = secondaryColor.value,
                        modifier = Modifier
                            .size(30.dp)
                    )
                }
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