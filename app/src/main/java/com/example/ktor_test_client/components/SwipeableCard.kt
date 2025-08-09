package com.example.ktor_test_client.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

@Composable
fun SwipeableCard(
    modifier: Modifier = Modifier,
    cardData: SwipeableCardData = SwipeableCardData(),
    cardState: SwipeableCardState = SwipeableCardState(),
    cardsFactors: SwipeableCardsFactors = SwipeableCardsFactors(LocalDensity.current),
    content: @Composable () -> Unit = { },
) {
    Box(
        modifier = modifier
            .offset {
                IntOffset(
                    x = animatedOffsetState.value.x.roundToInt(),
                    y = animatedOffsetState.value.y.roundToInt() + ((20).dp.toPx() * i).roundToInt()
                )
            }
            .scale(animatedScaleState.value)
            .zIndex(-i.toFloat())
            .pointerInput(Unit) {
                if (i == itemsMap.size - 1) return@pointerInput

                detectDragGestures(
                    onDragStart = {
                        data.isDragging.value = true
                    },
                    onDragEnd = {
                        data.xOffset.value = 0f
                        data.yOffset.value = 0f

                        data.isDragging.value = false
                    },
                    onDragCancel = {
                        data.xOffset.value = 0f
                        data.yOffset.value = 0f

                        data.isDragging.value = false
                    }
                ) { change, dragAmount ->
                    change.consume()

                    data.xOffset.value += dragAmount.x * 1.2f
                    data.yOffset.value += dragAmount.y * .5f

                    if (data.xOffset.value.absoluteValue > maxOffsetDeviation) {
                        data.xOffset.value = 0f
                        data.yOffset.value = 0f

                        data.isDragging.value = false
                        itemsMap.removeFirst()

                        return@detectDragGestures
                    }
                }
            }
            .graphicsLayer {
                if (i == itemsMap.size - 1) return@graphicsLayer

                rotationZ = animatedOffsetState.value.x / 50
            }
    ) {
        content()
    }
}

data class SwipeableCardData(
    val enableRotation: Boolean = true,

    val rotationAcceleration: Float = 1f,
    val horizontalOffsetAcceleration: Float = 1f,
    val verticalOffsetAcceleration: Float = 1f,

    val cardVerticalOffset: Dp = 20.dp,
    val swipeLimit: Dp = 150.dp,

    val scaleDivider: Float = 20f,
    val rotationDivider: Float = 50f
)

data class SwipeableCardState(
    var isDragging: MutableState<Boolean> = mutableStateOf(false),

    var xOffset: MutableState<Float> = mutableFloatStateOf(0f),
    var yOffset: MutableState<Float> = mutableFloatStateOf(0f),
    var scale: MutableState<Float> = mutableFloatStateOf(1f)
)

data class SwipeableCardsFactors(
    val density: Density,
    val rotationFactor: (
        offset: Offset,
        state: SwipeableCardState,
        data: SwipeableCardData
    ) -> Float = { offset, _, data,  ->
        offset.x / data.rotationDivider
    },
    val scaleFactor: (
        index: Int,
        state: SwipeableCardState,
        data: SwipeableCardData,
    ) -> Float = { index, _, data ->
        1 - index.toFloat() / data.scaleDivider
    },
    val cardOffsetCalculation: (
        index: Int,
        state: SwipeableCardState,
        data: SwipeableCardData,
    ) -> Offset = { index, state, data ->
        Offset(
            x = state.xOffset.value,
            y = state.yOffset.value + with (density) { data.cardVerticalOffset.toPx() * index }
        )
    },
)