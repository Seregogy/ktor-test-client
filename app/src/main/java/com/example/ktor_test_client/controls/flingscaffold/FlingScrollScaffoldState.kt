package com.example.ktor_test_client.controls.flingscaffold

import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ktor_test_client.state.ScrollState

class FlingScrollScaffoldState(
    val onScrollStateChange: FlingScrollScaffoldState.() -> Unit = { }
) {
    var screenHeight: Dp = 0.dp

    lateinit var lazyListState: LazyListState
    lateinit var noSnapLayout: SnapLayoutInfoProvider
    lateinit var snapLayoutInfoProvider: SnapLayoutInfoProvider

    lateinit var scrollState: State<ScrollState>
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