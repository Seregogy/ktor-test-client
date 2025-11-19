package com.example.ktor_test_client.page.album

import android.graphics.Bitmap
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.ktor_test_client.api.dtos.Album
import com.example.ktor_test_client.api.dtos.BaseAlbum
import com.example.ktor_test_client.api.dtos.BaseTrack
import com.example.ktor_test_client.control.AlbumMiniPreview
import com.example.ktor_test_client.control.CircleButton
import com.example.ktor_test_client.control.TrackMini
import com.example.ktor_test_client.control.coloredscaffold.ColoredScaffold
import com.example.ktor_test_client.control.coloredscaffold.ColoredScaffoldState
import com.example.ktor_test_client.control.coloredscaffold.rememberColoredScaffoldState
import com.example.ktor_test_client.control.flingscaffold.FlingScrollScaffold
import com.example.ktor_test_client.control.flingscaffold.FlingScrollScaffoldState
import com.example.ktor_test_client.control.flingscaffold.rememberFlingScaffoldState
import com.example.ktor_test_client.control.toolscaffold.ToolScaffold
import com.example.ktor_test_client.control.toolscaffold.rememberToolScaffoldState
import com.example.ktor_test_client.viewmodel.AlbumViewModel
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

    val imageBitmap: Bitmap? by viewModel.bitmap.collectAsStateWithLifecycle()
    val album by viewModel.album
    val tracks by viewModel.tracks
    val otherAlbums by viewModel.otherAlbums

    val coloredScaffoldState = rememberColoredScaffoldState {
        viewModel.palette.collectAsStateWithLifecycle()
    }
    val toolBarScaffoldState = rememberToolScaffoldState(onBackRequest = onBackRequest)

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
                containerColor = primaryOrBackgroundColor.value.copy(.25f),
                state = rememberFlingScaffoldState(
                    yFlingOffset = toolBarInnerPadding.calculateTopPadding()
                ) {
                    calcScrollState(toolBarInnerPadding.calculateTopPadding())

                    toolBarScaffoldState.toolBarTitle.value = if (isHeaderSwiped.value.not()) {
                         album?.name
                    } else {
                        null
                    }
                },
                backgroundContent = {
                    AlbumHeaderImage(
                        bitmap = imageBitmap,
                        currentOffset = currentOffset,
                        screenHeight = screenHeight,
                        alpha = alpha
                    )
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
                    tracks?.let { tracks ->
                        AlbumContent(
                            bottomPadding = bottomPadding,
                            album = album,
                            tracks = tracks,
                            infiniteTransition = infiniteTransition,
                            onTrackClicked = onTrackClicked,
                            otherAlbums = otherAlbums,
                            onAlbumClicked = onAlbumClicked,
                            onTrackHold = {
                                toolBarScaffoldState.launchContextAction {
                                    Text(
                                        text = "Track details"
                                    )
                                }
                            }
                        )
                    }
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
            .height(screenHeight * .7f)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(alpha.floatValue)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = album.name,
                    fontWeight = FontWeight.W800,
                    fontSize = 28.sp,
                    lineHeight = 28.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onArtistClicked(album.id) }
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
                        color = Color.White
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth(.85f)
                ) {
                    CircleButton(
                        containerColor = onPrimaryOrBackgroundColor.value,
                        onClick = { },
                        underscoreText = "Скачать",
                        underscoreTextColor = Color.White
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
                        containerColor = onPrimaryOrBackgroundColor.value,
                        onClick = { },
                        underscoreText = "Нравится",
                        underscoreTextColor = Color.White
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
                        containerColor = onPrimaryOrBackgroundColor.value,
                        onClick = { },
                        underscoreText = "Трейлер",
                        underscoreTextColor = Color.White
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
                        containerColor = onPrimaryOrBackgroundColor.value,
                        onClick = { },
                        underscoreText = "Слушать",
                        underscoreTextColor = Color.White
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
    }
}

@Composable
private fun ColoredScaffoldState.AlbumContent(
    bottomPadding: Dp,
    album: Album,
    tracks: List<BaseTrack>,
    infiniteTransition: InfiniteTransition,
    onTrackClicked: (track: BaseTrack) -> Unit,
    onTrackHold: (track: BaseTrack) -> Unit,
    otherAlbums: List<BaseAlbum>,
    onAlbumClicked: (artistId: String) -> Unit
) {
    Column {
        Spacer(Modifier.height(20.dp))

        for (track in tracks) {
            TrackMini(
                track = track,
                infiniteTransition = infiniteTransition,
                primaryColor = primaryOrBackgroundColorAnimated.value,
                onPrimaryColor = Color.White,
                onClick = onTrackClicked,
                onContextAction = onTrackHold
            )
        }

        Spacer(Modifier.height(20.dp))

        OtherAlbums("Ещё от ${album.artists.first().name}", otherAlbums, onAlbumClicked)

        Spacer(Modifier.height(bottomPadding))
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
private fun AlbumHeaderImage(
    modifier: Modifier = Modifier,
    bitmap: Bitmap?,
    currentOffset: State<Dp>,
    screenHeight: Dp,
    alpha: FloatState
) {
    Box(
        modifier = modifier
            .alpha(alpha.floatValue)
            .height(screenHeight * .7f)
            .offset {
                return@offset IntOffset(0, (-currentOffset.value / 4).roundToPx())
            }
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(.85f),
                            Color.Transparent,
                        )
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

private fun FlingScrollScaffoldState.calcScrollState(
    topPadding: Dp
) {
    isHeaderVisible.value = lazyListState.firstVisibleItemIndex == 0
    totalHeight.value = screenHeight * .7f

    if (isHeaderVisible.value) {
        currentOffset.value = with(density) { lazyListState.firstVisibleItemScrollOffset.toDp() }

        alpha.floatValue = (totalHeight.value - topPadding - currentOffset.value) / totalHeight.value
        colorAlpha.floatValue = (totalHeight.value - topPadding - currentOffset.value) / 45.dp
    }
}