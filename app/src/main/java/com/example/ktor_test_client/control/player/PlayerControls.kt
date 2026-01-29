package com.example.ktor_test_client.control.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.ktor_test_client.control.CircleButton
import com.example.ktor_test_client.control.coloredscaffold.ColoredScaffoldState

@Composable
fun ColoredScaffoldState.PlayerControls(
    modifier: Modifier = Modifier,
    secondaryColorWithLoadingState: Color,
    isPlay: State<Boolean>,
    isLastTrack: State<Boolean>,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onPlayPause: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val nextTrackLoadedColorState by remember {
        derivedStateOf {
            if (isLastTrack.value) {
                onBackgroundColorAnimated.value.copy(.3f)
            } else {
                onBackgroundColorAnimated.value
            }
        }
    }

    val fullyTransparentForeground by remember {
        derivedStateOf {
            onBackgroundColorAnimated.value.copy(.15f)
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(
            onClick = {
                haptic.performHapticFeedback(
                    HapticFeedbackType.ContextClick
                )

                onPrev()
            }
        ) {
            Icon(
                imageVector = Icons.Rounded.SkipPrevious,
                contentDescription = "prev icon",
                tint = secondaryColorWithLoadingState,
                modifier = Modifier
                    .size(34.dp)
            )
        }

        CircleButton(
            containerColor = fullyTransparentForeground,
            size = 70.dp,
            onClick = {
                haptic.performHapticFeedback(
                    HapticFeedbackType.Confirm
                )

                onPlayPause()
            },
            content = {
                Icon(
                    imageVector = if (isPlay.value)
                        Icons.Rounded.Pause
                    else
                        Icons.Rounded.PlayArrow,
                    contentDescription = "play/pause icon",
                    tint = onBackgroundColorAnimated.value,
                    modifier = Modifier
                        .size(36.dp)
                )
            }
        )

        IconButton(
            onClick = {
                haptic.performHapticFeedback(
                    HapticFeedbackType.ContextClick
                )

                onNext()
            },
            enabled = isLastTrack.value.not()
        ) {
            Icon(
                imageVector = Icons.Rounded.SkipNext,
                contentDescription = "next icon",
                tint = nextTrackLoadedColorState,
                modifier = Modifier
                    .size(34.dp)
            )
        }
    }
}