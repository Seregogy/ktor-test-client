package com.example.ktor_test_client.control.card

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Spring.StiffnessLow
import androidx.compose.animation.core.spring
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class SwipeableCardData(
    val enableRotation: Boolean = true,

    val rotationAcceleration: Float = 1f,
    val horizontalOffsetAcceleration: Float = 1f,
    val verticalOffsetAcceleration: Float = 1f,

    val cardVerticalOffset: Dp = 25.dp,
    val swipeLimit: Dp = 180.dp,

    val scaleDivider: Float = 20f,
    val rotationDivider: Float = 50f,

    val offsetAnimationSpec: AnimationSpec<Offset> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = StiffnessLow
    ),

    val zIndexAnimationSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioHighBouncy,
        stiffness = StiffnessLow
    )
)

enum class SwipeDirection {
    SwipeLeft,
    SwipeRight
}