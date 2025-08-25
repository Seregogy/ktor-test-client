package com.example.ktor_test_client.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ktor_test_client.R
import com.example.ktor_test_client.api.dtos.Artist
import com.example.ktor_test_client.api.dtos.BaseArtist
import com.example.ktor_test_client.controls.card.SwipeableCard
import com.example.ktor_test_client.controls.card.SwipeableCardData
import com.example.ktor_test_client.viewmodels.ArtistCardViewModel

@Composable
fun artistSwipeableCard(
    artist: BaseArtist,
    artistCardState: MutableState<ArtistCardState>,
    onClick: (artist: BaseArtist) -> Unit
) : SwipeableCard {
    val threshold = 100.dp

    val viewModel = ArtistCardViewModel()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchImageByUrl(context, artist.imageUrl ?: "")
    }

    LaunchedEffect(Unit) {
        viewModel.bitmap.collect {
            artistCardState.value.imageBitmap.value = it
        }
    }

    LaunchedEffect(Unit) {
        viewModel.palette.collect {
            if (it?.swatches != null) {
                artistCardState.value.palette.value = it
            }
        }
    }

    return SwipeableCard(
        cardData = SwipeableCardData(
            cardVerticalOffset = 30.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(.8f)
                .aspectRatio(.7f)
                .clip(MaterialTheme.shapes.medium)
                .clickable {
                    onClick(artist)
                }
        ) {
            ArtistCard(
                artist,
                artistCardState,
                ArtistCardViewModel()
            )

            if (cardState.value.selected.value) {
                RightSwipePreview(
                    modifier = Modifier
                        .alpha(
                            ((animatedOffset.value.x.dp - threshold) / cardData.swipeLimit.value.dp) / 2
                        )
                )

                LeftSwipePreview(
                    modifier = Modifier
                        .alpha(
                            ((-(animatedOffset.value.x.dp + threshold)) / cardData.swipeLimit.value.dp) / 2
                        )
                )
            }
        }
    }
}

@Composable
fun RightSwipePreview(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    center = Offset(0f,0f),
                    colors = listOf(
                        Color(0xFFA6F527),
                        Color.Transparent
                    ),
                    radius = 1500f
                )
            )
            .padding(50.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.heart_icon),
            contentDescription = "",
            modifier = Modifier
                .size(50.dp)

        )
    }
}

@Composable
fun LeftSwipePreview(
    modifier: Modifier = Modifier
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    center = Offset(with(density) { screenWidth.dp.toPx().div(2) }, 0f),
                    colors = listOf(
                        Color(0xFFBC4749),
                        Color.Transparent
                    ),
                    radius = 1500f
                )
            )
            .padding(50.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.heart_icon_minus),
            contentDescription = "",
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.TopEnd)
        )
    }
}