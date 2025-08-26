package com.example.ktor_test_client.data.sources

import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.data.providers.DataProvider

class SingleTrackDataSource(
    private val id: String
) : DataSource() {
    private var track: Track? = null

    override suspend fun nextTrack(dataProvider: DataProvider): Track? {
        return currentTrack(dataProvider)
    }

    override suspend fun currentTrack(dataProvider: DataProvider): Track? {
        loadTrack(dataProvider)

        return track
    }

    override suspend fun previousTrack(dataProvider: DataProvider): Track? {
        return currentTrack(dataProvider)
    }

    private suspend fun loadTrack(dataProvider: DataProvider) {
        if (track == null)
            track = dataProvider.getTrack(id)
    }
}