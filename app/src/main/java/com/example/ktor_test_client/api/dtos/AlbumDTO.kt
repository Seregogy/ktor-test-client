package org.example.api.dtos

import kotlinx.serialization.Serializable

@Serializable
data class BaseAlbum(
	val id: String = "",
	val name: String = "",
	val imageUrl: String = "",
)

@Serializable
data class FullAlbum(
	val name: String,
	val likes: Int,
	val listening: Int,
	val releaseDate: Long,
	val imageUrl: String?,
	val label: String?,
	val tracks: List<BaseTrack>,
	val artists: List<BaseArtist>
)