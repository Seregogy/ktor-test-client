package com.example.ktor_test_client.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.ktor_test_client.R
import com.example.ktor_test_client.tools.formatNumber

data class Artist(
    val name: String = "unknown artist",
    val about: String = "nothing interesting",
    val listeningInMonth: Int = 0,
    val likes: Int = 0,
    val bestTracks: List<Int> = listOf(),
    val tracks: List<Int> = listOf(),
    val albums: List<Int> = listOf(),
    val socialMedia: Map<String, String> = mapOf(),
    val imagesUrl: List<Pair<String, Color>> = listOf()
)

val postMalone = Artist(
    name = "Post Malone",
    about = "American raper, producer",
    listeningInMonth = 1_301_945,
    likes = 1_052_006,
    imagesUrl = listOf(
        "https://www.soyuz.ru/public/uploads/files/2/7390868/2019091211303003a5833f99.jpg" to Color(150, 159, 170),
        "https://cdn-image.zvuk.com/pic?type=artist&id=3289907&size=medium&hash=bbdb7895-d42f-40ca-801c-540fc2bc7f2c" to Color(140, 140, 140)
    )
)

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7_PRO)
@Composable
fun ArtistPagePreview() {
    ArtistHomePage(Modifier.padding(15.dp), postMalone)
}

@Composable
fun ArtistHomePage(
    modifier: Modifier = Modifier,
    artist: Artist = Artist()
) {
    val pagerState = rememberPagerState(0) { artist.imagesUrl.size }
    val currentImagePrimaryColor = remember { mutableStateOf(artist.imagesUrl.first().second) }

    val animatedColor = animateColorAsState(
        targetValue = currentImagePrimaryColor.value,
        label = "color animation",
        animationSpec = tween(250)
    )

    LaunchedEffect(pagerState.currentPage) {
        currentImagePrimaryColor.value = artist.imagesUrl[pagerState.currentPage].second
        println("bombom")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.6f)
        ) { page ->
            AsyncImage(
                model = artist.imagesUrl[page].first,
                contentDescription = "artist image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
        }

        ArtistHeaderFadingGradient(animatedColor)
        ArtistHeader(artist)
    }
}

@Composable
fun ArtistHeader(
    artist: Artist
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.65f)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = artist.name,
                fontWeight = FontWeight.W800,
                fontSize = 42.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.headphones_icon),
                    contentDescription = "headphones icon",
                    modifier = Modifier
                        .alignByBaseline()
                        .size(16.dp)
                )

                Text(
                    text = "${formatNumber(artist.listeningInMonth)} за месяц",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth(.85f)
            ) {
                CircleButton(
                    onClick = { },
                    underscoreText = formatNumber(artist.likes),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.heart_icon),
                        modifier = Modifier
                            .size(40.dp),
                        contentDescription = ""
                    )
                }

                CircleButton(
                    onClick = { },
                    underscoreText = "Трейлер",
                ) {
                    Icon(
                        painter = painterResource(R.drawable.queue_music_icon),
                        modifier = Modifier
                            .size(40.dp),
                        contentDescription = ""
                    )
                }

                CircleButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = { },
                    underscoreText = "Слушать"
                ) {
                    Icon(
                        painter = painterResource(R.drawable.play_icon),
                        modifier = Modifier
                            .size(40.dp),
                        contentDescription = ""
                    )
                }
            }
        }
    }
}

@Composable
fun ArtistHeaderFadingGradient(
    targetColor: State<Color>
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.51f)
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            .35f to Color.Transparent,
                            .95f to targetColor.value
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            .35f to targetColor.value,
                            .95f to Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
fun CircleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = .4f),
    underscoreText: String = "",
    content: @Composable () -> Unit = { }
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = modifier
                .clip(CircleShape)
                .size(70.dp),
            colors = ButtonColors(
                containerColor = containerColor,
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContentColor = MaterialTheme.colorScheme.onSecondary,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            onClick = onClick,
        ) {
            content()
        }

        Text(
            text = underscoreText,
            fontSize = 12.sp,
            fontWeight = FontWeight.W700
        )
    }
}