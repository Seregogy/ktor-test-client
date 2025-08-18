package com.example.ktor_test_client.api.dtos

import com.example.ktor_test_client.api.dtos.BaseArtist
import com.example.ktor_test_client.api.dtos.BaseTrack
import kotlinx.serialization.Serializable

@Serializable
data class BaseAlbum(
	val id: String = "",
	val name: String = "",
	val imageUrl: String = "",
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