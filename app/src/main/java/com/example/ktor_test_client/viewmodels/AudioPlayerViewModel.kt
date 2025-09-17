package com.example.ktor_test_client.viewmodels

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.ktor_test_client.data.AudioPlayer
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    val audioPlayer: AudioPlayer,
    context: Context
) : ImagePaletteViewModel() {
    init {
        viewModelScope.launch {
            audioPlayer.currentTrack.collect { track ->
                track?.data?.imageUrl?.let {
                    fetchImageByUrl(context, it)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        audioPlayer.release()
    }
}