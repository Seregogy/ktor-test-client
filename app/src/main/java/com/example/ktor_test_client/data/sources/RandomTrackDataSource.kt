package com.example.ktor_test_client.data.sources

import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.data.providers.DataProvider

class RandomTrackDataSource(
    private val apiService: MusicApiService
) : DataSource() {
    private var currentTrack = 0
    private val tracks: MutableList<Track> = mutableListOf()

    override suspend fun nextTrack(dataProvider: DataProvider): Track {
        if (currentTrack < tracks.indices.last) {
            currentTrack++
        } else {
            val track = loadNextTrack()

            track?.let {
                tracks.add(it)
                currentTrack = tracks.indices.last
            }
        }

        return tracks[currentTrack]
    }

    override suspend fun currentTrack(dataProvider: DataProvider): Track {
        if (tracks.size == 0) {
            loadNextTrack()?.let {
                tracks.add(it)

                currentTrack = tracks.indices.last
            }
        }

        return tracks[currentTrack]
    }

    override suspend fun previousTrack(dataProvider: DataProvider): Track {
        currentTrack = (currentTrack - 1).coerceIn(tracks.indices)

        return tracks[currentTrack]
    }

    private suspend fun loadNextTrack(): Track? {
        apiService.getRandomTrack().onSuccess {
            return it
        }

        return null
    }
}