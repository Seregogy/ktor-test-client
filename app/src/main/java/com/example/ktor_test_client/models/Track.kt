package com.example.ktor_test_client.models

data class Track(
    val id: Int = 0,
    val albumId: Int = 0,
    val name: String = "",
    val seconds: Int = 0,
    val lyrics: String = "",
    val artistsId: List<Int> = listOf()
)