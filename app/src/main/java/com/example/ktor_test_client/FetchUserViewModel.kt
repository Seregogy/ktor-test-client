package com.example.ktor_test_client

import androidx.lifecycle.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class FetchUserViewModel(
    private val hostUrl: String,
    private val port: Int
) : ViewModel() {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                }
            )
        }
    }

    suspend fun fetchTestFile() {
        val response = httpClient.get("$hostUrl:$port/download")

        println(response.status)
    }
}