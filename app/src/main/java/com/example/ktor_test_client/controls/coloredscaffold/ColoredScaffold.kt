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
        primaryOrBackgroundColor = remember {
            derivedStateOf {
                return@derivedStateOf if (currentPalette.value?.vibrantSwatch == null) {
                    Color(currentPalette.value?.dominantSwatch?.rgb ?: colorScheme.tertiary.toArgb())
                } else {
                    Color(currentPalette.value?.vibrantSwatch?.rgb!!)
                }
            }
        }

        onPrimaryOrBackgroundColor = remember {
            derivedStateOf {
                return@derivedStateOf if (currentPalette.value?.vibrantSwatch == null) {
                    Color(currentPalette.value?.dominantSwatch?.titleTextColor ?: colorScheme.tertiary.toArgb())
                } else {
                    Color(currentPalette.value?.vibrantSwatch?.titleTextColor!!)
                }
            }
        }

        textOnPrimaryOrBackgroundColor = remember {
            derivedStateOf {
                return@derivedStateOf if (currentPalette.value?.vibrantSwatch != null) {
                    if (currentPalette.value?.vibrantSwatch?.rgb!!.contrast(currentPalette.value?.dominantSwatch?.rgb) < 3f) {
                        Color(currentPalette.value?.dominantSwatch?.titleTextColor ?: colorScheme.onTertiary.toArgb())
                    } else {
                        Color(currentPalette.value?.vibrantSwatch?.rgb ?: colorScheme.tertiary.toArgb())
                    }
                } else {
                    Color(currentPalette.value?.dominantSwatch?.titleTextColor ?: colorScheme.onTertiary.toArgb())
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

        primaryOrBackgroundColorAnimated = animateColorAsState(
            targetValue = primaryOrBackgroundColor.value,
            label = "animated background value",
            animationSpec = animationSpec
        )

        onPrimaryOrBackgroundColorAnimated = animateColorAsState(
            targetValue = onPrimaryOrBackgroundColor.value,
            label = "animated background value",
            animationSpec = animationSpec
        )

        textOnPrimaryOrBackgroundColorAnimated = animateColorAsState(
            targetValue = textOnPrimaryOrBackgroundColor.value,
            label = "animated background value",
            animationSpec = animationSpec
        )
    }


    with(state) {
        content()
    }
}