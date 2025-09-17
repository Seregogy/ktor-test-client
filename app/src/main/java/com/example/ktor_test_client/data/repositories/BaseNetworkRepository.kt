package com.example.ktor_test_client.data.repositories

import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.api.dtos.TrackFullDto

class BaseNetworkRepository(
    private val service: MusicApiService,
) : Repository() {
    override suspend fun getTrack(id: String): TrackFullDto? {
        service.getTrack(id).onSuccess {
            return it
        }

        return null
    }
}