package com.example.ktor_test_client.controls.coloredscaffold

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette

object ColoredScaffoldDefaults {
    val animationSpec = tween<Color>(durationMillis = 700, easing = LinearEasing)
}

class ColoredScaffoldState(
    var currentPalette: State<Palette?> = mutableStateOf(null),
    var backgroundColor: State<Color> = mutableStateOf(Color.Black),
    var foregroundColor: State<Color> = mutableStateOf(Color.White),
    var primaryColor: State<Color> = mutableStateOf(Color.Yellow),
    var onPrimaryColor: State<Color> = mutableStateOf(Color.Black),

    val animationSpec: AnimationSpec<Color> = ColoredScaffoldDefaults.animationSpec,

    var animatedBackgroundColor: State<Color> = mutableStateOf(Color.Black),
    var animatedForegroundColor: State<Color> = mutableStateOf(Color.White),
    var animatedPrimaryColor: State<Color> = mutableStateOf(Color.Yellow),
    var animatedOnPrimaryColor: State<Color> = mutableStateOf(Color.Black)
)

@Composable
fun rememberColoredScaffoldState(): ColoredScaffoldState {
    return remember {
        mutableStateOf(ColoredScaffoldState())
    }.value
}

@Composable
fun rememberColoredScaffoldState(currentPalette: @Composable () -> State<Palette?>): ColoredScaffoldState {
    val palette = currentPalette()

    return remember {
        mutableStateOf(ColoredScaffoldState(palette))
    }.value
}