package com.example.ktor_test_client.control.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ktor_test_client.R
import com.example.ktor_test_client.control.coloredscaffold.ColoredScaffoldState
import com.example.ktor_test_client.player.AudioPlayer
import com.example.ktor_test_client.viewmodel.AudioPlayerViewModel
import kotlinx.coroutines.launch

@Composable
fun ColoredScaffoldState.PlayerAdditionalControls(
    modifier: Modifier,
    viewModel: AudioPlayerViewModel,
    isLyricsOpen: MutableState<Boolean>
) {
    val coroutineScope = rememberCoroutineScope()
    val repeatMode by viewModel.audioPlayer.repeatModeState.collectAsStateWithLifecycle()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = { }
        ) {
            Icon(
                painter = painterResource(R.drawable.timer_icon),
                contentDescription = "time icon",
                tint = onBackgroundColorAnimated.value
            )
        }

        IconToggleButton(
            checked = isLyricsOpen.value,
            onCheckedChange = {
                isLyricsOpen.value = !isLyricsOpen.value
                coroutineScope.launch {
                    viewModel.audioPlayer.getLyricsForCurrentTrack()
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.lyrics_icon),
                contentDescription = "time icon",
                tint = onBackgroundColorAnimated.value
            )
        }

        IconButton(
            onClick = {
                viewModel.audioPlayer.nextRepeatMode()
            }
        ) {
            Icon(
                painter = when(repeatMode) {
                    AudioPlayer.RepeatMode.Single -> painterResource(R.drawable.repeat_icon_1)
                    AudioPlayer.RepeatMode.Playlist -> painterResource(R.drawable.repeat_icon)
                    AudioPlayer.RepeatMode.None -> painterResource(R.drawable.repeat_off)
                },
                contentDescription = "time icon",
                tint = onBackgroundColorAnimated.value
            )
        }
    }
}