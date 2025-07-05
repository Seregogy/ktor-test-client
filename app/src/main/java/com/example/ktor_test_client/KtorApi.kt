package com.example.ktor_test_client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class KtorApi(
    private val hostUrl: String = "http://192.168.1.64",
    private val port: Int = 8080
) {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    suspend fun fetchUser(id: Int): User? {
        val response = httpClient.get("$hostUrl:$port/user?id=$id")

        return if (response.status.isSuccess()) {
            response.body()
        } else {
            null
        }
    }
}

@Serializable
data class User(
    val id: Int,
    val name: String?,
    val about: String?,
    val email: String?
)