package com.example.ktor_test_client.data.sources

import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.api.methods.getRandomTrackId
import com.example.ktor_test_client.data.providers.NetworkDataProvider

class RandomTrackDataSource(
    private val networkDataProvider: NetworkDataProvider,
) : DataSource() {
    private var currentTrackIndex = 0
    private val tracks: MutableList<Track> = mutableListOf()

    override suspend fun nextTrack(): Track {
        if (currentTrackIndex < tracks.indices.last) {
            currentTrackIndex++
        } else {
            val track = networkDataProvider.getTrack(networkDataProvider.apiService.getRandomTrackId()?.id ?: "")

            track?.let {
                tracks.add(it)
                currentTrackIndex = tracks.indices.last
            }
        }

        return tracks[currentTrackIndex]
    }

    override suspend fun currentTrack(): Track {
        return tracks[currentTrackIndex]
    }

    override suspend fun previousTrack(): Track {
        currentTrackIndex = (currentTrackIndex - 1).coerceIn(tracks.indices)

        return tracks[currentTrackIndex]
    }
}