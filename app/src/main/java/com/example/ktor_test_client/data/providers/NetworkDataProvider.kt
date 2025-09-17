package com.example.ktor_test_client.data.providers

import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.api.dtos.TrackFullDto

class NetworkDataProvider(
    val apiService: MusicApiService
) : DataProvider() {

    override suspend fun getTrack(id: String): TrackFullDto? {
        apiService.getTrack(id).onSuccess { track ->
            return track
        }

        return null
    }

    override suspend fun searchTracks(query: String): List<TrackFullDto> {
        TODO("Not yet implemented")
    }
}