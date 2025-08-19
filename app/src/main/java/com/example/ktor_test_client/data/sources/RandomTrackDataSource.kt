package com.example.ktor_test_client.data.sources

import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.api.methods.getRandomTrackId
import com.example.ktor_test_client.data.providers.DataProvider
import com.example.ktor_test_client.data.providers.NetworkDataProvider

class RandomTrackDataSource : DataSource() {
    private var currentTrack = 0
    private val tracks: MutableList<Track> = mutableListOf()

    override suspend fun nextTrack(dataProvider: DataProvider): Track {
        if (currentTrack < tracks.indices.last) {
            currentTrack++
        } else {
            val track = loadNextTrack(dataProvider)

            track?.let {
                tracks.add(it)
                currentTrack = tracks.indices.last
            }
        }

        return tracks[currentTrack]
    }

    override suspend fun currentTrack(dataProvider: DataProvider): Track {
        if (tracks.size == 0) {
            loadNextTrack(dataProvider)?.let {
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

    private suspend fun loadNextTrack(dataProvider: DataProvider) =
        dataProvider.getTrack((dataProvider as NetworkDataProvider).apiService.getRandomTrackId()?.id ?: "")
}