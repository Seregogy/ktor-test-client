package com.example.ktor_test_client.state

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ScrollState(
    var alpha: Float = 0f,
    var colorAlpha: Float = 0f,
    var isAvatarVisible: Boolean = false,
    var currentOffset: Dp = 0.dp
)