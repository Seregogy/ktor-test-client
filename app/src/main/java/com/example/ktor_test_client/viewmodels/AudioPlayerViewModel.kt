package com.example.ktor_test_client.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.api.dtos.Lyrics
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