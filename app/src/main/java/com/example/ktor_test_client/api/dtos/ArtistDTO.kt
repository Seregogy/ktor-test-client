package com.example.ktor_test_client.api.dtos

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
    val about: String? = "unknown artist",
    val imageUrl: String? = ""
)

@Serializable
data class Artist(
    val id: String = "",
    val name: String = "unknown artist",
    val imagesUrl: List<String> = listOf(),
    val about: String? = "",
    val listeningInMonth: Int = 0,
    val likes: Int = 0,
    val images: List<String> = listOf(),
    val socialMedias: List<SocialMedia> = listOf(),
    val albums: List<BaseAlbum> = listOf()
)