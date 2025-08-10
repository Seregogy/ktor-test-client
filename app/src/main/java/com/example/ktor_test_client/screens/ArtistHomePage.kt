package com.example.ktor_test_client.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.ktor_test_client.R
import com.example.ktor_test_client.controls.CircleButton
import com.example.ktor_test_client.models.Artist
import com.example.ktor_test_client.state.ScrollState
import com.example.ktor_test_client.helpers.formatNumber
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.delay

object TopAppContentBar {
    const val topPartWeight = .55f
    val additionalHeight = 60.dp
}

@Composable
fun ArtistHomePage(
    artist: Artist
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val pagerState = rememberPagerState(0) { artist.imagesUrl.count() }

    val lazyListState = rememberLazyListState()
    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)

            val currentIndex = pagerState.currentPage
            pagerState.animateScrollToPage((currentIndex + 1) % artist.imagesUrl.count())
        }
    }

    val snappingLayout = SnapLayoutInfoProvider(lazyListState, SnapPosition.Start)
    val flingBehavior = rememberSnapFlingBehavior(snappingLayout)

    val color = animateColorAsState(
        targetValue = artist.imagesUrl[pagerState.currentPage].second,
        label = "color cross fade"
    )

    val scrollState: State<ScrollState> = remember {
        derivedStateOf {
            val isAvatarVisible = lazyListState.firstVisibleItemIndex == 0
            val scrollState = ScrollState(isAvatarVisible = isAvatarVisible)
            val totalHeight = screenHeight * TopAppContentBar.topPartWeight + TopAppContentBar.additionalHeight

            if (scrollState.isAvatarVisible) {
                scrollState.currentOffset = with(density) { lazyListState.firstVisibleItemScrollOffset.toDp() }

                scrollState.alpha = (totalHeight - scrollState.currentOffset) / totalHeight
                scrollState.colorAlpha = (totalHeight - scrollState.currentOffset) / 60.dp
            }

            return@derivedStateOf scrollState
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        ArtistAvatarPager(color, pagerState, scrollState, screenHeight, artist)

        Content(lazyListState, flingBehavior, scrollState, color, screenHeight, artist)
    }
}

@Composable
private fun ArtistAvatarPager(
    color: State<Color>,
    pagerState: PagerState,
    scrollState: State<ScrollState>,
    screenHeight: Dp,
    artist: Artist
) {
    Box(
        modifier = Modifier
            .background(color.value)
            .fillMaxWidth()
            .height(screenHeight * TopAppContentBar.topPartWeight)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    return@offset IntOffset(0, (-scrollState.value.currentOffset / 5).roundToPx())
                }
        ) { page ->
            AsyncImage(
                model = artist.imagesUrl[page].first,
                contentDescription = "Artist avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}

@Composable
private fun Content(
    lazyListState: LazyListState,
    flingBehavior: TargetedFlingBehavior,
    scrollState: State<ScrollState>,
    color: State<Color>,
    screenHeight: Dp,
    artist: Artist
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        state = lazyListState,
        flingBehavior = flingBehavior,
        modifier = Modifier
            .pointerInteropFilter {
                return@pointerInteropFilter false
            }
    ) {
        item(0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * TopAppContentBar.topPartWeight + TopAppContentBar.additionalHeight)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color.Black)
                        .align(Alignment.BottomCenter)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    ArtistHeaderFadingGradientTop(
                        modifier = Modifier
                            .alpha(scrollState.value.colorAlpha)
                            .align(Alignment.BottomCenter),
                        targetColor = color
                    )

                    ArtistHeader(
                        modifier = Modifier
                            .alpha(scrollState.value.alpha)
                            .align(Alignment.BottomCenter),
                        artist = artist
                    )
                }
            }
        }

        item(1) {
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .background(Color.Black)
            ) {
                ArtistHeaderFadingGradientBottom(
                    modifier = Modifier
                        .alpha(scrollState.value.colorAlpha),
                    targetColor = color
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Text(
                        text = "Популярные треки",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.W700,
                        modifier = Modifier
                            .padding(top = 50.dp, start = 25.dp)
                    )

                    Spacer(Modifier.height(15.dp))

                    for (i in 1..5) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 25.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .shimmer()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .height(65.dp)
                                    .padding(5.dp)
                                    .aspectRatio(1f)
                            )

                            Column(
                                modifier = Modifier
                                    .height(60.dp)
                                    .align(Alignment.CenterVertically),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Box(
                                    modifier = Modifier
                                        .shimmer()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .size(170.dp, 20.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .shimmer()
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .size(100.dp, 20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item(2) {
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .background(Color.Black)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Text(
                        text = "Студийные альбомы",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.W700,
                        modifier = Modifier
                            .padding(top = 50.dp, start = 25.dp)
                    )

                    Spacer(Modifier.height(15.dp))

                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (i in 1..5) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = if (i == 1) 25.dp else 0.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .shimmer()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .height(160.dp)
                                        .padding(5.dp)
                                        .aspectRatio(1f)
                                )

                                Box(
                                    modifier = Modifier
                                        .shimmer()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .size(120.dp, 20.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .shimmer()
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .size(100.dp, 15.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .shimmer()
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .size(50.dp, 15.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item(3) {
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .height(1000.dp)
                    .background(Color.Black)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Text(
                        text = "Все альбомы",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.W700,
                        modifier = Modifier
                            .padding(top = 50.dp, start = 25.dp)
                    )

                    Spacer(Modifier.height(15.dp))

                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (i in 1..5) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = if (i == 1) 25.dp else 0.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .shimmer()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .height(160.dp)
                                        .padding(5.dp)
                                        .aspectRatio(1f)
                                )

                                Box(
                                    modifier = Modifier
                                        .shimmer()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .size(120.dp, 20.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .shimmer()
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .size(100.dp, 15.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .shimmer()
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .size(50.dp, 15.dp)
                                )
                            }
                        }
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
                .padding(top = 20.dp)
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
fun ArtistHeaderFadingGradientTop(
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

@Composable
fun ArtistHeaderFadingGradientBottom(
    modifier: Modifier,
    targetColor: State<Color>
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            .1f to targetColor.value,
                            .9f to Color.Transparent
                        )
                    )
                )
        )
    }
}