package com.example.ktor_test_client.data.sources

import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.data.providers.DataProvider

class RandomTrackDataSource(
    private val apiService: MusicApiService
) : DataSource() {

    override var currentIndex: Int = 0
    private val tracks: MutableList<Track> = mutableListOf()

    override suspend fun nextTrack(dataProvider: DataProvider): Track {
        if (currentIndex < tracks.indices.last) {
            currentIndex++
        } else {
            val track = loadNextTrack()

            track?.let {
                tracks.add(it)
                currentIndex = tracks.indices.last
            }
        }

        return tracks[currentIndex]
    }

    override suspend fun currentTrack(dataProvider: DataProvider): Track {
        if (tracks.size == 0) {
            loadNextTrack()?.let {
                tracks.add(it)

                currentIndex = tracks.indices.last
            }
        }

        return tracks[currentIndex]
    }

    override suspend fun previousTrack(dataProvider: DataProvider): Track {
        currentIndex = (currentIndex - 1).coerceIn(tracks.indices)

        return tracks[currentIndex]
    }

    private suspend fun loadNextTrack(): Track? {
        apiService.getRandomTrack().onSuccess {
            return it
        }

        return null
    }
}