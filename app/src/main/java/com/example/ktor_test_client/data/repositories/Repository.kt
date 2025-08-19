package com.example.ktor_test_client.data.repositories

import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.data.sources.DataSource


/**
 * Высший уровень абстракции над провайдерами и соурсами.
 * позволяет запрашивать треки у дата соурса и дата провайдера напрямую
 **/
abstract class Repository {
    abstract var dataSource: DataSource

    abstract suspend fun getTrack(id: String): Track?
    abstract suspend fun searchTracks(query: String): List<Track>

    abstract suspend fun nextTrack(): Track?
    abstract suspend fun currentTrack(): Track?
    abstract suspend fun previousTrack(): Track?

    fun injectDataSource(dataSource: DataSource) {
        this.dataSource = dataSource
    }
}