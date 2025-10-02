package com.example.ktor_test_client.api.methods

import android.util.Log
import com.example.ktor_test_client.api.ApiClient
import com.example.ktor_test_client.api.dtos.BaseTrackWithArtists
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable

@Serializable
data class GetArtistTopTracksResponse(
    val tracks: List<BaseTrackWithArtists> = listOf()
)

suspend fun ApiClient.getTopTracksByArtist(artistId: String, limit: Int = 10): GetArtistTopTracksResponse? {
    val response = httpClient.get("api/v1/artists/$artistId/tracks/top?limit=$limit")

    Log.d("API", response.status.toString())
    Log.d("API", response.bodyAsText())

    return if (response.status.isSuccess()) {
        response.body()
    } else {
        null
    }
}