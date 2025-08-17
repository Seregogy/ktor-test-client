package org.example.api.dtos

import kotlinx.serialization.Serializable

@Serializable
data class SocialMedia(
	val name: String = "",
	val link: String = ""
)

@Serializable
data class BaseArtist(
	val id: String = "",
	val name: String = "unknown artist",
	val imageUrl: String? = ""
)

@Serializable
data class FullArtist(
	val id: String = "",
	val name: String = "unknown artist",
	val imageUrl: String? = "",
	val about: String? = "",
	val listeningInMonth: Int = 0,
	val likes: Int = 0,
	val images: List<String> = listOf(),
	val socialMedias: List<SocialMedia> = listOf(),
	val albums: List<BaseAlbum> = listOf()
)