package com.example.ktor_test_client.controls

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
fun rememberColoredScaffoldState(): State<ColoredScaffoldState> {
    return remember {
        mutableStateOf(ColoredScaffoldState())
    }
}

@Composable
fun ColoredScaffoldState.ColoredScaffold(
    content: @Composable (ColoredScaffoldState.() -> Unit)
) {
    val colorScheme = MaterialTheme.colorScheme

    backgroundColor = remember {
        derivedStateOf {
            Color(currentPalette.value?.dominantSwatch?.rgb ?: colorScheme.background.toArgb())
        }
    }

    foregroundColor = remember {
        derivedStateOf {
            Color(currentPalette.value?.dominantSwatch?.titleTextColor ?: colorScheme.onBackground.toArgb())
        }
    }

    primaryColor = remember {
        derivedStateOf {
            Color(currentPalette.value?.vibrantSwatch?.rgb ?: colorScheme.onSurface.toArgb())

        }
    }

    onPrimaryColor = remember {
        derivedStateOf {
            Color(currentPalette.value?.vibrantSwatch?.titleTextColor ?: colorScheme.onSurface.toArgb())

        }
    }

    animatedBackgroundColor = animateColorAsState(
        targetValue = backgroundColor.value,
        label = "animated background value",
        animationSpec = animationSpec
    )

    animatedForegroundColor = animateColorAsState(
        targetValue = foregroundColor.value,
        label = "animated background value",
        animationSpec = animationSpec
    )

    animatedPrimaryColor = animateColorAsState(
        targetValue = primaryColor.value,
        label = "animated background value",
        animationSpec = animationSpec
    )

    animatedOnPrimaryColor = animateColorAsState(
        targetValue = onPrimaryColor.value,
        label = "animated background value",
        animationSpec = animationSpec
    )

    with(this) {
        content()
    }
}