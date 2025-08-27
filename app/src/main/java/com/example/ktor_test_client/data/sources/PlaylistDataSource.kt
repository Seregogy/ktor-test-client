package com.example.ktor_test_client.data.sources

import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.data.providers.DataProvider

class PlaylistDataSource(
    var tracksId: List<String>,
    firstTrack: Int = 0
) : DataSource() {
    var tracks: MutableList<Track?> = MutableList(tracksId.size) { null }

    var currentTrack: Int = firstTrack
        set(value) {
            if (value in tracks.indices)
                field = value
        }

    override suspend fun nextTrack(dataProvider: DataProvider): Track? {
        currentTrack = (currentTrack + 1) % tracks.size

        tracks[currentTrack]?.let {
            return it
        }

        tracks[currentTrack] = (loadTrack(tracksId[currentTrack], dataProvider))

        return tracks[currentTrack]
    }

    override suspend fun currentTrack(dataProvider: DataProvider): Track? {
        tracks[currentTrack]?.let {
            return it
        }

        tracks[currentTrack] = (loadTrack(tracksId[currentTrack], dataProvider))

        return tracks[currentTrack]
    }

    override suspend fun previousTrack(dataProvider: DataProvider): Track? {
        currentTrack = (currentTrack - 1).coerceIn(tracks.indices)

        tracks[currentTrack]?.let {
            return it
        }

        tracks[currentTrack] = (loadTrack(tracksId[currentTrack], dataProvider))

        return tracks[currentTrack]
    }

    private suspend fun loadTrack(id: String, dataProvider: DataProvider): Track? {
        return dataProvider.getTrack(id)
    }
}