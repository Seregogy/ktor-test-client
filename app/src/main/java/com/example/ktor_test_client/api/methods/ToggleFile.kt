package com.example.ktor_test_client.api.methods

import android.util.Log
import com.example.ktor_test_client.api.KtorAPI
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class ToggleLikeResponse(
	val trackId: String,
	val liked: Boolean
)

suspend fun KtorAPI.toggleLike(trackId: String) : ToggleLikeResponse? {
	val response = httpClient.post("api/v1/tracks/$trackId/like")

	Log.d("API", response.status.toString())
	Log.d("API", response.bodyAsText())

	return if (response.status.isSuccess()) {
		response.body()
	} else {
		null
	}
}