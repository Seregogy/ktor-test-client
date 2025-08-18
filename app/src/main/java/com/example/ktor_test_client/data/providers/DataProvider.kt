package com.example.ktor_test_client.data.providers

import com.example.ktor_test_client.api.dtos.Track

/**
 *Aбстракция класса, предоставляющего данные (локальные/сетевые)
 **/
abstract class DataProvider {
    abstract suspend fun getTrack(id: String): Track?
    abstract suspend fun searchTracks(query: String): List<Track>
}