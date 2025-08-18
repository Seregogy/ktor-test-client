package com.example.ktor_test_client.api.methods

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

    println(response.status)
    println(response.bodyAsText())

    return if (response.status.isSuccess()) {
        response.body()
    } else {
        null
    }
}