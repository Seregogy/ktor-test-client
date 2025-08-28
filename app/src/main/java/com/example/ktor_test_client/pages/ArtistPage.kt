package com.example.ktor_test_client.pages

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.ktor_test_client.api.dtos.Artist
import com.example.ktor_test_client.api.dtos.BaseAlbum
import com.example.ktor_test_client.api.dtos.BaseTrack
import com.example.ktor_test_client.controls.CircleButton
import com.example.ktor_test_client.controls.TrackMiniWithImage
import com.example.ktor_test_client.helpers.formatNumber
import com.example.ktor_test_client.state.ScrollState
import com.example.ktor_test_client.viewmodels.ArtistViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object TopAppContentBar {
    const val topPartWeight = .55f
    val additionalHeight = 60.dp
}

@Composable
fun ArtistPage(
    viewModel: ArtistViewModel,
    onTrackClicked: (clickedTrack: BaseTrack) -> Unit,
    onAlbumClicked: (albumId: String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(0) { viewModel.artist.value?.images?.size ?: 0 }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val density = LocalDensity.current

    val lazyListState = rememberLazyListState()

    val noSnapLayout = object : SnapLayoutInfoProvider {
        override fun calculateSnapOffset(velocity: Float): Float {
            return velocity
        }
    }
    val snapLayoutInfoProvider = SnapLayoutInfoProvider(lazyListState, SnapPosition.Start)

    val isFirstVisibleIndex by remember {
        var lastVisibleIndex = 0
        derivedStateOf {
            if (lastVisibleIndex >= 1 && lazyListState.firstVisibleItemIndex == 0) {
                coroutineScope.launch {
                    delay(300)

                    if (lazyListState.firstVisibleItemIndex == 0)
                        lazyListState.animateScrollToItem(0)
                }
            }

            lastVisibleIndex = lazyListState.firstVisibleItemIndex

            lazyListState.firstVisibleItemIndex == 0
        }
    }

    val flingBehavior = rememberSnapFlingBehavior(if (isFirstVisibleIndex) snapLayoutInfoProvider else noSnapLayout)

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

    val palette = viewModel.palette.collectAsState()

    val backgroundColor = remember {
        derivedStateOf {
            Color(palette.value?.dominantSwatch?.rgb ?: Color.Black.toArgb())
        }
    }

    val primaryColor = remember {
        derivedStateOf {
            Color(palette.value?.vibrantSwatch?.rgb ?: Color.Black.toArgb())
        }
    }


    val foregroundColor by remember {
        derivedStateOf {
            Color(palette.value?.vibrantSwatch?.titleTextColor ?: colorScheme.onBackground.toArgb())
        }
    }

    val iconsColor by remember {
        derivedStateOf {
            Color(palette.value?.vibrantSwatch?.bodyTextColor ?: colorScheme.onBackground.toArgb())
        }
    }

    val primaryButtonColor by remember {
        derivedStateOf {
            Color(palette.value?.mutedSwatch?.rgb ?: colorScheme.onSurface.toArgb())
        }
    }

    val primaryIconColor by remember {
        derivedStateOf {
            Color(palette.value?.mutedSwatch?.titleTextColor ?: colorScheme.onSecondary.toArgb())
        }
    }

    val backgroundColorAnimated = animateColorAsState(
        targetValue = backgroundColor.value,
        animationSpec = tween(durationMillis = 700, easing = LinearEasing),
        label = "animated color"
    )

    artist?.let {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            ArtistAvatarPager(
                viewModel = viewModel,
                color = backgroundColorAnimated,
                pagerState = pagerState,
                scrollState = scrollState,
                screenHeight = screenHeight,
                artist = it
            )

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
                    Header(
                        screenHeight,
                        scrollState,
                        backgroundColorAnimated,
                        it
                    )
                }

                item(1) {
                    Content(
                        it,
                        scrollState,
                        backgroundColorAnimated,
                        primaryColor,
                        topTracks ?: listOf(),
                        albums ?: listOf(),
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
    scrollState: State<ScrollState>,
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
    screenHeight: Dp,
    scrollState: State<ScrollState>,
    color: State<Color>,
    artist: Artist
) {
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
    scrollState: State<ScrollState>,
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
                .alpha(scrollState.value.colorAlpha),
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
        Spacer(Modifier.height(25.dp))

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