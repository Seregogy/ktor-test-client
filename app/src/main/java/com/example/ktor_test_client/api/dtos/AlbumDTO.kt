package com.example.ktor_test_client.api.dtos

import kotlinx.serialization.Serializable

@Serializable
data class BaseAlbum(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val artists: List<BaseArtist> = listOf()
)

@Serializable
data class Album(
    val name: String = "",
    val likes: Int = 0,
    val listening: Int = 0,
    val releaseDate: Long = 0,
    val imageUrl: String? = null,
    val label: String? = null,
    val tracks: List<BaseTrack> = listOf(),
    val artists: List<BaseArtist> = listOf()
)