package com.example.ktor_test_client.controls.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.controls.TrackControl
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel

@Composable
fun MiniAudioPlayer(
    viewModel: AudioPlayerViewModel,
    miniPlayerHeight: Dp,
    scaffoldInnerPadding: PaddingValues,
    onExpandRequest: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    val currentTrack by viewModel.currentTrack.collectAsStateWithLifecycle()
    val palette by viewModel.palette.collectAsStateWithLifecycle()

    val backgroundColor by remember {
        derivedStateOf {
            Color(palette?.vibrantSwatch?.rgb ?: colorScheme.background.toArgb())
        }
    }

    val foregroundColor by remember {
        derivedStateOf {
            Color(palette?.vibrantSwatch?.titleTextColor ?: colorScheme.onBackground.toArgb())
        }
    }

    val isPlay by remember { viewModel.isPlay }

    Box(
        modifier = Modifier
            .background(Color.Transparent)
    ) {
        TrackControl(
            modifier = Modifier
                .padding(bottom = scaffoldInnerPadding.calculateBottomPadding())
                .height(miniPlayerHeight)
                .fillMaxWidth()
                .padding(15.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(backgroundColor),
            onClick = { onExpandRequest() },
            foregroundColor = foregroundColor,
            track = currentTrack ?: Track()
        )

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 30.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { }
            ) {
                Icon(
                    imageVector = if (false)
                        Icons.Rounded.Favorite
                    else
                        Icons.Rounded.FavoriteBorder,
                    contentDescription = "favorite icon button",
                    modifier = Modifier
                        .size(24.dp),
                    tint = foregroundColor
                )
            }

            IconButton(
                onClick = {
                    viewModel.playPause()
                }
            ) {
                Icon(
                    imageVector = if (isPlay)
                        Icons.Rounded.Pause
                    else
                        Icons.Rounded.PlayArrow,
                    contentDescription = "play/pause icon",
                    modifier = Modifier
                        .size(26.dp),
                    tint = foregroundColor
                )
            }
        }
    }
}