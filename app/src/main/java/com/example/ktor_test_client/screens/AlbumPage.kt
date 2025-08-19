package com.example.ktor_test_client.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.sensitiveContent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.palette.graphics.Palette
import coil3.compose.AsyncImage
import com.example.ktor_test_client.R
import com.example.ktor_test_client.controls.CircleButton
import com.example.ktor_test_client.state.ScrollState
import com.example.ktor_test_client.helpers.formatNumber
import com.example.ktor_test_client.viewmodels.ImagePaletteViewModel
import com.example.ktor_test_client.api.dtos.Album
import com.example.ktor_test_client.api.dtos.BaseAlbum
import com.example.ktor_test_client.api.dtos.BaseTrack
import com.example.ktor_test_client.controls.MiniTrack
import com.example.ktor_test_client.helpers.times
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
fun AlbumPage(
    album: Album = Album(),
    otherAlbums: List<BaseAlbum> = listOf(),
    viewModel: ImagePaletteViewModel = viewModel(),
    bottomPadding: Dp = 0.dp,
    onAlbumClicked: (artistId: String) -> Unit = { },
    onArtistClicked: (albumId: String) -> Unit = { },
    onTrackClicked: (track: BaseTrack) -> Unit = { }
) {
    val coroutineScope = rememberCoroutineScope()

    val colorScheme = MaterialTheme.colorScheme

    val context = LocalContext.current
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

    var imageBitmap: Bitmap? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        album.imageUrl?.let {
            viewModel.fetchImageByUrl(context, it)
        }

        viewModel.bitmap.collect {
            imageBitmap = it
        }
    }

    val palette: MutableState<Palette?> = remember { mutableStateOf(null) }
    val backgroundColor by remember {
        derivedStateOf {
            Color(palette.value?.vibrantSwatch?.rgb ?: colorScheme.background.toArgb())
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

    LaunchedEffect(Unit) {
        viewModel.palette.collect {
            palette.value = it
        }
    }

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
                .background(backgroundColor)
        )

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AlbumImage(
                modifier = Modifier
                    .alpha(imageAlpha),
                screenHeight = screenHeight,
                bitmap = imageBitmap
            )

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
                            AlbumHeader(
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

                item(1) {
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
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
                                MiniTrack(
                                    track = track,
                                    onClick = onTrackClicked,
                                    primaryColor = backgroundColor
                                )
                            }
                        }
                    }
                }

                item(2) {
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Spacer(Modifier.height(25.dp))

                            Text(
                                text = "Ещё от ${album.artists.first().name}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.W700,
                                modifier = Modifier
                                    .padding(start = 25.dp)
                            )

                            Spacer(Modifier.height(15.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .horizontalScroll(rememberScrollState())
                                    .background(Color.Black),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                for (i in otherAlbums.indices) {
                                    if (i == 0)
                                        Spacer(Modifier.width(20.dp))

                                    Column(
                                        modifier = Modifier
                                            .width(180.dp)
                                            .clickable {
                                                onAlbumClicked(otherAlbums[i].id)
                                            }
                                    ) {
                                        AsyncImage(
                                            model = otherAlbums[i].imageUrl,
                                            modifier = Modifier
                                                .clip(MaterialTheme.shapes.small)
                                                .fillMaxWidth()
                                                .aspectRatio(1f),
                                            contentDescription = "${otherAlbums[i].name} image",
                                            contentScale = ContentScale.Crop
                                        )

                                        Text(
                                            text = otherAlbums[i].name,
                                            fontWeight = FontWeight.W700,
                                            fontSize = 18.sp,
                                            lineHeight = 18.sp,
                                            modifier = Modifier
                                                .basicMarquee()
                                        )

                                        Text(
                                            text = otherAlbums[i].artists.first().name
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(50.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.AlbumImage(
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
                    .clip(MaterialTheme.shapes.large)
            )
        }
    }
}

@Composable
fun AlbumHeader(
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