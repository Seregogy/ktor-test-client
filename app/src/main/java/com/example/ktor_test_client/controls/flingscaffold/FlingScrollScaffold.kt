package com.example.ktor_test_client.controls.flingscaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SuggestionChip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
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

    state.run {
        density = LocalDensity.current
        screenHeight = LocalConfiguration.current.screenHeightDp.dp
        lazyListState = rememberLazyListState()

        noSnapLayout = object : SnapLayoutInfoProvider {
            override fun calculateSnapOffset(velocity: Float): Float {
                return velocity
            }
        }

        val snapPosition = object : SnapPosition {
            override fun position(
                layoutSize: Int,
                itemSize: Int,
                beforeContentPadding: Int,
                afterContentPadding: Int,
                itemIndex: Int,
                itemCount: Int,
            ): Int {
                return beforeContentPadding + with(density) { yFlingOffset.roundToPx() }
            }
        }

        snapLayoutInfoProvider = SnapLayoutInfoProvider(lazyListState, snapPosition)

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

        LaunchedEffect(Unit) {
            snapshotFlow { lazyListState.firstVisibleItemScrollOffset }.collect {
                onScrollStateChange(this@run)

                with(density) {
                    isHeaderSwiped.value = (totalHeight.value != 0.dp && (
                        lazyListState.firstVisibleItemIndex != 0 ||
                        totalHeight.value.roundToPx() - lazyListState.firstVisibleItemScrollOffset <= (yFlingOffset.roundToPx() + 5)
                    )).not()
                }
            }
        }

        Box(
            modifier = modifier
                .background(Color.Black)
                .fillMaxSize()
        ) {
            backgroundContent()

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