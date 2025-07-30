package com.example.ktor_test_client.pages

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
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.ktor_test_client.R
import com.example.ktor_test_client.compoments.CircleButton
import com.example.ktor_test_client.helpers.formatMinuteTimer
import com.example.ktor_test_client.helpers.times
import com.example.ktor_test_client.models.Album
import com.example.ktor_test_client.models.Artist
import com.example.ktor_test_client.models.Track
import com.example.ktor_test_client.screens.TopAppContentBar.additionalHeight
import com.example.ktor_test_client.screens.TopAppContentBar.topPartWeight
import kotlin.math.roundToInt

val bottomGap = 110.dp

@Composable
fun PlayerPage(
    track: Track,
    album: Album,
    artist: Artist,
    innerPadding: PaddingValues,
    onAlbumClicked: (albumId: Int) -> Unit,
    onArtistClicked: (artistId: Int) -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    var sliderValue by remember { mutableFloatStateOf(0f) }

    AsyncImage(
        model = album.imageUrl,
        contentDescription = "album image",
        modifier = Modifier
            .fillMaxWidth()
            .height((screenHeight * topPartWeight) + additionalHeight),
        contentScale = ContentScale.Crop
    )

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
                            .size(30.dp)
                )
            }

            Text(
                text = "Плейлист ${album.name}",
                fontWeight = FontWeight.W700
            )

            IconButton(
                onClick = { }
            ) {
                Icon(
                    painter = painterResource(R.drawable.history_icon),
                    contentDescription = ""
                )
            }
        }

        PlayerPageHeaderFadingGradientTop(
            modifier = Modifier
                .height(screenHeight * topPartWeight),
            targetColor = remember { mutableStateOf(album.primaryColor) }
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
                    .background(album.primaryColor)
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
                            .basicMarquee()
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
                            .basicMarquee()
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
                        )

                        Text(
                            text = album.name,
                            fontWeight = FontWeight.W600
                        )

                        Text(
                            text = "●",
                            fontSize = 8.sp
                        )

                        Text(
                            text = (1970 + album.releaseDate / (3600 * 24 * 31 * 12)).toString(),
                            fontWeight = FontWeight.W600
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        Slider(
                            value = sliderValue,
                            onValueChange = {
                                sliderValue = it
                            },
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            colors = SliderColors(
                                thumbColor = album.primaryColor * 2.5f,
                                activeTrackColor = album.primaryColor * 2f,
                                inactiveTrackColor = album.primaryColor * .7f,
                                activeTickColor = album.primaryColor * 2.5f,
                                inactiveTickColor = album.primaryColor,
                                disabledThumbColor = album.primaryColor,
                                disabledActiveTrackColor = album.primaryColor,
                                disabledActiveTickColor = album.primaryColor,
                                disabledInactiveTickColor = album.primaryColor,
                                disabledInactiveTrackColor = album.primaryColor,
                            )
                        )

                        CircleButton(
                            containerColor = album.primaryColor * 2f,
                            onClick = { },
                            content = {
                                Icon(
                                    painter = painterResource(R.drawable.play_icon),
                                    contentDescription = "play icon",
                                    tint = Color.Black
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
                                append(formatMinuteTimer((track.seconds * sliderValue).roundToInt()))
                            }

                            append("/")

                            append(formatMinuteTimer(track.seconds))
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerPageHeaderFadingGradientTop(
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