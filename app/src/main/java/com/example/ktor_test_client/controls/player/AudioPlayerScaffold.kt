package com.example.ktor_test_client.controls.player

import android.util.Log
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInElastic
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AudioPlayerScaffold(
    innerPadding: PaddingValues,
    navController: NavHostController,
    hazeState: HazeState,
    content: @Composable (sheetPeekHeight: Dp, innerPadding: PaddingValues) -> Unit
) {
    val density = LocalDensity.current
    val viewModel: AudioPlayerViewModel = koinViewModel()

    val miniPlayerHeight = 100.dp
    val miniPlayerHeightPx = with(density) { 100.dp.roundToPx() }
    var allInit by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        allInit = true
    }

    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberBottomSheetScaffoldState()

    val yCurrentOffset = remember {
        derivedStateOf {
            return@derivedStateOf if (allInit) {
                bottomSheetState.bottomSheetState.requireOffset()
            } else {
                0f
            }
        }
    }

    val screenHeight = with(density) {
        LocalConfiguration.current.screenHeightDp.dp.roundToPx()
    }

    val alphaStateThreshold = with(density) { miniPlayerHeight.roundToPx() }
    val targetMiniPlayerAlpha by remember {
        derivedStateOf {
            yCurrentOffset.value / (screenHeight - miniPlayerHeightPx)
        }
    }

    val blurTargetMiniPlayerAlpha by remember {
        derivedStateOf {
            val a = 1f - ((yCurrentOffset.value) / alphaStateThreshold).coerceIn(0f..1f)
            Log.d("AAA", a.toString())

            a
        }
    }

    val sheetPeekHeight = miniPlayerHeight + innerPadding.calculateBottomPadding()
    BottomSheetScaffold(
        sheetPeekHeight = sheetPeekHeight,
        scaffoldState = bottomSheetState,
        sheetDragHandle = { },
        sheetShape = RoundedCornerShape(0.dp),
        sheetContainerColor = Color.Transparent,
        sheetContent = {
            BottomSheetAudioPlayer(
                miniPlayerHeight = miniPlayerHeight,
                innerPadding = innerPadding,
                viewModel = viewModel,
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                hazeState = hazeState,
                targetMiniPlayerAlpha = targetMiniPlayerAlpha,
                blurTargetMiniPlayerAlpha = blurTargetMiniPlayerAlpha,
                onExpandRequest = {
                    coroutineScope.launch {
                        bottomSheetState.bottomSheetState.expand()
                    }
                },
                onCollapseRequest = {
                    coroutineScope.launch {
                        bottomSheetState.bottomSheetState.partialExpand()
                    }
                },
                onAlbumClicked = { albumId ->
                    coroutineScope.launch {
                        navController.navigate("AlbumPage?id=$albumId")
                        bottomSheetState.bottomSheetState.partialExpand()
                    }
                },
                onArtistClicked = { artistId ->
                    coroutineScope.launch {
                        navController.navigate("ArtistPage?id=$artistId")
                        bottomSheetState.bottomSheetState.partialExpand()
                    }
                }
            )
        }
    ) { paddingValues ->
        content(sheetPeekHeight, paddingValues)
    }
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun BottomSheetAudioPlayer(
    miniPlayerHeight: Dp,
    innerPadding: PaddingValues,
    viewModel: AudioPlayerViewModel,
    modifier: Modifier,
    targetMiniPlayerAlpha: Float,
    blurTargetMiniPlayerAlpha: Float,
    hazeState: HazeState,
    onExpandRequest: () -> Unit = { },
    onCollapseRequest: () -> Unit = { },
    onAlbumClicked: (albumId: String) -> Unit,
    onArtistClicked: (artistId: String) -> Unit
) {
    Box {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .alpha(blurTargetMiniPlayerAlpha)
        )

        Box(
            modifier = Modifier
                .alpha(1f - targetMiniPlayerAlpha)

        ) {
            FullAudioPlayer(viewModel, modifier, onCollapseRequest, onAlbumClicked, onArtistClicked)
        }

        Box(
            modifier = Modifier
                .alpha(targetMiniPlayerAlpha)
                .align(Alignment.TopCenter)
                .then(
                    if (targetMiniPlayerAlpha > 0.99f) {
                        Modifier
                            .hazeEffect(hazeState, HazeMaterials.thin(Color.Black))
                    } else {
                        Modifier
                    }
                )
                .then(
                    if (targetMiniPlayerAlpha < .8f)
                        Modifier.pointerInteropFilter { return@pointerInteropFilter false }
                    else
                        Modifier
                )
        ) {
            MiniAudioPlayer(viewModel, miniPlayerHeight, innerPadding, onExpandRequest)
        }
    }
}