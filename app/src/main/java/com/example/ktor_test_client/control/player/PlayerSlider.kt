package com.example.ktor_test_client.control.player

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.ktor_test_client.control.coloredscaffold.ColoredScaffoldState
import com.example.ktor_test_client.helper.times
import com.example.ktor_test_client.viewmodel.AudioPlayerViewModel
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoredScaffoldState.PlayerSlider(
    modifier: Modifier = Modifier,
    currentPosition: MutableState<Long>,
    currentTrackDuration: Long,
    viewModel: AudioPlayerViewModel,
    isSliding: MutableState<Boolean>,
) {
    val haptic = LocalHapticFeedback.current

    val tickCount = 50
    val sliderGap = 1f / tickCount
    var currentGap by remember { mutableFloatStateOf(0f) }

    var localPosition by remember { mutableFloatStateOf(0f) }

    val compositionPosition by remember {
        derivedStateOf {
            if (!isSliding.value) {
                localPosition = (currentPosition.value / viewModel.audioPlayer.currentTrackDuration.value.toFloat()).coerceIn(0f..currentTrackDuration.toFloat())

                Log.d("Player", (currentPosition.value / viewModel.audioPlayer.currentTrackDuration.value.toFloat()).toString())
            }
            localPosition
        }
    }

    val semiTransparentForeground by remember {
        derivedStateOf {
            onBackgroundColorAnimated.value.copy(.65f)
        }
    }

    val fullyTransparentForeground by remember {
        derivedStateOf {
            onBackgroundColorAnimated.value.copy(.15f)
        }
    }

    val currentPositionAnimated by animateFloatAsState(
        targetValue = compositionPosition,
        animationSpec = if (isSliding.value) tween(0) else tween(500, easing = LinearEasing),
        label = "slider animation"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Slider(
            modifier = modifier
                .weight(1f),
            value = currentPositionAnimated,
            onValueChange = {
                if ((localPosition - currentGap).absoluteValue > sliderGap) {
                    haptic.performHapticFeedback(
                        HapticFeedbackType.SegmentFrequentTick
                    )

                    currentGap = currentPositionAnimated
                }

                if (!isSliding.value) isSliding.value = true

                localPosition = it

                currentPosition.value = (it * viewModel.audioPlayer.currentTrackDuration.value.toFloat()).toLong()
            },
            onValueChangeFinished = {
                isSliding.value = false

                viewModel.audioPlayer.seek(currentPosition.value)
            },
            colors = SliderDefaults.colors(
                activeTrackColor = semiTransparentForeground * 1.5f,
                activeTickColor = semiTransparentForeground * 2f,
                inactiveTrackColor = fullyTransparentForeground,
                inactiveTickColor = semiTransparentForeground,
                disabledThumbColor = semiTransparentForeground,
                disabledActiveTrackColor = semiTransparentForeground,
                disabledActiveTickColor = semiTransparentForeground,
                disabledInactiveTickColor = semiTransparentForeground,
                disabledInactiveTrackColor = semiTransparentForeground
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(30.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(onBackgroundColorAnimated.value)
                )
            }
        )

        IconButton(
            onClick = {
                haptic.performHapticFeedback(
                    HapticFeedbackType.Confirm
                )
            }
        ) {
            Icon(
                imageVector = if (false)
                    Icons.Rounded.Favorite
                else
                    Icons.Rounded.FavoriteBorder,
                contentDescription = "play/pause icon",
                tint = onBackgroundColorAnimated.value,
                modifier = Modifier
                    .size(26.dp)
            )
        }
    }

}