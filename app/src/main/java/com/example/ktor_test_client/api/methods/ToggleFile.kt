package com.example.ktor_test_client.api.methods

import android.util.Log
import com.example.ktor_test_client.api.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable

@Serializable
data class ToggleLikeResponse(
    val trackId: String,
    val liked: Boolean
)

suspend fun ApiClient.toggleLike(trackId: String): ToggleLikeResponse? {
    val response = httpClient.post("api/v1/tracks/$trackId/like")

    Log.d("API", response.status.toString())
    Log.d("API", response.bodyAsText())

    return if (response.status.isSuccess()) {
        response.body()
    } else {
        null
    }
}