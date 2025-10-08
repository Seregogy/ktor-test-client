package com.example.ktor_test_client.controls.toolscaffold

import androidx.compose.animation.core.EaseIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ktor_test_client.controls.MarqueeText
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.coroutines.launch

private val toolBarHeight = 50.dp

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun ToolScaffold(
    modifier: Modifier = Modifier,
    hazeState: HazeState?,
    state: ToolScaffoldState,
    content: @Composable (innerPadding: PaddingValues) -> Unit = { }
) {
    val expanded = remember { mutableStateOf(false) }
    var contentState: @Composable () -> Unit by remember { mutableStateOf({ }) }

    state.run {
        content(PaddingValues(top = toolBarHeight))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .then(
                    if (toolBarTitle.value != null) {
                        if (hazeState != null) {
                            Modifier
                                .hazeEffect(
                                    state = hazeState,
                                    style = HazeMaterials.ultraThin(Color.Black)
                                ) {
                                    progressive = HazeProgressive.verticalGradient(
                                        startIntensity = 1f,
                                        endIntensity = 0f,
                                        easing = EaseIn
                                    )
                                }
                        } else {
                            Modifier.background(Color.Black.copy(.93f))
                        }
                    } else {
                        Modifier
                    }
                )
                .fillMaxWidth()
                .then(modifier)
                .padding(horizontal = 15.dp)
                .height(toolBarHeight)
        ) {
            onLaunchContextAction = {
                contentState = it
                expanded.value = true
            }

            ContextMenu(
                expanded = expanded,
                content = contentState
            )

            Box(
                modifier = Modifier
                    .weight(4f)
            ) {
                IconButton(
                    onClick = onBackRequest,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "",
                        tint = foregroundColor.value
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(9f),
                contentAlignment = Alignment.Center
            ) {
                MarqueeText(
                    text = toolBarTitle.value ?: "",
                    fontWeight = FontWeight.W700,
                    color = foregroundColor.value,
                    textAlign = Alignment.Center
                )
            }

            Row(
                modifier = Modifier
                    .weight(4f),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onSearchRequest
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "",
                        tint = foregroundColor.value
                    )
                }

                IconButton(
                    onClick = {  }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "",
                        tint = foregroundColor.value
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContextMenu(
    expanded: MutableState<Boolean>,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    if (expanded.value) {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    expanded.value = false
                }
            }
        ) {
            content()
        }
    }
}