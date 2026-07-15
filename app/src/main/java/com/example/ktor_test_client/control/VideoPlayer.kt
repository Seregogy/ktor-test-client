package com.example.ktor_test_client.control

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.SurfaceType
import com.example.ktor_test_client.modifier.scaleToFill
import com.example.ktor_test_client.viewmodel.VideoPlayerViewModel

@Composable
fun VideoPlayer(
    viewModel: VideoPlayerViewModel
) {
    val player by remember { mutableStateOf(viewModel.player) }
    val aspectRatio by viewModel.aspectRatio.collectAsState()

    val screenSize = with(LocalDensity.current) {
        with(LocalConfiguration.current) {
            IntSize(
                width = screenWidthDp.dp.roundToPx(),
                height = screenHeightDp.dp.roundToPx()
            )
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PlayerSurface(
            player = player,
            modifier = Modifier
                .alpha(1f)
                .scaleToFill(screenSize)
                .aspectRatio(aspectRatio),
            surfaceType = SURFACE_TYPE_TEXTURE_VIEW
        )
    }
}