package com.example.ktor_test_client.api.methods

import com.example.ktor_test_client.api.KtorAPI
import com.example.ktor_test_client.api.dtos.BaseAlbum
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable

@Serializable
data class GetAlbumsByArtistResponse(
    val albums: List<BaseAlbum> = listOf()
)

suspend fun KtorAPI.getAlbumsFromArtist(artistId: String): GetAlbumsByArtistResponse? {
    val response = httpClient.get {
        url { host("api/v1/artists/$artistId/albums") }
    }

    println(response.status)
    println(response.bodyAsText())

    return if (response.status.isSuccess()) {
        response.body()
    } else {
        null
    }
}