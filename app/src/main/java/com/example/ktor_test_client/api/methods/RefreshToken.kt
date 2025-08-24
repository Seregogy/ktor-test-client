package com.example.ktor_test_client.api.methods

import android.util.Log
import com.example.ktor_test_client.api.KtorAPI
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable

@Serializable
data class UpdateAccessTokenRequest(
	val refreshToken: String
)

@Serializable
data class UpdateAccessTokensResponse(
	val accessToken: String,
	val refreshToken: String
)

suspend fun KtorAPI.refreshTokens(): UpdateAccessTokensResponse? {
	val response = httpClient.post {
		url { host("/api/v1/auth/refresh-token") }

		contentType(ContentType.Application.Json)
		setBody(UpdateAccessTokenRequest(refreshToken))
	}

	Log.d("API", response.status.toString())
	Log.d("API", response.bodyAsText())

	return if (response.status.isSuccess()) {
		val parsedResponse = response.body<UpdateAccessTokensResponse>()

		accessToken = parsedResponse.accessToken
		refreshToken = parsedResponse.refreshToken

		parsedResponse
	} else {
		null
	}
}