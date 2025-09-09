package com.example.ktor_test_client.data.sources

import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.data.providers.DataProvider

/**
 * Абстракиця класса, источника данных (плейлист, поток, избранные)
 **/
abstract class DataSource {
    abstract var currentIndex: Int

    abstract suspend fun nextTrack(dataProvider: DataProvider): Track?
    abstract suspend fun currentTrack(dataProvider: DataProvider): Track?
    abstract suspend fun previousTrack(dataProvider: DataProvider): Track?
}