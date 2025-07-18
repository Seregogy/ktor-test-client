package com.example.ktor_test_client.models

data class Track(
    val id: Int = 0,
    val albumId: Int = 0,
    val length: Float = 0f,
    val artistsId: List<Int> = listOf(),
)