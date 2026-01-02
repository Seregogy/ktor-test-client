package com.example.ktor_test_client.control

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.ktor_test_client.api.dtos.TrackFullDto
import com.example.ktor_test_client.viewmodel.AudioPlayerViewModel

@Composable
fun TrackControl(
    modifier: Modifier = Modifier,
    viewModel: AudioPlayerViewModel,
    onClick: (it: TrackFullDto) -> Unit = { },
    controls: @Composable RowScope.() -> Unit
) {
    val track by viewModel.audioPlayer.currentTrack.collectAsState()
    val currentPosition by viewModel.audioPlayer.currentPosition
    val currentTrackDuration by viewModel.audioPlayer.currentTrackDuration.collectAsState()

    val trackTimelinePosition by remember {
        derivedStateOf {
            currentPosition.toFloat() / currentTrackDuration
        }
    }

    val density = LocalDensity.current
    val artistsNames = track?.data?.artists?.joinToString(", ") { it.name } ?: "unknown"
    var trackControlsHeight by remember { mutableStateOf(0.dp) }

    Box(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .padding(vertical = 10.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                track?.data?.let {
                    onClick(it)
                }
            }
            .background(Color.White.copy(.1f))
    ) {
        Box(
            modifier = Modifier
                .height(trackControlsHeight)
                .background(Color.White.copy(.1f))
                .fillMaxWidth(trackTimelinePosition)
                .align(Alignment.CenterStart)
        )

        Row(
            modifier = Modifier
                .onSizeChanged {
                    trackControlsHeight = with(density) { it.height.toDp() }
                }
                .padding(7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier

                    .weight(5f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                AsyncImage(
                    model = track?.data?.imageUrl,
                    modifier = Modifier
                        .height(45.dp)
                        .aspectRatio(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(5.dp)),
                    contentDescription = "mini track image",
                    contentScale = ContentScale.Crop
                )

                Column {
                    MarqueeText(
                        text = track?.data?.name ?: "unknown",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W700,
                        color = Color.White,
                        maxLines = 1,
                        lineHeight = 16.sp,
                        textAlign = Alignment.CenterStart
                    )

                    MarqueeText(
                        text = artistsNames,
                        fontSize = 13.sp,
                        color = Color.White.copy(.7f),
                        lineHeight = 13.sp,
                        textAlign = Alignment.CenterStart
                    )
                }
            }

            controls()
        }
    }
}