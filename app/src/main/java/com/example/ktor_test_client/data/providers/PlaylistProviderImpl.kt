package com.example.ktor_test_client.data.providers

import android.util.Log
import com.example.ktor_test_client.api.MusicApiService

class PlaylistProviderImpl(
    val baseTracks: List<String> = listOf(),
    val musicApiService: MusicApiService
) : PlaylistProvider() {
    override suspend fun getTracks(): List<String> {
        if (baseTracks.isEmpty()) {
            return getAdditionalTracks(5, 0)
        }
        return baseTracks
    }

    override suspend fun getAdditionalTracks(count: Int, remain: Int): List<String> {
        return List(count) {
            musicApiService.getRandomTrackId().getOrNull() ?: ""
        }.also {
            Log.d("Playlist", it.toString())
        }
    }
}