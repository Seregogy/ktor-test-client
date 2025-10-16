package com.example.ktor_test_client.api.dtos

import kotlinx.serialization.Serializable

@Serializable
data class Lyrics(
    val plainText: String? = null,
    val syncedText: Map<Long, String>? = null,
    val provider: String? = null
)