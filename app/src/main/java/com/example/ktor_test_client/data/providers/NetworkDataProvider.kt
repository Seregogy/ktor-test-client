package com.example.ktor_test_client.data.providers

import android.content.Context
import com.example.ktor_test_client.api.ApiClient
import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.api.methods.getTrack
import com.example.ktor_test_client.api.tools.InternetConnectionChecker

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