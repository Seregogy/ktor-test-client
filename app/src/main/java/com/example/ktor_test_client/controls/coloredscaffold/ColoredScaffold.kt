package com.example.ktor_test_client.controls.coloredscaffold

import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

@Composable
fun ColoredScaffold(
    state: ColoredScaffoldState,
    content: @Composable (ColoredScaffoldState.() -> Unit)
) {
    val colorScheme = MaterialTheme.colorScheme

    state.run {
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
    }


    with(state) {
        content()
    }
}