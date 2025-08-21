package com.example.ktor_test_client.api

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

open class KtorAPI(
	private val defaultProtocol: URLProtocol = URLProtocol.Companion.HTTP,
	private val defaultHost: String = "95.31.212.185",
	private val defaultPort: Int = 7777,
	private val tokenHandler: TokenHandler
) {
	internal val endpoint = "${defaultProtocol.name}$defaultHost:$defaultPort"

	var accessToken = ""
	var refreshToken = ""

	init {
		if (tokenHandler.hasToken(TokenType.AccessToken).not()) {
			TODO("login request")
		}
	}

	internal val httpClient = HttpClient {
		install(ContentNegotiation.Plugin) {
			json(
				Json {
					prettyPrint = true
					ignoreUnknownKeys = true
				}
			)
		}

		defaultRequest {
			header("Authorization", "Bearer $accessToken")
		}
	}

	internal fun URLBuilder.host(endpointPath: String) {
		host = defaultHost
		protocol = defaultProtocol
		port = defaultPort

		path(endpointPath)
	}

	protected fun hostAsString(): String = "${defaultProtocol.name}://$endpoint:$defaultProtocol/"
}