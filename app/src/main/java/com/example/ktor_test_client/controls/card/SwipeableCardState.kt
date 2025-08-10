package com.example.ktor_test_client.controls.card

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset

data class SwipeableCardState(
    var selected: MutableState<Boolean> = mutableStateOf(false),
    var swiped: MutableState<Boolean> = mutableStateOf(false),

    var initialIndex: Int = 0,
    var currentIndex: MutableState<Int> = mutableIntStateOf(0),

    var isDragging: MutableState<Boolean> = mutableStateOf(false),

    var xOffset: MutableState<Float> = mutableFloatStateOf(0f),
    var yOffset: MutableState<Float> = mutableFloatStateOf(0f),
    var scale: MutableState<Float> = mutableFloatStateOf(0f),

    var onSwipe: (SwipeDirection) -> Unit = { }
)