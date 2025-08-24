package com.example.ktor_test_client.api.methods

import android.util.Log
import com.example.ktor_test_client.api.KtorAPI
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable

@Serializable
data class RandomTrackIdResponse(
    val id: String = ""
)

suspend fun KtorAPI.getRandomTrackId(): RandomTrackIdResponse? {
    val response = httpClient.get {
        url { host("api/v1/tracks/random/id") }
    }

    Log.d("API", response.status.toString())
    Log.d("API", response.bodyAsText())

    return if (response.status.isSuccess()) {
        response.body()
    } else {
        null
    }
}