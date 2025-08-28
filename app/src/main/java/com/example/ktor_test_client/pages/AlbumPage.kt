package com.example.ktor_test_client.pages

import android.graphics.Bitmap
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Downloading
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.ktor_test_client.R
import com.example.ktor_test_client.api.dtos.Album
import com.example.ktor_test_client.api.dtos.BaseAlbum
import com.example.ktor_test_client.api.dtos.BaseTrack
import com.example.ktor_test_client.controls.AlbumMiniPreview
import com.example.ktor_test_client.controls.CircleButton
import com.example.ktor_test_client.controls.coloredscaffold.ColoredScaffold
import com.example.ktor_test_client.controls.TrackMini
import com.example.ktor_test_client.controls.coloredscaffold.rememberColoredScaffoldState
import com.example.ktor_test_client.helpers.formatNumber
import com.example.ktor_test_client.helpers.times
import com.example.ktor_test_client.state.ScrollState
import com.example.ktor_test_client.viewmodels.AlbumViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AlbumPage(
    viewModel: AlbumViewModel,
    bottomPadding: Dp = 0.dp,
    onAlbumClicked: (artistId: String) -> Unit = { },
    onArtistClicked: (albumId: String) -> Unit = { },
    onTrackClicked: (track: BaseTrack) -> Unit = { }
) {
    val infiniteTransition = rememberInfiniteTransition("infinity transition animation")

    val coroutineScope = rememberCoroutineScope()

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val density = LocalDensity.current

    val lazyListState = rememberLazyListState()

    val noSnapLayout = object : SnapLayoutInfoProvider {
        override fun calculateSnapOffset(velocity: Float): Float {
            return velocity
        }
    }
    val snapLayoutInfoProvider = SnapLayoutInfoProvider(lazyListState, SnapPosition.Start)

    //TODO: Подумать как улучшить этот костыль (частичный fling эффект)
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

    var imageAlpha: Float by remember { mutableFloatStateOf(1f) }
    val albumHeaderHeight = 150.dp

    val scrollState: State<ScrollState> = remember {
        derivedStateOf {
            val isAvatarVisible = lazyListState.firstVisibleItemIndex == 0
            val scrollState = ScrollState(isAvatarVisible = isAvatarVisible)
            val totalHeight = screenHeight * TopAppContentBar.topPartWeight + TopAppContentBar.additionalHeight

            if (scrollState.isAvatarVisible) {
                scrollState.currentOffset = with(density) { lazyListState.firstVisibleItemScrollOffset.toDp() }

                scrollState.alpha = ((totalHeight - scrollState.currentOffset) / totalHeight).coerceIn(0f..1f)
                scrollState.colorAlpha = ((totalHeight - scrollState.currentOffset) / 60.dp).coerceIn(0f..1f)

                imageAlpha = (albumHeaderHeight - scrollState.currentOffset) / albumHeaderHeight
            }

            return@derivedStateOf scrollState
        }
    }

    val imageBitmap: Bitmap? by viewModel.bitmap.collectAsStateWithLifecycle()
    val album by viewModel.album
    val otherAlbums by viewModel.otherAlbums


    ColoredScaffold(
        state = rememberColoredScaffoldState {
            viewModel.palette.collectAsStateWithLifecycle()
        }
    ) {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxSize()
                .padding(bottom = bottomPadding)
        ) {
            Box(
                modifier = Modifier
                    .alpha(scrollState.value.colorAlpha)
                    .fillMaxSize()
                    .background(primaryColor.value)
            ) {
                AlbumHeaderImage(
                    modifier = Modifier
                        .alpha(imageAlpha),
                    screenHeight = screenHeight,
                    bitmap = imageBitmap
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {


                LazyColumn(
                    state = lazyListState,
                    flingBehavior = flingBehavior,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .pointerInteropFilter {
                            return@pointerInteropFilter false
                        }
                ) {
                    item(0) {
                        album?.let { album ->
                            AlbumHeader(
                                screenHeight = screenHeight,
                                scrollState = scrollState,
                                album = album,
                                foregroundColor = onPrimaryColor.value,
                                backgroundColor = primaryColor.value,
                                iconsColor = primaryColor.value,
                                primaryButtonColor = primaryColor.value,
                                primaryIconColor = primaryColor.value,
                                onArtistClicked = onArtistClicked
                            )
                        }
                    }

                    item(1) {
                        album?.let { album ->
                            otherAlbums?.let { otherAlbums ->
                                AlbumContent(
                                    scrollState,
                                    primaryColor.value,
                                    album,
                                    infiniteTransition,
                                    onTrackClicked,
                                    otherAlbums,
                                    onAlbumClicked
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
private fun AlbumHeader(
    screenHeight: Dp,
    scrollState: State<ScrollState>,
    album: Album,
    foregroundColor: Color,
    backgroundColor: Color,
    iconsColor: Color,
    primaryButtonColor: Color,
    primaryIconColor: Color,
    onArtistClicked: (albumId: String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight * TopAppContentBar.topPartWeight + TopAppContentBar.additionalHeight)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            AlbumHeaderControls(
                modifier = Modifier
                    .alpha(scrollState.value.alpha)
                    .align(Alignment.BottomCenter),
                album = album,
                foregroundColor = foregroundColor,
                backgroundColor = backgroundColor,
                iconsColor = iconsColor,
                primaryButtonColor = primaryButtonColor,
                primaryIconColor = primaryIconColor
            ) {
                onArtistClicked(album.artists.first().id)
            }
        }
    }
}

@Composable
private fun AlbumContent(
    scrollState: State<ScrollState>,
    backgroundColor: Color,
    album: Album,
    infiniteTransition: InfiniteTransition,
    onTrackClicked: (track: BaseTrack) -> Unit,
    otherAlbums: List<BaseAlbum>,
    onAlbumClicked: (artistId: String) -> Unit
) {
    Box(
        modifier = Modifier
            .background(Color.Black)
    ) {
        AlbumHeaderFadingGradientBottom(
            modifier = Modifier
                .alpha(scrollState.value.colorAlpha),
            targetColor = backgroundColor
        )

        Column {
            Spacer(Modifier.height(30.dp))

            for (track in album.tracks) {
                TrackMini(
                    track = track,
                    infiniteTransition = infiniteTransition,
                    onClick = onTrackClicked,
                    primaryColor = backgroundColor,
                    onPrimaryColor = Color.White
                )
            }

            Spacer(Modifier.height(20.dp))

            OtherAlbums("Ещё от ${album.artists.first().name}", otherAlbums, onAlbumClicked)
        }
    }
}

@Composable
fun OtherAlbums(
    message: String?,
    otherAlbums: List<BaseAlbum>,
    onAlbumClicked: (artistId: String) -> Unit
) {
    message?.let {
        Text(
            text = message,
            fontSize = 24.sp,
            fontWeight = FontWeight.W700,
            modifier = Modifier
                .padding(start = 25.dp)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .padding(bottom = 25.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.Top
    ) {
        Spacer(Modifier.width(20.dp))

        for (otherAlbum in otherAlbums) {
            AlbumMiniPreview(onAlbumClicked, otherAlbum)
        }

        Spacer(Modifier.width(25.dp))
    }
}

@Composable
private fun BoxScope.AlbumHeaderImage(
    modifier: Modifier,
    screenHeight: Dp,
    bitmap: Bitmap?
) {
    Box(
        modifier = modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth()
            .height(screenHeight * TopAppContentBar.topPartWeight)
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.Center)
                    .aspectRatio(1f)
                    .fillMaxSize()
                    .offset(y = (-20).dp)
                    .padding(75.dp)
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun AlbumHeaderControls(
    modifier: Modifier = Modifier,
    album: Album,
    foregroundColor: Color,
    backgroundColor: Color,
    iconsColor: Color,
    primaryButtonColor: Color,
    primaryIconColor: Color,
    onArtistClick: () -> Unit = { }
) {
    Column(
        modifier = modifier
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = album.name,
            fontWeight = FontWeight.W800,
            fontSize = 28.sp,
            lineHeight = 10.sp,
            color = foregroundColor
        )

        Row(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable {
                    onArtistClick()
                }
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            AsyncImage(
                model = album.artists.first().imageUrl,
                contentDescription = "mini artist avatar",
                modifier = Modifier
                    .size(25.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Text(
                text = album.artists.first().name,
                fontWeight = FontWeight.W700,
                color = foregroundColor
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(.85f)
        ) {
            CircleButton(
                onClick = { },
                underscoreText = "Скачать",
            ) {
                Icon(
                    imageVector = Icons.Rounded.Downloading,
                    modifier = Modifier
                        .size(28.dp),
                    contentDescription = "",
                    tint = iconsColor
                )
            }

            CircleButton(
                onClick = { },
                underscoreText = formatNumber(album.likes),
            ) {
                Icon(
                    imageVector = Icons.Rounded.FavoriteBorder,
                    modifier = Modifier
                        .size(28.dp),
                    contentDescription = "",
                    tint = iconsColor
                )
            }

            CircleButton(
                onClick = { },
                underscoreText = "Трейлер",
            ) {
                Icon(
                    painter = painterResource(R.drawable.queue_music_icon),
                    modifier = Modifier
                        .size(28.dp),
                    contentDescription = "",
                    tint = iconsColor
                )
            }

            CircleButton(
                containerColor = backgroundColor.times(1.5f),
                onClick = { },
                underscoreText = "Слушать"
            ) {
                Icon(
                    painter = painterResource(R.drawable.pause_icon),
                    modifier = Modifier
                        .size(30.dp),
                    contentDescription = "",
                    tint = iconsColor
                )
            }
        }
    }
}

@Composable
fun AlbumHeaderFadingGradientBottom(
    modifier: Modifier,
    targetColor: Color
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
                            .1f to targetColor,
                            .9f to Color.Transparent
                        )
                    )
                )
        )
    }
}