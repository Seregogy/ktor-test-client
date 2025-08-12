package com.example.ktor_test_client.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.palette.graphics.Palette
import com.example.ktor_test_client.R
import com.example.ktor_test_client.controls.CircleButton
import com.example.ktor_test_client.helpers.formatMinuteTimer
import com.example.ktor_test_client.helpers.times
import com.example.ktor_test_client.models.Album
import com.example.ktor_test_client.models.Artist
import com.example.ktor_test_client.models.Track
import com.example.ktor_test_client.screens.TopAppContentBar.additionalHeight
import com.example.ktor_test_client.screens.TopAppContentBar.topPartWeight
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/*@Composable
fun AudioPlayerScreen(viewModel: AudioPlayerViewModel = viewModel()) {
    val context = LocalContext.current
    val isPlaying by remember { derivedStateOf { viewModel.exoPlayer?.isPlaying ?: false } }
    val currentPosition by remember { derivedStateOf { viewModel.exoPlayer?.currentPosition ?: 0L } }

    LaunchedEffect(Unit) {
        //viewModel.initializePlayer(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.releasePlayer()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Position: ${currentPosition / 1000}s")

        Button(onClick = { viewModel.playPause() }) {
            Text(if (isPlaying) "Pause" else "Play")
        }

        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { viewModel.seekTo(it.toLong()) },
            valueRange = 0f..(viewModel.exoPlayer?.duration?.toFloat() ?: 0f)
        )
    }
}*/

val bottomGap = 110.dp

@Composable
fun PlayerPage(
    viewModel: AudioPlayerViewModel,
    track: Track,
    album: Album,
    artist: Artist,
    innerPadding: PaddingValues,
    onAlbumClicked: (albumId: Int) -> Unit,
    onArtistClicked: (artistId: Int) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val bitmap: MutableState<Bitmap?> = remember { mutableStateOf(null) }
    val palette: MutableState<Palette?> = remember { mutableStateOf(null) }

    val primaryColor: MutableState<Color?> = remember { mutableStateOf(null) }
    val textColor: MutableState<Color?> = remember { mutableStateOf(null) }
    val tertiaryColor: MutableState<Color?> = remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchImageByUrl(context, album.imageUrl)
    }

    LaunchedEffect(Unit) {
        viewModel.bitmap.collect {
            bitmap.value = it
        }
    }

    LaunchedEffect(Unit) {
        viewModel.palette.collect {
            palette.value = it

            primaryColor.value = Color(palette.value?.dominantSwatch?.rgb ?: 0xFFFFFF)
            textColor.value = Color(palette.value?.mutedSwatch?.rgb ?: 0xFFFFFF)
            /*secondaryColor.value = Color(palette.value!!.swatches[1].rgb)
            tertiaryColor.value = Color(palette.value!!.swatches[2].rgb)*/
        }
    }

    var isPlaying by remember { mutableStateOf(false) }
    val currentPosition = remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            while (true) {
                //if ((viewModel.exoPlayer?.isPlaying) != true) continue

                delay(500)

                currentPosition.longValue = viewModel.exoPlayer?.currentPosition ?: 0L

                println(currentPosition.longValue)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.releasePlayer()
        }
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    bitmap.value?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "album image",
            modifier = Modifier
                .fillMaxWidth()
                .height((screenHeight * topPartWeight) + additionalHeight),
            contentScale = ContentScale.Crop
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { }
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_down_icon),
                    contentDescription = "",
                    modifier = Modifier
                        .size(30.dp),
                    tint = textColor.value ?: MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = "Плейлист ${album.name}",
                fontWeight = FontWeight.W700,
                color = textColor.value ?: MaterialTheme.colorScheme.onBackground
            )

            IconButton(
                onClick = { }
            ) {
                Icon(
                    painter = painterResource(R.drawable.history_icon),
                    contentDescription = "",
                    tint = textColor.value ?: MaterialTheme.colorScheme.onBackground
                )
            }
        }

        PlayerPageHeaderFadingGradientTop(
            modifier = Modifier
                .height(screenHeight * topPartWeight),
            targetColor = primaryColor
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
                    .background(primaryColor.value ?: Color.Transparent)
            )

            Column(
                modifier = Modifier
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column {
                    Text(
                        text = track.name,
                        fontSize = 80.sp,
                        fontWeight = FontWeight.W800,
                        lineHeight = 10.sp,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                onAlbumClicked(album.id)
                            }
                            .padding(horizontal = 5.dp)
                            .basicMarquee(),
                        color = textColor.value ?: MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = artist.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.W800,
                        modifier = Modifier
                            .offset(y = (-7).dp)
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                onArtistClicked(album.artistId)
                            }
                            .padding(horizontal = 5.dp)
                            .basicMarquee(),
                        color = textColor.value ?: MaterialTheme.colorScheme.onBackground
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
                            tint = textColor.value ?: MaterialTheme.colorScheme.onBackground
                        )

                        Text(
                            text = album.name,
                            fontWeight = FontWeight.W600,
                            color = textColor.value ?: MaterialTheme.colorScheme.onBackground
                        )

                        Text(
                            text = "●",
                            fontSize = 8.sp,
                            color = textColor.value ?: MaterialTheme.colorScheme.onBackground
                        )

                        Text(
                            text = (1970 + album.releaseDate / (3600 * 24 * 31 * 12)).toString(),
                            fontWeight = FontWeight.W600,
                            color = textColor.value ?: MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        Slider(
                            value = currentPosition.longValue / viewModel.currentDuration.value.toFloat(),
                            onValueChange = {
                                currentPosition.longValue = (it * viewModel.currentDuration.value).toLong()

                                viewModel.exoPlayer?.seekTo(currentPosition.longValue)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            colors = SliderColors(
                                thumbColor = (primaryColor.value ?: Color.Transparent) * 2.5f,
                                activeTrackColor = (primaryColor.value ?: Color.Transparent) * 2f,
                                inactiveTrackColor = (primaryColor.value ?: Color.Transparent) * .7f,
                                activeTickColor = (primaryColor.value ?: Color.Transparent) * 2.5f,
                                inactiveTickColor = (primaryColor.value ?: Color.Transparent),
                                disabledThumbColor = (primaryColor.value ?: Color.Transparent),
                                disabledActiveTrackColor = (primaryColor.value ?: Color.Transparent),
                                disabledActiveTickColor = (primaryColor.value ?: Color.Transparent),
                                disabledInactiveTickColor = (primaryColor.value ?: Color.Transparent),
                                disabledInactiveTrackColor = (primaryColor.value ?: Color.Transparent),
                            )
                        )

                        CircleButton(
                            containerColor = (primaryColor.value ?: Color.Transparent) * 2f,
                            onClick = {
                                viewModel.playPause()

                                isPlaying = viewModel.exoPlayer?.isPlaying ?: false
                            },
                            content = {
                                Icon(
                                    painter = if (isPlaying)
                                            painterResource(R.drawable.play_icon_1)
                                        else
                                            painterResource(R.drawable.pause_icon_1),
                                    contentDescription = "play icon",
                                    tint = Color.Black,
                                    modifier = Modifier
                                        .size(32.dp)
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
                                append(formatMinuteTimer((currentPosition.longValue / 1000f).roundToInt()))
                            }

                            append("/")

                            append(formatMinuteTimer((viewModel.currentDuration.value / 1000).toInt()))
                        },
                        textAlign = TextAlign.Center,
                        color = textColor.value ?: MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerPageHeaderFadingGradientTop(
    modifier: Modifier,
    targetColor: State<Color?>
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
                            .8f to (targetColor.value ?: Color.Transparent)
                        )
                    )
                )
        )
    }
}