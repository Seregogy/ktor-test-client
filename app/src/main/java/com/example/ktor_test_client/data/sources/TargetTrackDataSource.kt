package com.example.ktor_test_client.data.sources

import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.data.providers.DataProvider

class TargetTrackDataSource(
    private val id: String,
    private val dataProvider: DataProvider
) : DataSource() {
    private var track: Track? = null

    override suspend fun nextTrack(): Track? {
        loadTrack()

        return track
    }

    override suspend fun currentTrack(): Track? {
        loadTrack()

        return track
    }

    override suspend fun previousTrack(): Track? {
        loadTrack()

        return track
    }

    private suspend fun loadTrack() {
        if (track == null)
            track = dataProvider.getTrack(id)
    }
}