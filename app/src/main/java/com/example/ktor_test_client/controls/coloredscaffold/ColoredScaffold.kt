package com.example.ktor_test_client.controls.coloredscaffold

import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.ktor_test_client.helpers.contrast

@Composable
fun ColoredScaffold(
    state: ColoredScaffoldState,
    content: @Composable (ColoredScaffoldState.() -> Unit)
) {
    val colorScheme = MaterialTheme.colorScheme

    state.run {
        primaryColor = remember {
            derivedStateOf {
                if (currentPalette.value?.vibrantSwatch != null) {
                    val colorValue = if (currentPalette.value?.vibrantSwatch?.rgb == currentPalette.value?.dominantSwatch?.rgb)
                        currentPalette.value?.mutedSwatch?.rgb
                    else
                        currentPalette.value?.vibrantSwatch?.rgb

                    Color(colorValue ?: Color.Black.toArgb())
                } else {
                    Color(colorScheme.tertiary.toArgb())
                }
            }
        }

        onPrimaryColor = remember {
            derivedStateOf {
                if (currentPalette.value?.vibrantSwatch != null) {
                    val colorValue = if (currentPalette.value?.vibrantSwatch?.rgb == currentPalette.value?.dominantSwatch?.rgb)
                        currentPalette.value?.mutedSwatch?.titleTextColor
                    else
                        currentPalette.value?.vibrantSwatch?.titleTextColor

                    Color(colorValue ?: Color.White.toArgb())
                } else {
                    Color(colorScheme.onTertiary.toArgb())
                }
            }
        }

        backgroundColor = remember {
            derivedStateOf {
                Color(currentPalette.value?.dominantSwatch?.rgb ?: colorScheme.background.toArgb())
            }
        }

        onBackgroundColor = remember {
            derivedStateOf {
                Color(currentPalette.value?.dominantSwatch?.titleTextColor ?: colorScheme.onBackground.toArgb())
            }
        }

        backgroundColorAnimated = animateColorAsState(
            targetValue = backgroundColor.value,
            label = "animated background value",
            animationSpec = animationSpec
        )

        onBackgroundColorAnimated = animateColorAsState(
            targetValue = onBackgroundColor.value,
            label = "animated background value",
            animationSpec = animationSpec
        )

        primaryColorAnimated = animateColorAsState(
            targetValue = primaryColor.value,
            label = "animated background value",
            animationSpec = animationSpec
        )

        onPrimaryColorAnimated = animateColorAsState(
            targetValue = onPrimaryColor.value,
            label = "animated background value",
            animationSpec = animationSpec
        )
    }


    with(state) {
        content()
    }
}