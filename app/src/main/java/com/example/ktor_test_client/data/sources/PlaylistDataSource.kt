package com.example.ktor_test_client.data.sources

import com.example.ktor_test_client.api.dtos.Track

class PlaylistDataSource(
    private val tracks: List<Track>,
    firstTrack: Int = 0
) : DataSource() {
    var currentTrack: Int = firstTrack
        private set

    override suspend fun nextTrack(): Track {
        currentTrack = (currentTrack + 1) % tracks.size

        return tracks[currentTrack]
    }

    override suspend fun currentTrack(): Track {
        return tracks[currentTrack]
    }

    override suspend fun previousTrack(): Track {
        currentTrack = (currentTrack - 1).coerceIn(tracks.indices)

        return tracks[currentTrack]
    }
}