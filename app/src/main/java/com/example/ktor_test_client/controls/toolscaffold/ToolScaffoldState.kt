package com.example.ktor_test_client.controls.toolscaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

class ToolScaffoldState<ToolAction, ContextAction>(
    var onBackRequest: () -> Unit,
    var onSearchRequest: () -> Unit,
    var toolAction: @Composable ToolAction.() -> Unit,
    var contextAction: @Composable ContextAction.() -> Unit,

    var primaryColor: State<Color> = mutableStateOf(Color.Black),
    var onPrimaryColor: State<Color> = mutableStateOf(Color.White),

    var toolBarTitle: String? = null
)

@Composable
fun <T, C> rememberToolScaffoldState(
    onBackRequest: () -> Unit = { },
    onSearchRequest: () -> Unit = { },
    toolAction: @Composable T.() -> Unit = { },
    contextAction: @Composable C.() -> Unit = { },
    primaryColor: State<Color> = mutableStateOf(Color.Black),
    onPrimaryColor: State<Color> = mutableStateOf(Color.White),
    toolBarTitle: String? = null
): ToolScaffoldState<T, C> {
    return remember {
        ToolScaffoldState(onBackRequest, onSearchRequest, toolAction, contextAction, primaryColor, onPrimaryColor, toolBarTitle)
    }
}

@Composable
fun rememberToolScaffoldState(): ToolScaffoldState<Nothing, Nothing> {
    return remember {
        ToolScaffoldState({  }, { }, { }, { }, mutableStateOf(Color.Black), mutableStateOf(Color.White))
    }
}