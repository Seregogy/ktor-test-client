package com.example.ktor_test_client.controls.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AudioPlayerScaffold(
    innerPadding: PaddingValues,
    navController: NavHostController,
    content: @Composable (sheetPeekHeight: Dp, innerPadding: PaddingValues) -> Unit
) {
    val miniPlayerHeight = 100.dp
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

    val sheetPeekHeight = miniPlayerHeight + innerPadding.calculateBottomPadding()
    BottomSheetScaffold(
        sheetPeekHeight = sheetPeekHeight,
        scaffoldState = bottomSheetState,
        sheetDragHandle = { },
        sheetShape = RoundedCornerShape(0.dp),
        sheetContent = {
            BottomSheetAudioPlayer(
                yCurrentOffset,
                miniPlayerHeight,
                innerPadding,
                viewModel<AudioPlayerViewModel>(),
                Modifier.padding(top = innerPadding.calculateTopPadding()),
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
                        navController.navigate("AlbumPage/?id=$albumId")
                        bottomSheetState.bottomSheetState.partialExpand()
                    }
                },
                onArtistClicked = { artistId ->
                    coroutineScope.launch {
                        navController.navigate("ArtistPage/?id=$artistId")
                        bottomSheetState.bottomSheetState.partialExpand()
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(.5f)
    ) { paddingValues ->
        content(sheetPeekHeight, paddingValues)
    }
}

@Composable
fun BottomSheetAudioPlayer(
    yCurrentOffset: State<Float>,
    miniPlayerHeight: Dp,
    innerPadding: PaddingValues,
    viewModel: AudioPlayerViewModel,
    modifier: Modifier,
    onExpandRequest: () -> Unit = { },
    onCollapseRequest: () -> Unit = { },
    onAlbumClicked: (albumId: String) -> Unit,
    onArtistClicked: (artistId: String) -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val targetMiniPlayerAlpha by remember {
        derivedStateOf {
            yCurrentOffset.value / screenHeight
        }
    }

    Box {
        Box(
            modifier = Modifier.alpha(1f - targetMiniPlayerAlpha)
        ) {
            FullAudioPlayer(viewModel, modifier, onCollapseRequest, onAlbumClicked, onArtistClicked)
        }

        Box(
            modifier = Modifier
                .alpha(targetMiniPlayerAlpha)
                .align(Alignment.TopCenter)
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