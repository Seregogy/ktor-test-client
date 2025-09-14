package com.example.ktor_test_client.pages

import android.graphics.Bitmap
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Downloading
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.ktor_test_client.api.dtos.Album
import com.example.ktor_test_client.api.dtos.BaseAlbum
import com.example.ktor_test_client.api.dtos.BaseTrack
import com.example.ktor_test_client.controls.AlbumMiniPreview
import com.example.ktor_test_client.controls.CircleButton
import com.example.ktor_test_client.controls.TrackMini
import com.example.ktor_test_client.controls.coloredscaffold.ColoredScaffold
import com.example.ktor_test_client.controls.coloredscaffold.ColoredScaffoldState
import com.example.ktor_test_client.controls.coloredscaffold.rememberColoredScaffoldState
import com.example.ktor_test_client.controls.flingscaffold.FlingScrollScaffold
import com.example.ktor_test_client.controls.flingscaffold.FlingScrollScaffoldState
import com.example.ktor_test_client.controls.flingscaffold.rememberFlingScaffoldState
import com.example.ktor_test_client.controls.toolscaffold.ToolScaffold
import com.example.ktor_test_client.controls.toolscaffold.rememberToolScaffoldState
import com.example.ktor_test_client.helpers.formatNumber
import com.example.ktor_test_client.helpers.times
import com.example.ktor_test_client.viewmodels.AlbumViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun AlbumPage(
    viewModel: AlbumViewModel,
    innerPadding: PaddingValues,
    bottomPadding: Dp,
    hazeState: HazeState,
    onBackRequest: () -> Unit = { },
    onAlbumClicked: (artistId: String) -> Unit = { },
    onArtistClicked: (albumId: String) -> Unit = { },
    onTrackClicked: (track: BaseTrack) -> Unit = { }
) {
    val infiniteTransition = rememberInfiniteTransition("infinity transition animation")

    val imageAlpha = remember { mutableFloatStateOf(1f) }
    val albumHeaderHeight = 150.dp

    val imageBitmap: Bitmap? by viewModel.bitmap.collectAsStateWithLifecycle()
    val album by viewModel.album
    val otherAlbums by viewModel.otherAlbums

    val coloredScaffoldState = rememberColoredScaffoldState {
        viewModel.palette.collectAsStateWithLifecycle()
    }
    val toolBarScaffoldState = rememberToolScaffoldState<Nothing, Nothing>(onBackRequest = onBackRequest)

    val topBarHazeState = rememberHazeState()
    ColoredScaffold(
        state = coloredScaffoldState
    ) {
        ToolScaffold(
            modifier = Modifier
                .padding(innerPadding),
            state = toolBarScaffoldState,
            hazeState = topBarHazeState
        ) { toolBarInnerPadding ->

            FlingScrollScaffold(
                modifier = Modifier
                    .hazeSource(state = hazeState)
                    .hazeSource(state = topBarHazeState)
                    .background(Color.Black)
                    .fillMaxSize(),
                state = rememberFlingScaffoldState(
                    yFlingOffset = toolBarInnerPadding.calculateTopPadding()
                ) {
                    calcScrollState(imageAlpha, albumHeaderHeight, toolBarInnerPadding.calculateTopPadding())

                    toolBarScaffoldState.toolBarTitle.value = if (isHeaderSwiped.value.not()) {
                         album?.name
                    } else {
                        null
                    }
                },
                backgroundContent = {
                    Box(
                        modifier = Modifier
                            .alpha(colorAlpha.floatValue)
                            .fillMaxSize()
                            .background(primaryOrBackgroundColorAnimated.value)
                    ) {
                        AlbumHeaderImage(
                            modifier = Modifier
                                .alpha(imageAlpha.floatValue),
                            innerPadding = toolBarInnerPadding,
                            screenHeight = screenHeight,
                            bitmap = imageBitmap
                        )
                    }
                },
                headingContent = {
                    album?.let { album ->
                        AlbumHeader(
                            screenHeight = screenHeight,
                            alpha = alpha,
                            album = album,
                            onArtistClicked = onArtistClicked
                        )
                    }
                }
            ) {
                album?.let { album ->
                    AlbumContent(
                        colorAlpha = colorAlpha,
                        bottomPadding = bottomPadding,
                        album = album,
                        infiniteTransition = infiniteTransition,
                        onTrackClicked = onTrackClicked,
                        otherAlbums = otherAlbums,
                        onAlbumClicked = onAlbumClicked
                    )
                }
            }
        }
    }
}

@Composable
private fun ColoredScaffoldState.AlbumHeader(
    screenHeight: Dp,
    alpha: FloatState,
    album: Album,
    onArtistClicked: (albumId: String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight * TopAppContentBar.TOP_PART_WEIGHT + TopAppContentBar.additionalHeight)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            AlbumHeaderControls(
                modifier = Modifier
                    .alpha(alpha.floatValue)
                    .align(Alignment.BottomCenter),
                album = album
            ) {
                onArtistClicked(album.artists.first().id)
            }
        }
    }
}

@Composable
private fun ColoredScaffoldState.AlbumContent(
    colorAlpha: FloatState,
    bottomPadding: Dp,
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
                .alpha(colorAlpha.floatValue),
            targetColor = primaryOrBackgroundColorAnimated.value
        )

        Column {
            Spacer(Modifier.height(30.dp))

            for (track in album.tracks) {
                TrackMini(
                    track = track,
                    infiniteTransition = infiniteTransition,
                    onClick = onTrackClicked,
                    primaryColor = primaryOrBackgroundColorAnimated.value,
                    onPrimaryColor = Color.White
                )
            }

            Spacer(Modifier.height(20.dp))

            OtherAlbums("Ещё от ${album.artists.first().name}", otherAlbums, onAlbumClicked)

            Spacer(Modifier.height(bottomPadding))
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
    innerPadding: PaddingValues,
    screenHeight: Dp,
    bitmap: Bitmap?
) {
    val defaultImagePaddingDp = 75.dp

    Box(
        modifier = modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth()
            .height(screenHeight * TopAppContentBar.TOP_PART_WEIGHT)
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
                    .padding(defaultImagePaddingDp)
//                    .padding(top = defaultImagePaddingDp - innerPadding.calculateTopPadding(), bottom = defaultImagePaddingDp)
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun ColoredScaffoldState.AlbumHeaderControls(
    modifier: Modifier = Modifier,
    album: Album,
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
            lineHeight = 28.sp,
            color = onPrimaryOrBackgroundColorAnimated.value,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
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
                color = onPrimaryOrBackgroundColorAnimated.value
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(.85f)
        ) {
            CircleButton(
                containerColor = onPrimaryOrBackgroundColorAnimated.value,
                onClick = { },
                underscoreText = "Скачать",
                underscoreTextColor = onPrimaryOrBackgroundColorAnimated.value
            ) {
                Icon(
                    imageVector = Icons.Rounded.Downloading,
                    modifier = Modifier
                        .size(28.dp),
                    contentDescription = "",
                    tint = primaryOrBackgroundColorAnimated.value
                )
            }

            CircleButton(
                containerColor = onPrimaryOrBackgroundColorAnimated.value,
                onClick = { },
                underscoreText = formatNumber(album.likes),
                underscoreTextColor = onPrimaryOrBackgroundColorAnimated.value
            ) {
                Icon(
                    imageVector = Icons.Rounded.FavoriteBorder,
                    modifier = Modifier
                        .size(28.dp),
                    contentDescription = "",
                    tint = primaryOrBackgroundColorAnimated.value
                )
            }

            CircleButton(
                containerColor = onPrimaryOrBackgroundColorAnimated.value,
                onClick = { },
                underscoreText = "Трейлер",
                underscoreTextColor = onPrimaryOrBackgroundColorAnimated.value
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                    modifier = Modifier
                        .size(28.dp),
                    contentDescription = "",
                    tint = primaryOrBackgroundColorAnimated.value
                )
            }

            CircleButton(
                containerColor = textOnPrimaryOrBackgroundColorAnimated.value.times(1.2f),
                onClick = { },
                underscoreText = "Слушать",
                underscoreTextColor = onPrimaryOrBackgroundColorAnimated.value
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    modifier = Modifier
                        .size(30.dp),
                    contentDescription = "",
                    tint = primaryOrBackgroundColorAnimated.value
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

private fun FlingScrollScaffoldState.calcScrollState(
    imageAlpha: MutableFloatState,
    albumHeaderHeight: Dp,
    topPadding: Dp
) {
    isHeaderVisible.value = lazyListState.firstVisibleItemIndex == 0
    totalHeight.value = screenHeight * TopAppContentBar.TOP_PART_WEIGHT + TopAppContentBar.additionalHeight

    if (isHeaderVisible.value) {
        currentOffset.value = with(density) { lazyListState.firstVisibleItemScrollOffset.toDp() }

        alpha.floatValue = ((totalHeight.value - topPadding - currentOffset.value) / totalHeight.value).coerceIn(0f..1f)
        colorAlpha.floatValue = ((totalHeight.value - topPadding - currentOffset.value) / 60.dp).coerceIn(0f..1f)

        imageAlpha.floatValue = (albumHeaderHeight - currentOffset.value) / albumHeaderHeight
    }
}