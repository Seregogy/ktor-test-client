package com.example.ktor_test_client.controls.toolscaffold

import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ToolScaffold(
    modifier: Modifier = Modifier,
    state: ToolScaffoldState<*, *> ,
    content: @Composable (innerPadding: PaddingValues) -> Unit = { }
) {
    val toolBarHeight = 50.dp

    state.run {
        Box {
            content(PaddingValues(top = toolBarHeight))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (toolBarTitle.value != null) Color.Black.copy(0.92f) else Color.Transparent)
                    .then(modifier)
                    .height(toolBarHeight)
                    .padding(horizontal = 15.dp)
                    .align(Alignment.TopCenter)
            ) {
                IconButton(
                    onClick = onBackRequest,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "",
                        tint = onPrimaryColor.value
                    )
                }

                Text(
                    text = toolBarTitle.value ?: "",
                    fontWeight = FontWeight.W700,
                    color = onPrimaryColor.value,
                    modifier = Modifier
                        .align(Alignment.Center)
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                ) {
                    IconButton(
                        onClick = onSearchRequest
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "",
                            tint = onPrimaryColor.value
                        )
                    }

                    IconButton(
                        onClick = { /*toolAction*/ }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = "",
                            tint = onPrimaryColor.value
                        )
                    }
                }
            }
        }
    }
}