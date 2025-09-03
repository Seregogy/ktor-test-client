package com.example.ktor_test_client.helpers

import androidx.compose.ui.graphics.Color

operator fun Color.times(multiplier: Float): Color {
    return Color(this.red * multiplier, this.green * multiplier, this.blue * multiplier, 1f)
}

fun Color.contrast(otherColor: Color): Double {
    val lum1 = this.l() + 0.05f
    val lum2 = otherColor.l() + 0.05f

    return maxOf(lum1, lum2) / minOf(lum1, lum2)
}

fun Int.contrast(otherColorValue: Int?): Double {
    otherColorValue?.let {
        val lum1 = Color(this).l() + 0.05f
        val lum2 = Color(otherColorValue).l() + 0.05f

        return maxOf(lum1, lum2) / minOf(lum1, lum2)
    }

    return .0
}

fun Color.l(): Double {
    return 0.2126 * this.red + 0.7152 * this.green + 0.0722 * this.blue
}