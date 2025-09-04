package com.example.ktor_test_client.api.endpoints

import android.util.Log
import com.example.ktor_test_client.api.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val login: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)

suspend fun ApiClient.login(loginRequest: LoginRequest): HttpResponse {
    val response = httpClient.post("api/v1/auth/login") {
        setBody(loginRequest)
    }

    Log.d("API", response.status.toString())
    Log.d("API", response.bodyAsText())

    val tokens: LoginResponse = response.body()

    System.setProperty("accessToken", tokens.accessToken)
    System.setProperty("refreshToken", tokens.refreshToken)

    return response
}