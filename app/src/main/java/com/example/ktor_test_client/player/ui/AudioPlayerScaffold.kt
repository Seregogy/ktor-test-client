package com.example.ktor_test_client.player.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.controls.coloredscaffold.ColoredScaffold
import com.example.ktor_test_client.controls.coloredscaffold.ColoredScaffoldState
import com.example.ktor_test_client.controls.coloredscaffold.rememberColoredScaffoldState
import com.example.ktor_test_client.data.providers.PlaylistProviderImpl
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AudioPlayerScaffold(
    viewModel: AudioPlayerViewModel,
    innerPadding: PaddingValues,
    navController: NavHostController,
    hazeState: HazeState,
    content: @Composable (sheetPeekHeight: Dp, innerPadding: PaddingValues) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberBottomSheetScaffoldState()

    val density = LocalDensity.current


    val bottomSectionHeight = remember { mutableStateOf(0.dp) }
    val bottomSectionHeightPx by remember {
        derivedStateOf {
            with(density) {
                bottomSectionHeight.value.roundToPx()
            }
        }
    }


    var allInit by remember { mutableStateOf(false) }

    val apiService = koinInject<MusicApiService>()
    LaunchedEffect(Unit) {
        viewModel.audioPlayer.setPlaylist(
            PlaylistProviderImpl(
                baseTracks = listOf(),
                musicApiService = apiService
            )
        )
    }

    LaunchedEffect(Unit) {
        delay(300)
        allInit = true
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.audioPlayer.release()
        }
    }

    if (bottomSheetState.bottomSheetState.currentValue == SheetValue.Expanded) {
        BackHandler {
            coroutineScope.launch {
                bottomSheetState.bottomSheetState.partialExpand()
            }
        }
    }

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

    val alphaStateThreshold = with(density) { bottomSectionHeight.value.roundToPx() }
    val targetMiniPlayerAlpha by remember {
        derivedStateOf {
            yCurrentOffset.value / (screenHeight - bottomSectionHeightPx)
        }
    }

    val blurTargetMiniPlayerAlpha by remember {
        derivedStateOf {
            1f - ((yCurrentOffset.value) / alphaStateThreshold).coerceIn(0f..1f)
        }
    }

    val sheetPeekHeight = bottomSectionHeight.value + innerPadding.calculateBottomPadding()
    BottomSheetScaffold(
        sheetPeekHeight = sheetPeekHeight,
        scaffoldState = bottomSheetState,
        sheetDragHandle = { },
        sheetShape = RoundedCornerShape(0.dp),
        sheetContainerColor = Color.Transparent,
        sheetContent = {
            BottomSheetAudioPlayer(
                bottomSectionHeight = bottomSectionHeight,
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
    bottomSectionHeight: MutableState<Dp>,
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
    val density = LocalDensity.current

    val coloredScaffoldState = rememberColoredScaffoldState {
        viewModel.palette.collectAsState()
    }

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
            FullAudioPlayer(viewModel, modifier, coloredScaffoldState, onCollapseRequest, onAlbumClicked, onArtistClicked)
        }

        ColoredScaffold(
            state = rememberColoredScaffoldState {
                viewModel.palette.collectAsState()
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = innerPadding.calculateBottomPadding())
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
                    .onSizeChanged {
                        bottomSectionHeight.value = with(density) {
                            it.height.toDp()
                        }
                    }
                    .background(additionalHorizontalGradientBrush.value)
            ) {
                MiniAudioPlayer(
                    viewModel = viewModel,
                    onExpandRequest = onExpandRequest
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                        .padding(bottom = 10.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(Color.Black.copy(.7f))
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(
                        Icons.Rounded.Home,
                        Icons.Rounded.Search,
                        Icons.Rounded.AutoAwesome,
                        Icons.Rounded.Person
                    ).forEach {
                        IconButton(
                            onClick = { }
                        ) {
                            Icon(
                                imageVector = it,
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }
    }
}