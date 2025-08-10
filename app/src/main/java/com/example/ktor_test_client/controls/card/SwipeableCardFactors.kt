package com.example.ktor_test_client.controls.card

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

data class SwipeableCardsFactors(
    val rotationFactor: (
        offset: Float,
        state: SwipeableCardState,
        data: SwipeableCardData
    ) -> Float = { xOffset, _, data,  ->
        xOffset / data.rotationDivider
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
        density: Density
    ) -> IntOffset = { index, state, data, density ->
        with(density) {
            IntOffset(
                x = 0,
                y = (data.cardVerticalOffset.toPx() * index).roundToInt()
            )
        }
    },
    val zIndexFactor: (
        index: Int,
        state: SwipeableCardState,
        data: SwipeableCardData
    ) -> Int = { index, _, _ ->
        index
    }
)