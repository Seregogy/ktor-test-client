package com.example.ktor_test_client.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.ktor_test_client.Library
import com.example.ktor_test_client.R
import com.example.ktor_test_client.components.CircleButton
import com.example.ktor_test_client.models.Album
import com.example.ktor_test_client.state.ScrollState
import com.example.ktor_test_client.helpers.formatNumber
import com.valentinilk.shimmer.shimmer

@Composable
fun AlbumPage(
    album: Album,
    onNavigateToArtist: (artistId: Int) -> Unit = { }
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val density = LocalDensity.current

    val lazyListState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(SnapLayoutInfoProvider(lazyListState, SnapPosition.Start))

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

    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .alpha(scrollState.value.colorAlpha)
                .fillMaxSize()
                .background(album.primaryColor)
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
                album = album
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
                                album = album
                            ) {
                                onNavigateToArtist(album.artistId)
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
                            targetColor = album.primaryColor
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
                                text = "Ещё треки",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.W700,
                                modifier = Modifier
                                    .padding(start = 25.dp)
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
            }
        }
    }
}

@Composable
private fun BoxScope.AlbumImage(
    modifier: Modifier,
    screenHeight: Dp,
    album: Album
) {
    Box(
        modifier = modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth()
            .height(screenHeight * TopAppContentBar.topPartWeight)
    ) {
        AsyncImage(
            model = album.imageUrl,
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

@Composable
fun AlbumHeader(
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
            lineHeight = 10.sp
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
                model = Library.artists.first { it.id == album.artistId }.imagesUrl.first().first,
                contentDescription = "mini artist avatar",
                modifier = Modifier
                    .size(25.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Text(
                text = Library.artists.first { it.id == album.artistId }.name,
                fontWeight = FontWeight.W700
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
                    painter = painterResource(R.drawable.download_icon),
                    modifier = Modifier
                        .size(40.dp),
                    contentDescription = ""
                )
            }

            CircleButton(
                onClick = { },
                underscoreText = formatNumber(album.likes),
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