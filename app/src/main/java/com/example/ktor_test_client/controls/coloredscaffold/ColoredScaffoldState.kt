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

class ColoredScaffoldState(
    var currentPalette: State<Palette?> = mutableStateOf(null),
    var backgroundColor: State<Color> = mutableStateOf(Color.Black),
    var onBackgroundColor: State<Color> = mutableStateOf(Color.White),
    var primaryColor: State<Color> = mutableStateOf(Color.Yellow),
    var onPrimaryColor: State<Color> = mutableStateOf(Color.Black),

    val animationSpec: AnimationSpec<Color> = tween(durationMillis = 700, easing = LinearEasing),

    var backgroundColorAnimated: State<Color> = mutableStateOf(Color.Black),
    var onBackgroundColorAnimated: State<Color> = mutableStateOf(Color.White),
    var primaryColorAnimated: State<Color> = mutableStateOf(Color.Yellow),
    var onPrimaryColorAnimated: State<Color> = mutableStateOf(Color.Black)
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

@Composable
fun rememberColoredScaffoldState(
    animationSpec: AnimationSpec<Color>,
    currentPalette: @Composable () -> State<Palette?>
): ColoredScaffoldState {
    val palette = currentPalette()

    return remember {
        mutableStateOf(
            ColoredScaffoldState(
                currentPalette = palette,
                animationSpec = animationSpec
            )
        )
    }.value
}