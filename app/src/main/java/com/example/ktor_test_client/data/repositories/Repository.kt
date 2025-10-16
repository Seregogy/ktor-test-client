package com.example.ktor_test_client.data.repositories

import com.example.ktor_test_client.api.dtos.Lyrics
import com.example.ktor_test_client.api.dtos.TrackFullDto

/**
 * Высший уровень абстракции над провайдерами и соурсами.
 * позволяет запрашивать треки у дата соурса и дата провайдера напрямую
 **/
abstract class Repository {
    abstract suspend fun getTrack(trackId: String): TrackFullDto?
    abstract suspend fun getTracks(tracksId: List<String>): List<TrackFullDto>?
    abstract suspend fun getLyrics(trackId: String): Lyrics?
}