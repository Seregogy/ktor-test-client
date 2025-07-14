package com.example.ktor_test_client.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.example.ktor_test_client.R
import com.example.ktor_test_client.tools.formatNumber
import kotlinx.coroutines.delay

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

@Composable
fun ArtistHomePage(
    artist: Artist
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val pagerState = rememberPagerState(0) { artist.imagesUrl.count() }

    val state = rememberLazyListState()
    val density = LocalDensity.current

    var alpha by remember { mutableFloatStateOf(1f) }
    var colorAlpha by remember { mutableFloatStateOf(1f) }

    val color = animateColorAsState(
        targetValue = artist.imagesUrl[pagerState.currentPage].second,
        label = "color cross fade"
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)

            val currentIndex = pagerState.currentPage
            pagerState.animateScrollToPage((currentIndex + 1) % artist.imagesUrl.count())
        }
    }

    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo }
            .collect {
                if (state.firstVisibleItemIndex == 0) {
                    println(state.firstVisibleItemScrollOffset)
                    alpha = (1120f - state.firstVisibleItemScrollOffset * 2f) / 1120f

                    colorAlpha = (1120f - state.firstVisibleItemScrollOffset) / 45f
                }
            }
    }

    val snappingLayout = remember(state, density) {
            val snapPosition =
                object : SnapPosition {
                    override fun position(
                        layoutSize: Int,
                        itemSize: Int,
                        beforeContentPadding: Int,
                        afterContentPadding: Int,
                        itemIndex: Int,
                        itemCount: Int,
                    ): Int {
                        return beforeContentPadding + 400
                    }
                }
            SnapLayoutInfoProvider(state, snapPosition)
        }

    val flingBehavior = rememberSnapFlingBehavior(snappingLayout)

    val avatarHeight = screenHeight * .55f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(avatarHeight)
        ) { page ->
            AsyncImage(
                model = artist.imagesUrl[page].first,
                contentDescription = "Artist avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
        }

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            state = state,
            flingBehavior = flingBehavior,
        ) {
            item(0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(avatarHeight + 180.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .background(Color.Black)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        ArtistHeaderFadingGradient(
                            modifier = Modifier
                                .alpha(colorAlpha),
                            targetColor = color
                        )

                        ArtistHeader(
                            modifier = Modifier
                                .alpha(alpha)
                                .padding(bottom = 20.dp)
                                .align(Alignment.Center),
                            artist = artist
                        )
                    }
                }
            }

            item(1) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .offset(y = (-80).dp)
                        .zIndex(1f)
                ) {
                    Text(
                        text = "Популярные треки",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.W700,
                        modifier = Modifier
                            .padding(start = 25.dp)
                    )
                }
            }


            items(25) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp, vertical = 2.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .height(80.dp)
                                .padding(5.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(11.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArtistHeader(
    modifier: Modifier = Modifier,
    artist: Artist
) {
    Column(
        modifier = modifier
            .padding(horizontal = 10.dp),
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.headphones_icon),
                contentDescription = "headphones icon",
                modifier = Modifier
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

@Composable
fun ArtistHeaderFadingGradient(
    modifier: Modifier,
    targetColor: State<Color>
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            .5f to Color.Transparent,
                            .95f to targetColor.value
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            .4f to targetColor.value,
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
                .size(65.dp),
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