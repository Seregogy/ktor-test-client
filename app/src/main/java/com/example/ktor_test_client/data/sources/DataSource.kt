package com.example.ktor_test_client.data.sources

import com.example.ktor_test_client.api.dtos.Track


/**
 * Абстракиця класса, источника данных (плейлист, поток, избранные)
 **/
abstract class DataSource {
    abstract suspend fun nextTrack(): Track?
    abstract suspend fun currentTrack(): Track?
    abstract suspend fun previousTrack(): Track?
}