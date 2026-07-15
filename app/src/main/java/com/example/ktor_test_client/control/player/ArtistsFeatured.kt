package com.example.ktor_test_client.control.player

import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ktor_test_client.api.dtos.BaseArtist
import com.example.ktor_test_client.viewmodel.ImagePaletteExtractor

@Composable
fun ArtistsFeatured(
    modifier: Modifier,
    cardModifier: Modifier,
    horizontalPaddings: Dp = 20.dp,
    artists: List<BaseArtist>,
    onClick: (artist: BaseArtist) -> Unit
) {
    val lazyListState = rememberLazyListState()

    val snapPosition = object : SnapPosition {
        override fun position(
            layoutSize: Int,
            itemSize: Int,
            beforeContentPadding: Int,
            afterContentPadding: Int,
            itemIndex: Int,
            itemCount: Int,
        ): Int {
            return beforeContentPadding / 2
        }
    }
    val snapLayoutInfoProvider = SnapLayoutInfoProvider(lazyListState, snapPosition)

    val spaceBetweenCards = 5.dp
    LazyRow(
        modifier = modifier,
        state = lazyListState,
        flingBehavior = rememberSnapFlingBehavior(snapLayoutInfoProvider),
        horizontalArrangement = Arrangement.spacedBy(spaceBetweenCards)
    ) {
        itemsIndexed(artists) { index, artist ->
            if (index == 0) {
                Spacer(Modifier.width(horizontalPaddings))
            }

            ArtistCard(cardModifier, artist, ImagePaletteExtractor()) {
                onClick(it)
            }

            if (index == (artists.size - 1)) {
                Spacer(Modifier.width(horizontalPaddings))
            }
        }
    }
}