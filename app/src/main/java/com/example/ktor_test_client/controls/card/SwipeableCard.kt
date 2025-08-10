package com.example.ktor_test_client.controls.card

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

class SwipeableCard(
    val modifier: Modifier = Modifier,
    val cardData: SwipeableCardData = SwipeableCardData(),
    val cardState: MutableState<SwipeableCardState> = mutableStateOf(SwipeableCardState()),
    val cardsFactors: SwipeableCardsFactors = SwipeableCardsFactors(),
    val content: @Composable SwipeableCard.() -> Unit = { },
) {
    @Composable
    fun DrawCard() {
        val density = LocalDensity.current

        val animatedOffset by animateOffsetAsState(
            targetValue = Offset(cardState.value.xOffset.value, cardState.value.yOffset.value),
            animationSpec = if (cardState.value.isDragging.value) tween(0) else cardData.offsetAnimationSpec,
            label = "card offset animation"
        )

        val animatedScale by animateFloatAsState(
            targetValue = cardsFactors.scaleFactor(cardState.value.currentIndex.value, cardState.value, cardData),
            animationSpec = cardData.zIndexAnimationSpec,
            label = "card scale animation"
        )

        val animatedAlpha by animateFloatAsState(
            targetValue = if (cardState.value.swiped.value) 0f else 1f,
            animationSpec = tween(200),
            label = "alpha animation"
        )

        Box(
            modifier = modifier
                .alpha(animatedAlpha)
                .offset {
                    IntOffset(
                        x = animatedOffset.x.roundToInt(),
                        y = animatedOffset.y.roundToInt()
                    ) + cardsFactors.cardOffsetCalculation(
                        cardState.value.currentIndex.value,
                        cardState.value,
                        cardData,
                        density
                    )
                }
                .scale(animatedScale)
                .zIndex(-cardState.value.currentIndex.value.toFloat())
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            cardState.value.isDragging.value = true
                        },
                        onDragEnd = {
                            if (cardState.value.xOffset.value > cardData.swipeLimit.toPx()) {
                                cardState.value.onSwipe(SwipeDirection.SwipeRight)

                                cardState.value.swiped.value = true
                            } else if (cardState.value.xOffset.value < -cardData.swipeLimit.toPx()) {
                                cardState.value.onSwipe(SwipeDirection.SwipeLeft)

                                cardState.value.swiped.value = true
                            }

                            swipeEnd(cardState)
                        },
                        onDragCancel = {
                            swipeEnd(cardState)
                        }
                    ) { change, dragAmount ->
                        if (cardState.value.selected.value.not()) return@detectDragGestures

                        change.consume()

                        cardState.value.xOffset.value += dragAmount.x * cardData.horizontalOffsetAcceleration
                        cardState.value.yOffset.value += dragAmount.y * cardData.verticalOffsetAcceleration
                    }
                }
                .graphicsLayer {
                    if (cardState.value.selected.value.not()) return@graphicsLayer

                    rotationZ = cardsFactors.rotationFactor(animatedOffset.x, cardState.value, cardData)
                }
        ) {
            content()
        }
    }

    private fun swipeEnd(cardState: MutableState<SwipeableCardState>) {
        cardState.value.xOffset.value = 0f
        cardState.value.yOffset.value = 0f

        cardState.value.isDragging.value = false
    }
}

