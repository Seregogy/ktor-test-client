package com.example.ktor_test_client.models

import androidx.compose.ui.graphics.Color

data class Album(
    val id: Int = 0,
    val artistId: Int = 0,
    val name: String = "album",
    val likes: Int = 0,
    val tracksId: List<Int> = listOf(),
    val bestTracks: List<Int> = listOf(),
    val totalListening: Int = 0,
    val releaseDate: Long = 0,
    val imageUrl: String = "",
    val label: String = "",
    val primaryColor: Color = Color.Transparent
)