package com.example.ktor_test_client.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Headset
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.ktor_test_client.api.dtos.Artist
import com.example.ktor_test_client.api.dtos.BaseAlbum
import com.example.ktor_test_client.api.dtos.BaseTrack
import com.example.ktor_test_client.controls.CircleButton
import com.example.ktor_test_client.controls.TrackMiniWithImage
import com.example.ktor_test_client.controls.coloredscaffold.ColoredScaffold
import com.example.ktor_test_client.controls.coloredscaffold.rememberColoredScaffoldState
import com.example.ktor_test_client.controls.flingscaffold.FlingScrollScaffold
import com.example.ktor_test_client.controls.flingscaffold.FlingScrollScaffoldState
import com.example.ktor_test_client.controls.flingscaffold.rememberFlingScaffoldState
import com.example.ktor_test_client.controls.toolscaffold.ToolScaffold
import com.example.ktor_test_client.controls.toolscaffold.rememberToolScaffoldState
import com.example.ktor_test_client.helpers.formatNumber
import com.example.ktor_test_client.viewmodels.ArtistViewModel
import kotlinx.coroutines.delay

object TopAppContentBar {
    const val TOP_PART_WEIGHT = .55f
    val additionalHeight = 60.dp
}

@Composable
fun ArtistPage(
    viewModel: ArtistViewModel,
    innerPadding: PaddingValues,
    bottomPadding: Dp,
    onBackRequest: () -> Unit = { },
    onTrackClicked: (clickedTrack: BaseTrack) -> Unit = { },
    onAlbumClicked: (albumId: String) -> Unit = { }
) {
    val pagerState = rememberPagerState(0) { viewModel.artist.value?.images?.size ?: 0 }

    val artist by viewModel.artist
    val topTracks by viewModel.topTracks
    val albums by viewModel.albums

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)

            val currentIndex = pagerState.currentPage
            pagerState.animateScrollToPage((currentIndex + 1) % (artist?.images?.count() ?: 0))
        }
    }

    val coloredScaffoldState = rememberColoredScaffoldState {
        viewModel.palette.collectAsStateWithLifecycle()
    }
    val toolScaffoldState = rememberToolScaffoldState<Nothing, Nothing>(onBackRequest = onBackRequest)

    ColoredScaffold(
        state = coloredScaffoldState
    ) {
        ToolScaffold(
            modifier = Modifier
                .padding(innerPadding),
            state = toolScaffoldState
        ) { toolScaffoldInnerPadding ->

            FlingScrollScaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = bottomPadding)
                    .background(Color.Black),
                state = rememberFlingScaffoldState(
                    yFlingOffset = toolScaffoldInnerPadding.calculateTopPadding()
                ) {
                    calcScrollState(toolScaffoldInnerPadding.calculateTopPadding())

                    toolScaffoldState.toolBarTitle.value = if (isHeaderSwiped.value.not()) {
                        artist?.name
                    } else {
                        null
                    }
                },
                backgroundContent = {
                    artist?.let {
                        ArtistAvatarPager(
                            viewModel = viewModel,
                            color = backgroundColorAnimated,
                            pagerState = pagerState,
                            currentOffset = currentOffset,
                            screenHeight = screenHeight,
                            artist = it
                        )
                    }
                },
                headingContent = {
                    artist?.let {
                        Header(
                            artist = it,
                            screenHeight = screenHeight,
                            alpha = alpha,
                            colorAlpha = colorAlpha,
                            color = backgroundColorAnimated
                        )
                    }
                }
            ) {
                artist?.let {
                    Content(
                        artist = it,
                        colorAlpha = colorAlpha,
                        backgroundColorAnimated = backgroundColorAnimated,
                        primaryColor = primaryColor,
                        topTracks = topTracks ?: listOf(),
                        albums = albums ?: listOf(),
                        onTrackClicked = onTrackClicked,
                        onAlbumClicked = onAlbumClicked
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtistAvatarPager(
    viewModel: ArtistViewModel,
    color: State<Color>,
    pagerState: PagerState,
    currentOffset: State<Dp>,
    screenHeight: Dp,
    artist: Artist
) {
    val context = LocalContext.current
    LaunchedEffect(pagerState.currentPage) {
        viewModel.artist.value?.let {
            viewModel.fetchImageByUrl(context, it.images[pagerState.currentPage])
        }
    }

    Box(
        modifier = Modifier
            .background(color.value)
            .fillMaxWidth()
            .height(screenHeight * TopAppContentBar.TOP_PART_WEIGHT)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    return@offset IntOffset(0, (-currentOffset.value / 5).roundToPx())
                }
        ) { page ->
            AsyncImage(
                model = artist.images[page],
                contentDescription = "Artist avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}

@Composable
private fun Header(
    artist: Artist,
    screenHeight: Dp,
    alpha: FloatState,
    colorAlpha: FloatState,
    color: State<Color>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight * TopAppContentBar.TOP_PART_WEIGHT + TopAppContentBar.additionalHeight)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.Black)
                .align(Alignment.BottomCenter)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ArtistHeaderFadingGradientTop(
                modifier = Modifier
                    .alpha(colorAlpha.floatValue)
                    .align(Alignment.BottomCenter),
                targetColor = color
            )

            ArtistHeader(
                modifier = Modifier
                    .alpha(alpha.floatValue)
                    .align(Alignment.BottomCenter),
                artist = artist
            )
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
            fontSize = 38.sp
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Headset,
                contentDescription = "headphones icon",
                modifier = Modifier
                    .size(24.dp)
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
                    imageVector = Icons.Rounded.FavoriteBorder,
                    modifier = Modifier
                        .size(24.dp),
                    contentDescription = ""
                )
            }

            CircleButton(
                onClick = { },
                underscoreText = "Трейлер",
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                    modifier = Modifier
                        .size(24.dp),
                    contentDescription = ""
                )
            }

            CircleButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { },
                underscoreText = "Слушать"
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    modifier = Modifier
                        .size(28.dp),
                    contentDescription = ""
                )
            }
        }
    }
}

@Composable
private fun Content(
    artist: Artist,
    colorAlpha: FloatState,
    backgroundColorAnimated: State<Color>,
    primaryColor: State<Color>,
    topTracks: List<BaseTrack>,
    albums: List<BaseAlbum>,
    onTrackClicked: (clickedTrack: BaseTrack) -> Unit,
    onAlbumClicked: (albumId: String) -> Unit
) {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .background(Color.Black)
    ) {
        ArtistHeaderFadingGradientBottom(
            modifier = Modifier
                .alpha(colorAlpha.floatValue),
            targetColor = backgroundColorAnimated
        )

        Column {
            TopTracks(
                message = "Популярные треки",
                primaryColor = primaryColor.value,
                tracks = topTracks,
                onTrackClicked = onTrackClicked
            )

            OtherAlbums(
                message = "Альбомы ${artist.name}",
                otherAlbums = albums,
                onAlbumClicked = onAlbumClicked
            )
        }
    }
}

@Composable
private fun TopTracks(
    message: String?,
    primaryColor: Color,
    tracks: List<BaseTrack>,
    onTrackClicked: (clickedTrack: BaseTrack) -> Unit
) {
    Column {
        Spacer(Modifier.height(50.dp))

        message?.let {
            Text(
                text = message,
                fontSize = 24.sp,
                fontWeight = FontWeight.W700,
                modifier = Modifier
                    .padding(start = 25.dp)
            )
        }

        Spacer(Modifier.height(15.dp))

        for (track in tracks) {
            TrackMiniWithImage(
                track = track,
                primaryColor = primaryColor,
                onPrimaryColor = Color.White,
                onClick = onTrackClicked
            )
        }

        Spacer(Modifier.height(15.dp))
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

private fun FlingScrollScaffoldState.calcScrollState(
    topPadding: Dp
) {
    isHeaderVisible.value = lazyListState.firstVisibleItemIndex == 0
    totalHeight.value = screenHeight * TopAppContentBar.TOP_PART_WEIGHT + TopAppContentBar.additionalHeight

    if (isHeaderVisible.value) {
        currentOffset.value = with(density) { lazyListState.firstVisibleItemScrollOffset.toDp() }

        alpha.floatValue = (totalHeight.value - topPadding - currentOffset.value) / totalHeight.value
        colorAlpha.floatValue = (totalHeight.value - topPadding - currentOffset.value) / 45.dp
    }
}