package com.example.ktor_test_client.api.methods

import android.util.Log
import com.example.ktor_test_client.api.ApiClient
import com.example.ktor_test_client.api.dtos.Album
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

suspend fun ApiClient.getAlbum(albumId: String): Album? {
    val response = httpClient.get("api/v1/albums/$albumId")

    Log.d("API", response.status.toString())
    Log.d("API", response.bodyAsText())

    return if (response.status.isSuccess()) {
        response.body()
    } else {
        null
    }
}