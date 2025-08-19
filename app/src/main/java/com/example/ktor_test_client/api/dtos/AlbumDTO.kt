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
	val name: String,
	val likes: Int,
	val listening: Int,
	val releaseDate: Long,
	val imageUrl: String?,
	val label: String?,
	val tracks: List<BaseTrack>,
	val artists: List<BaseArtist>
)