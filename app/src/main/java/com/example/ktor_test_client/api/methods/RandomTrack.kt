package com.example.ktor_test_client.api.methods

import com.example.ktor_test_client.api.KtorAPI
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import com.example.ktor_test_client.api.dtos.Track

suspend fun KtorAPI.getRandomTrack(): Track? {
	val response = httpClient.get {
		url { host("/api/v1/tracks/random") }
	}

	println(response.status)
	println(response.bodyAsText())

	return if (response.status.isSuccess()) {
		response.body()
	} else {
		null
	}
}