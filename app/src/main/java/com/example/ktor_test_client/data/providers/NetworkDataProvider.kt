package com.example.ktor_test_client.data.providers

import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.api.dtos.Track

class NetworkDataProvider(
    val apiService: MusicApiService
) : DataProvider() {

    override suspend fun getTrack(id: String): Track? {
        apiService.getTrack(id).onSuccess { track ->
            return track
        }

        return null
    }

    override suspend fun searchTracks(query: String): List<Track> {
        TODO("Not yet implemented")
    }
}