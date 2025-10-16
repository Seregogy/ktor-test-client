package com.example.ktor_test_client.data.providers

abstract class PlaylistProvider {
    abstract suspend fun getTracks(): List<String>
    abstract suspend fun getAdditionalTracks(count: Int, remain: Int): List<String>
}