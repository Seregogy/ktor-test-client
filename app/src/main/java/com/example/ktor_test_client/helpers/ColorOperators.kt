package com.example.ktor_test_client.helpers

import androidx.compose.ui.graphics.Color

operator fun Color.times(multiplier: Float): Color {
    return Color(this.red * multiplier, this.green * multiplier, this.blue * multiplier, 1f)
}