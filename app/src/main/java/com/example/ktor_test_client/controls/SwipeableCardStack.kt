package com.example.ktor_test_client.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.zIndex
import com.example.ktor_test_client.controls.card.SwipeDirection
import com.example.ktor_test_client.controls.card.SwipeableCard

@Composable
fun SwipeableCardStack(
    modifier: Modifier,
    cards: MutableList<SwipeableCard>,
    maxVisibleSize: Int,
    onCardSelected: (SwipeableCard) -> Unit = { },
    onCardsEnded: () -> Unit = { },
    onSwipe: (direction: SwipeDirection, swipeableCard: SwipeableCard) -> Unit = { _, _ -> }
) {
    val cardsState = remember { mutableStateListOf<SwipeableCard>() }

    var xGlobalOffset by remember { mutableFloatStateOf(0f) }
    var yGlobalOffset by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(cardsState) {
        cards.forEach {
            cardsState.add(it)
        }

        cardsState.forEachIndexed { index, card ->
            card.cardState.value.initialIndex = index
        }

        cardsState.forEach { card ->
            card.cardState.value.onSwipe = { swipeDirection ->
                xGlobalOffset = 0f
                yGlobalOffset = 0f

                onSwipe(swipeDirection, card)

                cardsState.removeAt(card.cardState.value.currentIndex.value)

                cardsState.forEach {
                    it.cardState.value.xOffset.value = 0f
                    it.cardState.value.yOffset.value = 0f
                }
            }
            card.cardState.value.onSwiping = {
                xGlobalOffset = card.cardState.value.xOffset.value
                yGlobalOffset = card.cardState.value.yOffset.value
            }
            card.cardState.value.onSwipeEnd = {
                xGlobalOffset = 0f
                yGlobalOffset = 0f
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (cardsState.isEmpty())
            onCardsEnded()

        cardsState.take(maxVisibleSize + 1).forEachIndexed { index, it ->
            key(it.hashCode()) {
                it.cardState.value.currentIndex.value = index
                it.cardState.value.selected.value = index == 0

                if (it.cardState.value.selected.value) {
                    onCardSelected(it)
                } else {
                    it.cardState.value.xOffset.value = xGlobalOffset / (3 * index)
                    it.cardState.value.yOffset.value = yGlobalOffset / (3 * index)
                }

                Box(
                    modifier = Modifier
                        .alpha(if (index == maxVisibleSize) 0f else 1f)
                        .zIndex(
                            -it.cardsFactors
                                .zIndexFactor(index, it.cardState.value, it.cardData)
                                .toFloat()
                        )
                ) {
                    it.DrawCard()
                }
            }
        }
    }
}