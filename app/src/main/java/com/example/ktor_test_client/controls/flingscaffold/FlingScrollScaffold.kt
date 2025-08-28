package com.example.ktor_test_client.controls.flingscaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.ktor_test_client.pages.TopAppContentBar
import com.example.ktor_test_client.state.ScrollState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FlingScrollScaffold(
    modifier: Modifier = Modifier,
    state: FlingScrollScaffoldState,
    backgroundContent: @Composable FlingScrollScaffoldState.() -> Unit,
    headingContent: @Composable FlingScrollScaffoldState.() -> Unit,
    mainContent: @Composable FlingScrollScaffoldState.() -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    state.run {
        screenHeight = LocalConfiguration.current.screenHeightDp.dp
        lazyListState = rememberLazyListState()

        noSnapLayout = object : SnapLayoutInfoProvider {
            override fun calculateSnapOffset(velocity: Float): Float {
                return velocity
            }
        }
        snapLayoutInfoProvider = SnapLayoutInfoProvider(lazyListState, SnapPosition.Start)

        val isFirstVisibleIndex by remember {
            var lastVisibleIndex = 0
            derivedStateOf {
                if (lastVisibleIndex >= 1 && lazyListState.firstVisibleItemIndex == 0) {
                    coroutineScope.launch {
                        delay(300)

                        if (lazyListState.firstVisibleItemIndex == 0)
                            lazyListState.animateScrollToItem(0)
                    }
                }

                lastVisibleIndex = lazyListState.firstVisibleItemIndex

                lazyListState.firstVisibleItemIndex == 0
            }
        }

        val flingBehavior = rememberSnapFlingBehavior(if (isFirstVisibleIndex) snapLayoutInfoProvider else noSnapLayout)

        var imageAlpha: Float by remember { mutableFloatStateOf(1f) }
        val backgroundContentHeight = 150.dp

        LaunchedEffect(Unit) {
            snapshotFlow { lazyListState.firstVisibleItemScrollOffset }.collect {
                onScrollStateChange(this@run)
            }
        }

        scrollState = remember {
            derivedStateOf {
                val isAvatarVisible = lazyListState.firstVisibleItemIndex == 0
                val scrollState = ScrollState(isAvatarVisible = isAvatarVisible)
                val totalHeight = screenHeight * TopAppContentBar.topPartWeight + TopAppContentBar.additionalHeight

                if (scrollState.isAvatarVisible) {
                    scrollState.currentOffset = with(density) { lazyListState.firstVisibleItemScrollOffset.toDp() }

                    scrollState.alpha = ((totalHeight - scrollState.currentOffset) / totalHeight).coerceIn(0f..1f)
                    scrollState.colorAlpha = ((totalHeight - scrollState.currentOffset) / 60.dp).coerceIn(0f..1f)

                    imageAlpha = (backgroundContentHeight - scrollState.currentOffset) / backgroundContentHeight
                }

                return@derivedStateOf scrollState
            }
        }

        Box(
            modifier = modifier
                .background(Color.Black)
                .fillMaxSize()
        ) {

            backgroundContent()

            /*Box(
                modifier = modifier
                    .alpha(scrollState.value.colorAlpha)
                    .fillMaxSize()
            ) {
                AlbumHeaderImage(
                    modifier = Modifier
                        .alpha(imageAlpha),
                    screenHeight = screenHeight,
                    bitmap = imageBitmap
                )
            }*/

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn(
                    state = lazyListState,
                    flingBehavior = flingBehavior,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .pointerInteropFilter {
                            return@pointerInteropFilter false
                        }
                ) {
                    item(0) {
                        headingContent()
                    }

                    items(1) {
                        mainContent()
                    }
                }
            }
        }
    }
}