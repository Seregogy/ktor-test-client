package com.example.ktor_test_client.controls

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktor_test_client.KtorApi
import com.example.ktor_test_client.R

@Preview(showBackground = true)
@Composable
fun ApiCard(
    modifier: Modifier = Modifier,
    apiMethodModel: ApiMethodModel = ApiMethodModel(
        route = "/user",
        params = mapOf(
            "id: Int" to "уникальный ID для каждого элемента",
            "name: String" to "впомогательный параметр"
        )
    ),
    api: KtorApi = KtorApi(),
    onDisable: (ApiMethodModel) -> Unit = { }
) {
    var expanded by remember { mutableStateOf(false) }

    var enabled by remember { mutableStateOf(true) }

    val swipeToDismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { -it * .9f }
    )

    val corners by animateDpAsState(
        targetValue = if (swipeToDismissState.currentValue == SwipeToDismissBoxValue.Settled) 0.dp else 12.dp,
        label = "corners animation"
    )

    LaunchedEffect(swipeToDismissState) {
        snapshotFlow { swipeToDismissState.progress }
            .collect {
                println(it)
            }
    }

    AnimatedVisibility(
        visible = enabled
    ) {
        SwipeToDismissBox(
            modifier = Modifier
                .fillMaxWidth(),
            state = swipeToDismissState,
            enableDismissFromStartToEnd = false,
            gesturesEnabled = true,
            backgroundContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.errorContainer)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.delete_icon),
                        contentDescription = "delete icon",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 25.dp)
                    )
                }
            },
            onDismiss = {
                onDisable(apiMethodModel)
                enabled = false
            }
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topEnd = corners, bottomEnd = corners))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .clickable {
                        expanded = !expanded
                    },
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    Text(
                        text = apiMethodModel.route,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.W700
                    )

                    AnimatedVisibility(
                        visible = !expanded
                    ) {
                        Row {
                            Text(
                                text = apiMethodModel.method
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = expanded
                    ) {
                        Column(
                            modifier = Modifier
                                .wrapContentHeight()
                        ) {
                            Spacer(Modifier.height(20.dp))

                            for (item in listOf(
                                "host" to apiMethodModel.apiHost,
                                "method" to apiMethodModel.method,
                                "params" to apiMethodModel.params.toList()
                                    .joinToString(separator = "\n") { "${it.first}: ${it.second}" }
                            )) {


                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .width(70.dp),
                                        text = item.first,
                                        textAlign = TextAlign.Right,
                                        fontWeight = FontWeight.W300
                                    )

                                    Text(
                                        text = item.second
                                    )
                                }

                            }

                            Button(
                                onClick = {

                                }
                            ) {
                                Text(
                                    text = "Send request"
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}

data class ApiMethodModel(
    val route: String = "unknown",
    val apiHost: String = "http://192.168.1.64:8080",
    val params: Map<String, String> = mapOf(),
    val method: String = "get"
)