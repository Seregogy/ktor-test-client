package com.example.ktor_test_client.controls.flingscaffold

import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ktor_test_client.state.ScrollState

class FlingScrollScaffoldState(
    val onScrollStateChange: FlingScrollScaffoldState.() -> Unit = { }
) {
    var screenHeight: Dp = 0.dp

    lateinit var density: Density
    lateinit var lazyListState: LazyListState
    lateinit var noSnapLayout: SnapLayoutInfoProvider
    lateinit var snapLayoutInfoProvider: SnapLayoutInfoProvider

    var scrollState: MutableState<ScrollState> = mutableStateOf(ScrollState())
}

@Composable
fun rememberFlingScaffoldState(): FlingScrollScaffoldState {
    return remember {
        mutableStateOf(FlingScrollScaffoldState())
    }.value
}

@Composable
fun rememberFlingScaffoldState(
    onScrollStateChange: FlingScrollScaffoldState.() -> Unit
): FlingScrollScaffoldState {
    return remember {
        mutableStateOf(FlingScrollScaffoldState(onScrollStateChange))
    }.value
}