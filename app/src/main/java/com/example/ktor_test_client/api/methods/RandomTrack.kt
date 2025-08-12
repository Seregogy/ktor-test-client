package com.example.ktor_test_client.api.methods

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import com.example.ktor_test_client.api.KtorAPI

@Serializable
data class Track(
	val id: String = "",
	val name: String = "unknown",
	val durationSeconds: Int = 0,
	val lyrics: String? = "",
	val indexInAlbum: Int = 0,
	val listening: Int? = 0,
	val isExplicit: Boolean? = false,
	val audioUrl: String = ""
)

@Serializable
data class Album(
	val id: String = "",
	val name: String = "",
	val imageUrl: String = "",
)

@Serializable
data class Artist(
	val id: String = "",
	val name: String = "unknown artist",
	val imageUrl: String? = ""
)

@Serializable
data class RandomTrackResponse(
	val track: Track = Track(),
	val album: Album = Album(),
	val artist: List<Artist> = listOf()
)

suspend fun KtorAPI.getRandomTrack() : RandomTrackResponse? {
	val response = httpClient.get {
		url { host("/api/v1/tracks/random") }
	}

	return if (response.status.isSuccess()) {
		response.body()
	} else {
		null
	}
}