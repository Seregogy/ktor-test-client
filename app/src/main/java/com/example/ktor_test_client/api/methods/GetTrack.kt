package com.example.ktor_test_client.api.methods

import android.util.Log
import com.example.ktor_test_client.api.KtorAPI
import com.example.ktor_test_client.api.dtos.Track
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

suspend fun KtorAPI.getTrack(id: String): Track? {
    val response = httpClient.get {
        url { host("api/v1/tracks/$id") }
    }

    Log.d("API", response.status.toString())
    Log.d("API", response.bodyAsText())

    return if (response.status.isSuccess()) {
        response.body()
    } else {
        null
    }
}