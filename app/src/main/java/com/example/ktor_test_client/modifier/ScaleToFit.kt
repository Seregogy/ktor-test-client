package com.example.ktor_test_client.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntSize

fun Modifier.scaleToFill(
    screenSize: IntSize,
) = graphicsLayer {
    val scale = (Math.max(screenSize.height, screenSize.width) * Math.max(size.height, size.width)) /
                (Math.min(screenSize.height, screenSize.width) * Math.min(size.height, size.width))

    scaleX = scale
    scaleY = scale
}