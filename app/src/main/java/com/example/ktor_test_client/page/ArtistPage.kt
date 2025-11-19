package com.example.ktor_test_client.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Headset
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
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
import com.example.ktor_test_client.api.dtos.BaseTrackWithArtists
import com.example.ktor_test_client.control.TrackMiniWithImage
import com.example.ktor_test_client.control.coloredscaffold.ColoredScaffold
import com.example.ktor_test_client.control.coloredscaffold.rememberColoredScaffoldState
import com.example.ktor_test_client.control.flingscaffold.FlingScrollScaffold
import com.example.ktor_test_client.control.flingscaffold.FlingScrollScaffoldState
import com.example.ktor_test_client.control.flingscaffold.rememberFlingScaffoldState
import com.example.ktor_test_client.control.toolscaffold.ToolScaffold
import com.example.ktor_test_client.control.toolscaffold.rememberToolScaffoldState
import com.example.ktor_test_client.page.album.OtherAlbums
import com.example.ktor_test_client.viewmodel.ArtistViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TopAppContentBar {
    const val TOP_PART_WEIGHT = .55f
    val additionalHeight = 60.dp
}

@Composable
fun ArtistPage(
    viewModel: ArtistViewModel,
    innerPadding: PaddingValues,
    bottomPadding: Dp,
    hazeState: HazeState,
    onBackRequest: () -> Unit = { },
    onTrackClicked: (clickedTrack: BaseTrack) -> Unit = { },
    onAlbumClicked: (albumId: String) -> Unit = { }
) {
    val pagerState = rememberPagerState(0) { viewModel.artist.value?.images?.size ?: 0 }

    val artist by viewModel.artist
    val latestRelease by viewModel.latestRelease
    val topTracks by viewModel.topTracks
    val albums by viewModel.albums
    val singles by viewModel.singles

    val coloredScaffoldState = rememberColoredScaffoldState {
        viewModel.palette.collectAsStateWithLifecycle()
    }
    val toolScaffoldState = rememberToolScaffoldState(onBackRequest = onBackRequest)

    ColoredScaffold(
        state = coloredScaffoldState
    ) {
        ToolScaffold(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding()),
            state = toolScaffoldState,
            hazeState = hazeState
        ) { toolScaffoldInnerPadding ->

            FlingScrollScaffold(
                modifier = Modifier
                    .hazeSource(hazeState)
                    .fillMaxSize()
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
                        if (lastVisibleIndex.intValue == 0) {
                            ArtistAvatarPager(
                                viewModel = viewModel,
                                pagerState = pagerState,
                                currentOffset = currentOffset,
                                screenHeight = screenHeight,
                                alpha = alpha,
                                artist = it
                            )
                        }
                    }
                },
                headingContent = {
                    artist?.let {
                        Header(
                            artist = it,
                            screenHeight = screenHeight,
                            alpha = alpha
                        )
                    }
                }
            ) {
                artist?.let {
                    Content(
                        artist = it,
                        bottomPadding = bottomPadding,
                        latestRelease = latestRelease,
                        topTracks = topTracks,
                        albums = albums,
                        singles = singles,
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
    pagerState: PagerState,
    currentOffset: State<Dp>,
    screenHeight: Dp,
    alpha: FloatState,
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
            .alpha(alpha.floatValue)
            .fillMaxWidth()
            .height(screenHeight * .7f)
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color.Transparent,
                        )
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
            .offset {
                return@offset IntOffset(0, (-currentOffset.value / 4).roundToPx())
            }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
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
    alpha: FloatState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .height(screenHeight * TopAppContentBar.TOP_PART_WEIGHT),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(alpha.floatValue)
                .align(Alignment.BottomCenter)
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Headset,
                    contentDescription = "headphones icon",
                    modifier = Modifier
                        .size(15.dp),
                    tint = Color.White
                )

                Text(
                    text = artist.listeningInMonth.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.W600,
                    lineHeight = 10.sp
                )
            }

            Text(
                text = artist.name,
                fontWeight = FontWeight.W800,
                fontSize = 40.sp,
                lineHeight = 40.sp,
                color = Color.White
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                listOf("singer", "producer", "rapper").forEach {
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(Color.White.copy(.3f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = it,
                            fontWeight = FontWeight.W600
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Content(
    artist: Artist,
    bottomPadding: Dp,
    latestRelease: Pair<BaseAlbum, Long>?,
    topTracks: List<BaseTrackWithArtists>?,
    albums: List<BaseAlbum>?,
    singles: List<BaseAlbum>?,
    onTrackClicked: (clickedTrack: BaseTrack) -> Unit,
    onAlbumClicked: (albumId: String) -> Unit
) {

    Box(
        modifier = Modifier
            .wrapContentHeight()
    ) {
        Column {
            Spacer(Modifier.height(40.dp))

            latestRelease?.let {
                LatestRelease(
                    latestAlbum = it,
                    onAlbumClicked
                )
            }

            Spacer(Modifier.height(15.dp))

            topTracks?.let {
                if (it.isNotEmpty()) {
                    TopTracks(
                        tracks = it,
                        onTrackClicked = onTrackClicked
                    )
                }
            }

            Spacer(Modifier.height(15.dp))

            albums?.let {
                if (it.isNotEmpty()) {
                    OtherAlbums(
                        message = "Альбомы ${artist.name}",
                        otherAlbums = it,
                        onAlbumClicked = onAlbumClicked
                    )
                }
            }

            Spacer(Modifier.height(15.dp))

            singles?.let {
                if (it.isNotEmpty()) {
                    OtherAlbums(
                        message = "Синглы ${artist.name}",
                        otherAlbums = it,
                        onAlbumClicked = onAlbumClicked
                    )
                }
            }

            Spacer(Modifier.height(bottomPadding))
        }
    }
}

@Composable
fun LatestRelease(
    latestAlbum: Pair<BaseAlbum, Long>,
    onAlbumClicked: (albumId: String) -> Unit
) {
    Column {
        Text(
            text = "Последний релиз",
            fontSize = 24.sp,
            fontWeight = FontWeight.W700,
            modifier = Modifier
                .padding(start = 25.dp)
        )

        Spacer(Modifier.height(15.dp))

        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    onAlbumClicked(latestAlbum.first.id)
                }
                .padding(start = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AsyncImage(
                model = latestAlbum.first.imageUrl,
                contentDescription = "latest release image",
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .height(80.dp)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )

            Column {
                Text(
                    text = latestAlbum.first.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W700
                )

                Text(
                    text = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date(latestAlbum.second * 1000)),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun TopTracks(
    tracks: List<BaseTrackWithArtists>,
    onTrackClicked: (clickedTrack: BaseTrack) -> Unit
) {
    Column {
        Text(
            text = "Популярные треки",
            fontSize = 24.sp,
            fontWeight = FontWeight.W700,
            modifier = Modifier
                .padding(start = 25.dp)
        )

        Spacer(Modifier.height(15.dp))

        for (track in tracks) {
            TrackMiniWithImage(
                track = track,
                primaryColor = Color.White,
                onPrimaryColor = Color.White,
                onClick = onTrackClicked
            )
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

private fun FlingScrollScaffoldState.calcScrollState(
    topPadding: Dp
) {
    isHeaderVisible.value = lazyListState.firstVisibleItemIndex == 0
    totalHeight.value = screenHeight * TopAppContentBar.TOP_PART_WEIGHT

    if (isHeaderVisible.value) {
        currentOffset.value = with(density) { lazyListState.firstVisibleItemScrollOffset.toDp() }

        alpha.floatValue = (totalHeight.value - topPadding - currentOffset.value) / totalHeight.value
        colorAlpha.floatValue = (totalHeight.value - topPadding - currentOffset.value) / 45.dp
    }
}