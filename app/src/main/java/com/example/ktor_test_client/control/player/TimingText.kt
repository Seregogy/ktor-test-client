package com.example.ktor_test_client.control.player

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktor_test_client.control.coloredscaffold.ColoredScaffoldState
import com.example.ktor_test_client.helper.formatMinuteTimer
import kotlin.math.roundToInt

enum class TimingTextState {
    CurrentTime,
    RemainingTime
}

@Composable
fun ColoredScaffoldState.TimingText(
    secondaryColorWithLoadingState: Color,
    currentPosition: MutableState<Long>,
    currentTrackDuration: Long,
    isSliding: MutableState<Boolean>
) {
    val haptic = LocalHapticFeedback.current

    val currentPositionAnimated = animateFloatAsState(
        targetValue = (currentPosition.value / currentTrackDuration.toFloat()).coerceIn(0f..currentTrackDuration.toFloat()),
        animationSpec = if (isSliding.value) tween(0) else tween(300, easing = LinearEasing),
        label = "slider animation"
    )

    val fullyTransparentForeground by remember {
        derivedStateOf {
            onBackgroundColorAnimated.value.copy(.15f)
        }
    }

    var currentTextState by remember { mutableStateOf(TimingTextState.CurrentTime) }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(fullyTransparentForeground)
            .clickable {
                haptic.performHapticFeedback(
                    HapticFeedbackType.ContextClick
                )

                currentTextState = if (currentTextState == TimingTextState.CurrentTime) {
                    TimingTextState.RemainingTime
                } else {
                    TimingTextState.CurrentTime
                }
            }
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontSize = 13.sp
                    )
                ) {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.W800
                        )
                    ) {
                        append(
                            formatMinuteTimer(
                                if (currentTextState == TimingTextState.CurrentTime) {
                                    (currentPositionAnimated.value * currentTrackDuration.toFloat() / 1000)
                                } else {
                                    -(currentTrackDuration - currentPositionAnimated.value * currentTrackDuration.toFloat()) / 1000
                                }.roundToInt().coerceIn(-currentTrackDuration.toInt()..currentTrackDuration.toInt())
                            )
                        )
                    }

                    append(" / ")

                    append(formatMinuteTimer((currentTrackDuration / 1000).toInt()))
                }
            },
            textAlign = TextAlign.Center,
            color = secondaryColorWithLoadingState
        )
    }
}