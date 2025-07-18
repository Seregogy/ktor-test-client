package com.example.ktor_test_client.models

import androidx.compose.ui.graphics.Color

data class Artist(
    val id: Int = 0,
    val name: String = "unknown artist",
    val about: String = "nothing interesting",
    val listeningInMonth: Int = 0,
    val likes: Int = 0,
    val bestTracks: List<Int> = listOf(),
    val tracks: List<Int> = listOf(),
    val albums: List<Int> = listOf(),
    val socialMedia: Map<String, String> = mapOf(),
    val imagesUrl: List<Pair<String, Color>> = listOf()
)