package com.example.ktor_test_client.data.repositories

import android.util.Log
import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.api.dtos.TrackFullDto
class BaseNetworkRepository(
    private val service: MusicApiService,
) : Repository() {
    override suspend fun getTrack(id: String): TrackFullDto? =
        service.getTrack(id).getOrNull()

    override suspend fun getTracks(tracksId: List<String>): List<TrackFullDto>? {
        service.getTracks(tracksId).onSuccess {
            return@getTracks it
        }.onFailure {
            Log.e("API", "Error during fetch tracks")
            Log.e("API", "${it.cause?.message}\n${it.message}")
        }

        return null
    }
}

