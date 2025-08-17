package org.example.api.methods

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
data class RegisterRequest(
	val name: String = "",
	val email: String = "",
	val password: String = ""
)

@Serializable
data class RegisterResponse(
	val id: String,
	val name: String,
	val accessToken: String,
	val refreshToken: String
)

suspend fun KtorAPI.registration(registerRequest: RegisterRequest): RegisterResponse? {
	val response = httpClient.post {
		url { host("/api/v1/auth/register") }

		contentType(ContentType.Application.Json)
		setBody(registerRequest)
	}

	println(response.status)
	println(response.bodyAsText())

	return if (response.status.isSuccess()) {
		val responseData = response.body<RegisterResponse>()

		this.accessToken = responseData.accessToken
		this.refreshToken = responseData.refreshToken

		responseData
	} else {
		null
	}
}