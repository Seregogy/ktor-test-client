package com.example.ktor_test_client.controls

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.ktor_test_client.controls.card.SwipeDirection
import com.example.ktor_test_client.controls.card.SwipeableCard
import com.example.ktor_test_client.controls.card.SwipeableCardData

@Composable
fun card(color: Color) : SwipeableCard {
    return SwipeableCard(
        cardData = SwipeableCardData(
            cardVerticalOffset = 30.dp
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(.85f)
                .aspectRatio(.7f),
            colors = CardDefaults.cardColors(
                containerColor = color
            )
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp),
                text = "Card ${cardState.value.initialIndex}",
                fontWeight = FontWeight.W700,
                fontSize = 32.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ElementsStackTest() {
    SwipeableCardStack(
        Modifier,
        mutableListOf(
            card(MaterialTheme.colorScheme.primary),
            card(MaterialTheme.colorScheme.secondary),
            card(MaterialTheme.colorScheme.tertiary),
            card(MaterialTheme.colorScheme.tertiaryContainer),
            card(MaterialTheme.colorScheme.primary),
            card(MaterialTheme.colorScheme.secondary),
            card(MaterialTheme.colorScheme.tertiary),
            card(MaterialTheme.colorScheme.tertiaryContainer),
            card(MaterialTheme.colorScheme.primary),
            card(MaterialTheme.colorScheme.secondary),
            card(MaterialTheme.colorScheme.tertiary),
            card(MaterialTheme.colorScheme.tertiaryContainer),
            card(MaterialTheme.colorScheme.primary),
            card(MaterialTheme.colorScheme.secondary),
            card(MaterialTheme.colorScheme.tertiary),
            card(MaterialTheme.colorScheme.tertiaryContainer),
        ),
        3,
        onSwipe = { direction, card ->

        }
    )
}

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

    LaunchedEffect(cardsState) {
        cards.forEach {
            cardsState.add(it)
        }

        cardsState.forEachIndexed { index, card ->
            card.cardState.value.initialIndex = index
        }

        cardsState.forEach { card ->
            card.cardState.value.onSwipe = { swipeDirection ->
                onSwipe(swipeDirection, card)

                cardsState.removeAt(card.cardState.value.currentIndex.value)
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

                if (it.cardState.value.selected.value)
                    onCardSelected(it)

                Box(
                    modifier = Modifier
                        .alpha(if (index == maxVisibleSize) 0f else 1f)
                        .zIndex(-it.cardsFactors.zIndexFactor(index, it.cardState.value, it.cardData).toFloat())
                ) {
                    it.DrawCard()
                }
            }
        }
    }
}