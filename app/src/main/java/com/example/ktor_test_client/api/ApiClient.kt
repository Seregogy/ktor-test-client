package com.example.ktor_test_client.api

import android.content.Context
import android.util.Log
import com.example.ktor_test_client.api.tools.InternetConnectionChecker
import com.example.ktor_test_client.api.tools.TokenHandler
import com.example.ktor_test_client.api.tools.TokenType
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

open class ApiClient(
    private val context: Context,
    private val defaultProtocol: URLProtocol = URLProtocol.Companion.HTTPS,
    private val defaultHost: String = "onewave.duckdns.org",
    private val defaultPort: Int = 443,
    private val tokenHandler: TokenHandler
) {
    private val connectionChecker = InternetConnectionChecker(context)

    var accessToken = ""
    var refreshToken = ""

    init {
        if (tokenHandler.hasToken(TokenType.AccessToken).not()) {
            TODO("login request")
        }
    }

    internal val httpClient = HttpClient {
        install(HttpCache)

        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                }
            )
        }

        install(HttpRequestRetry) {
            maxRetries = 5
            retryOnExceptionIf { _, _ ->
                connectionChecker.isConnected.value.not()
            }
            delayMillis { retry ->
                Log.d("API", "resending request #$retry")

                retry * 3000L
            }
        }

        defaultRequest {
            url {
                host = defaultHost
                protocol = defaultProtocol
            }
            header("Authorization", "Bearer $accessToken")
        }

        expectSuccess = false

        HttpResponseValidator {
            handleResponseExceptionWithRequest { cause, request ->
                Log.e("API", "${request.url}, ${cause.message}")
            }
        }
    }
}