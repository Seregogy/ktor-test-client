package com.example.ktor_test_client.api.dtos

import kotlinx.serialization.Serializable

@Serializable
open class BaseTrack(
    val id: String = "",
    val name: String = "",
    val imageUrl: String? = "",
    val indexInAlbum: Int = 0
)

@Serializable
open class BaseTrackWithArtists(
    val id: String = "",
    val name: String = "",
    val imageUrl: String? = "",
    val indexInAlbum: Int = 0,
    val artists: List<BaseArtist> = listOf()
)

@Serializable
class TrackFullDto(
    val id: String = "",
    val name: String = "",
    val imageUrl: String? = "",
    val indexInAlbum: Int = 0,
    val durationSeconds: Int = 0,
    val lyrics: String? = "",
    val listening: Int? = 0,
    val isExplicit: Boolean? = false,
    val audioUrl: String = "",
    val album: BaseAlbum = BaseAlbum()
)