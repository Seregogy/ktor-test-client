package com.example.ktor_test_client.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Spring.StiffnessLow
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Preview(showBackground = true)
@Composable
fun CardRectangle(color: Color = MaterialTheme.colorScheme.primary) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 15.dp
        ),
        modifier = Modifier
            .fillMaxWidth(.8f)
            .aspectRatio(.7f)
    ) { }
}

@Composable
fun ElementsStackTest() {
    ElementsStack(
        Modifier,
        mutableListOf(
            { CardRectangle(MaterialTheme.colorScheme.primary) },
            { CardRectangle(MaterialTheme.colorScheme.secondary) },
            { CardRectangle(MaterialTheme.colorScheme.tertiary) },
            { CardRectangle(MaterialTheme.colorScheme.primaryFixed) },
            { CardRectangle(MaterialTheme.colorScheme.primary) },
            { CardRectangle(MaterialTheme.colorScheme.secondary) },
            { CardRectangle(MaterialTheme.colorScheme.tertiary) },
            { CardRectangle(MaterialTheme.colorScheme.primaryFixed) },
            { CardRectangle(MaterialTheme.colorScheme.primary) },
            { CardRectangle(MaterialTheme.colorScheme.secondary) },
            { CardRectangle(MaterialTheme.colorScheme.tertiary) },
            { CardRectangle(MaterialTheme.colorScheme.primaryFixed) }
        ),
        3,
        700
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ElementsStack(
    modifier: Modifier,
    items: MutableList<@Composable () -> Unit>,
    maxVisibleSize: Int,
    maxOffsetDeviation: Int
) {
    /*val itemsMap = remember {
        items.mapIndexed { index, item ->
            item to SwipeableCardData(
                totalIndex = index
            )
        }.toMutableList()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            for (i in itemsMap.indices) {
                val content by remember { mutableStateOf(itemsMap[i].first) }
                val data by remember { mutableStateOf(itemsMap[i].second) }

                val animatedScaleState = animateFloatAsState(
                    targetValue = data.scale.value,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = StiffnessLow
                    ),
                    label = "card scale animation"
                )

                val animatedOffsetState = animateOffsetAsState(
                    targetValue = Offset(data.xOffset.value, data.yOffset.value),
                    animationSpec = if (data.isDragging.value) tween(0) else spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = StiffnessLow
                    ),
                    label = "card offset animation"
                )

                itemsMap.forEach {
                    println("${it.second.hashCode()}: ${it.second.totalIndex}")
                }
                println("----------------")

                data.scale.value = 1 - i.toFloat() / 20

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = animatedOffsetState.value.x.roundToInt(),
                                y = animatedOffsetState.value.y.roundToInt() + ((20).dp.toPx() * i).roundToInt()
                            )
                        }
                        .scale(animatedScaleState.value)
                        .zIndex(-i.toFloat())
                        .pointerInput(Unit) {
                            if (i == itemsMap.size - 1) return@pointerInput

                            detectDragGestures(
                                onDragStart = {
                                    data.isDragging.value = true
                                },
                                onDragEnd = {
                                    data.xOffset.value = 0f
                                    data.yOffset.value = 0f

                                    data.isDragging.value = false
                                },
                                onDragCancel = {
                                    data.xOffset.value = 0f
                                    data.yOffset.value = 0f

                                    data.isDragging.value = false
                                }
                            ) { change, dragAmount ->
                                change.consume()

                                data.xOffset.value += dragAmount.x * 1.2f
                                data.yOffset.value += dragAmount.y * .5f

                                if (data.xOffset.value.absoluteValue > maxOffsetDeviation) {
                                    data.xOffset.value = 0f
                                    data.yOffset.value = 0f

                                    data.isDragging.value = false
                                    itemsMap.removeFirst()

                                    return@detectDragGestures
                                }
                            }
                        }
                        .graphicsLayer {
                            if (i == itemsMap.size - 1) return@graphicsLayer

                            rotationZ = animatedOffsetState.value.x / 50
                        }
                ) {
                    content()
                }
            }
        }
    }*/
}