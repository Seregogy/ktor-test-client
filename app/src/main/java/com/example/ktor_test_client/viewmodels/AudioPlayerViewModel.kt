package com.example.ktor_test_client.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.ktor_test_client.player.AudioPlayer
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
        audioPlayer.release()
    }
}