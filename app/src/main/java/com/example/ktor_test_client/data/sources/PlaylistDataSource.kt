package com.example.ktor_test_client.data.sources

import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.data.providers.DataProvider

class PlaylistDataSource(
    var tracksId: List<String>,
    firstTrack: Int = 0
) : DataSource() {
    override var currentIndex: Int = firstTrack
        set(value) {
            if (value in tracks.indices)
                field = value
        }

    var tracks: MutableList<Track?> = MutableList(tracksId.size) { null }

    override suspend fun nextTrack(dataProvider: DataProvider): Track? {
        currentIndex = (currentIndex + 1) % tracks.size

        tracks[currentIndex]?.let {
            return it
        }

        tracks[currentIndex] = (loadTrack(tracksId[currentIndex], dataProvider))

        return tracks[currentIndex]
    }

    override suspend fun currentTrack(dataProvider: DataProvider): Track? {
        tracks[currentIndex]?.let {
            return it
        }

        tracks[currentIndex] = (loadTrack(tracksId[currentIndex], dataProvider))

        return tracks[currentIndex]
    }

    override suspend fun previousTrack(dataProvider: DataProvider): Track? {
        currentIndex = (currentIndex - 1).coerceIn(tracks.indices)

        tracks[currentIndex]?.let {
            return it
        }

        tracks[currentIndex] = (loadTrack(tracksId[currentIndex], dataProvider))

        return tracks[currentIndex]
    }

    private suspend fun loadTrack(id: String, dataProvider: DataProvider): Track? {
        return dataProvider.getTrack(id)
    }
}