package com.example.ktor_test_client.data.repositories

import com.example.ktor_test_client.api.dtos.Track


/**
 * Высший уровень абстракции над провайдерами и соурсами.
 * позволяет запрашивать треки у дата соурса и дата провайдера напрямую
 **/
abstract class Repository {
    abstract suspend fun getTrack(id: String): Track?
    abstract suspend fun searchTracks(query: String): List<Track>

    abstract suspend fun nextTrack(): Track?
    abstract suspend fun currentTrack(): Track?
    abstract suspend fun previousTrack(): Track?
}