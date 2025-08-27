package com.example.ktor_test_client.data.repositories

import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.data.providers.DataProvider
import com.example.ktor_test_client.data.sources.DataSource

class BaseNetworkRepository(
    private val dataProvider: DataProvider,
) : Repository() {
    override lateinit var dataSource: DataSource

    override suspend fun getTrack(id: String): Track? {
        return dataProvider.getTrack(id)
    }

    override suspend fun searchTracks(query: String): List<Track> {
        return dataProvider.searchTracks(query)
    }

    override suspend fun nextTrack(): Track? {
        return dataSource.nextTrack(dataProvider)
    }

    override suspend fun currentTrack(): Track? {
        return dataSource.currentTrack(dataProvider)
    }

    override suspend fun previousTrack(): Track? {
        return dataSource.previousTrack(dataProvider)
    }
}