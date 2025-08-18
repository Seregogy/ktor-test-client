package com.example.ktor_test_client.data.providers

import com.example.ktor_test_client.api.KtorAPI
import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.api.methods.getTrack

class NetworkDataProvider(
    val apiService: KtorAPI
) : DataProvider() {
    override suspend fun getTrack(id: String): Track? {
        return apiService.getTrack(id)
    }

    override suspend fun searchTracks(query: String): List<Track> {
        TODO("Not yet implemented")
    }
}