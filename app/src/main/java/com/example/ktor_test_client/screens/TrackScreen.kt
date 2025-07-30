package com.example.ktor_test_client.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ktor_test_client.viewmodels.TrackScreenViewModel

@Composable
fun AudioPlayerScreen(viewModel: TrackScreenViewModel = viewModel()) {
    val context = LocalContext.current
    val isPlaying by remember { derivedStateOf { viewModel.exoPlayer?.isPlaying ?: false } }
    val currentPosition by remember { derivedStateOf { viewModel.exoPlayer?.currentPosition ?: 0L } }

    LaunchedEffect(Unit) {
        viewModel.initializePlayer(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.releasePlayer()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Position: ${currentPosition / 1000}s")

        Button(onClick = { viewModel.playPause() }) {
            Text(if (isPlaying) "Pause" else "Play")
        }

        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { viewModel.seekTo(it.toLong()) },
            valueRange = 0f..(viewModel.exoPlayer?.duration?.toFloat() ?: 0f)
        )
    }
}