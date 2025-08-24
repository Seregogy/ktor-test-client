package com.example.ktor_test_client.api.methods

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import com.example.ktor_test_client.api.KtorAPI
import io.ktor.client.statement.bodyAsText

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

suspend fun KtorAPI.login(loginRequest: LoginRequest): HttpResponse {
	val response = httpClient.post("$endpoint/api/v1/auth/login") {
		contentType(ContentType.Application.Json)
		setBody(loginRequest)
	}

	Log.d("API", response.status.toString())
	Log.d("API", response.bodyAsText())

	val tokens: LoginResponse = response.body()

	System.setProperty("accessToken", tokens.accessToken)
	System.setProperty("refreshToken", tokens.refreshToken)

	return response
}