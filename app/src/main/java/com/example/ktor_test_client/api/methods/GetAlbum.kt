package com.example.ktor_test_client.api.methods

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import com.example.ktor_test_client.api.KtorAPI
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request

@Serializable
data class ArtistByAlbum(
    val id: String,
    val name: String,
    val imageUrl: String
)

@Serializable
data class TrackByAlbum(
    val id: String,
    val name: String,
    val isExplicit: Boolean
)

@Serializable
data class AlbumResponse(
    val name: String,
    val likes: Int,
    val listening: Int,
    val releaseDate: Long,
    val imageUrl: String?,
    val label: String?,
    val tracks: List<Track>,
    val artists: List<Artist>
)

suspend fun KtorAPI.getAlbumById(id: String?): AlbumResponse? {
    val response = httpClient.get {
        url {
            host("api/v1/albums/$id")
        }
    }

    println(response.request.url)
    println(response.status)
    println(response.status.isSuccess())
    println(response.bodyAsText())

    return if (response.status.isSuccess()) {
        response.body()
    } else {
        null
    }
}