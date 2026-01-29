package com.example.ktor_test_client.control.player

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.ktor_test_client.api.dtos.TrackFullDto
import com.example.ktor_test_client.control.ContextMenu
import com.example.ktor_test_client.control.MarqueeText
import com.example.ktor_test_client.control.coloredscaffold.ColoredScaffoldState
import com.example.ktor_test_client.layout.AvatarRow

@Composable
fun ColoredScaffoldState.TrackInfo(
    currentTrackFullDto: TrackFullDto?,
    isTrackLoading: State<Boolean>,
    onAlbumClicked: (albumId: String) -> Unit,
    onArtistClicked: (artistId: String) -> Unit
) {
    val artistsSheet = remember { mutableStateOf(false) }
    val density = LocalDensity.current

    val infiniteTransition = rememberInfiniteTransition("cycling animation transition")
    val textAlphaAnimated by infiniteTransition.animateFloat(
        initialValue = .3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "text alpha animation"
    )

    val textAlpha by remember {
        derivedStateOf {
            if (isTrackLoading.value) textAlphaAnimated else 1f
        }
    }

    var columnSize by remember { mutableStateOf(IntSize.Zero) }

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (columnSize.height != 0) {
            AvatarRow(
                spaceBetween = 5.dp
            ) {
                currentTrackFullDto?.album?.artists?.forEach { artist ->
                    Box {
                        AsyncImage(
                            model = artist.imageUrl,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .height(with(density) { columnSize.height.toDp() })
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .clickable {
                                    onArtistClicked(artist.id)
                                },
                            contentDescription = "mini avatar"
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .onSizeChanged {
                    columnSize = it
                }
        ) {
            MarqueeText(
                text = currentTrackFullDto?.name ?: "",
                fontSize = 30.sp,
                fontWeight = FontWeight.W800,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        onAlbumClicked(currentTrackFullDto?.album?.id ?: "")
                    }
                    .alpha(textAlpha),
                color = textOnPrimaryOrBackgroundColorAnimated.value
            )

            MarqueeText(
                text = currentTrackFullDto?.album?.artists?.joinToString(", ") { it.name } ?: "unknown",
                fontWeight = FontWeight.W600,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        if ((currentTrackFullDto?.album?.artists?.size ?: 0) == 1) {
                            onArtistClicked(currentTrackFullDto?.album?.artists?.first()?.id ?: "")
                        } else {
                            artistsSheet.value = true
                        }
                    }
                    .alpha(textAlpha),
                color = onBackgroundColorAnimated.value
            )
        }
    }

    ContextMenu(artistsSheet) { padding ->
        currentTrackFullDto?.album?.artists?.let { artists ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 25.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                item {
                    Text(
                        text = "Артисты",
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .padding(bottom = 15.dp),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.W700
                    )
                }

                items(artists) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                onArtistClicked(it.id)
                                artistsSheet.value = false
                            }
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        AsyncImage(
                            model = it.imageUrl,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(60.dp),
                            contentDescription = "",
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = it.name,
                            fontWeight = FontWeight.W600
                        )
                    }
                }
            }
        }
    }
}