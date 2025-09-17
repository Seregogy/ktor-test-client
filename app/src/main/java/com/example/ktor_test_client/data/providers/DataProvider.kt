package com.example.ktor_test_client.data.providers

import com.example.ktor_test_client.api.dtos.TrackFullDto

/**
 *Aбстракция класса, предоставляющего данные (локальные/сетевые)
 **/
abstract class DataProvider {
    abstract suspend fun getTrack(id: String): TrackFullDto?
    abstract suspend fun searchTracks(query: String): List<TrackFullDto>
}