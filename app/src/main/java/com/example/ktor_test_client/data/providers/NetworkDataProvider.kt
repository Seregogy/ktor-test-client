package com.example.ktor_test_client.data.providers

import android.content.Context
import com.example.ktor_test_client.api.KtorAPI
import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.api.methods.getTrack
import com.example.ktor_test_client.api.InternetConnectionChecker

class NetworkDataProvider(
    val apiService: KtorAPI,
    val context: Context
) : DataProvider() {
    private val connectivityChecker = InternetConnectionChecker(context)
    private val isConnected = connectivityChecker.isConnected

    override suspend fun getTrack(id: String): Track? {
        return if (isConnected.value) {
            apiService.getTrack(id)
        } else {
            null
        }
    }

    override suspend fun searchTracks(query: String): List<Track> {
        TODO("Not yet implemented")
    }
}