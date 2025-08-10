package com.example.ktor_test_client.controls

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.palette.graphics.Palette
import com.example.ktor_test_client.helpers.times
import com.example.ktor_test_client.models.Artist
import com.example.ktor_test_client.viewmodels.ArtistCardViewModel
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState

data class ArtistCardState(
    val imageBitmap: MutableState<Bitmap?> = mutableStateOf(null),
    val palette: MutableState<Palette?> = mutableStateOf(null)
)

@Composable
fun ArtistCard(
    artist: Artist,
    artistCardState: MutableState<ArtistCardState>,
    viewModel: ArtistCardViewModel
) {
    val hazeState = rememberHazeState(blurEnabled = true)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchImageByUrl(context, artist.imagesUrl.first().first)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.medium)
    ) {
        artistCardState.value.imageBitmap.value?.let {
            Image(
                bitmap = it.asImageBitmap(),
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(hazeState),
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.7f)
                .align(Alignment.BottomCenter)
                .then(
                    artistCardState.value.palette.value?.let {
                        Modifier
                            .background(
                                brush = Brush.verticalGradient(
                                    0f to Color.Transparent,
                                    1f to Color(it.dominantSwatch!!.rgb)
                                )
                            )
                    } ?: Modifier
                )
        ) {
            Column(
                Modifier
                    .padding(20.dp)
                    .align(Alignment.BottomStart),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = artist.name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.W700
                )

                Text(
                    text = artist.about,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.W300,
                    maxLines = 2,
                    lineHeight = 10.sp
                )
            }
        }
    }
}