package com.example.ktor_test_client.di

import com.example.ktor_test_client.api.ApiClient
import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.api.tools.TokenHandler
import com.example.ktor_test_client.api.tools.TokenType
import org.koin.dsl.module

val apiServiceDi = module {
    single<MusicApiService> {
        MusicApiService(apiClient = get())
    }
}

val apiClientDi = module {
    single<ApiClient> {
        ApiClient(
            context = get(),
            tokenHandler = get()
        )
    }
}

val tokenHandlerDi = module {
    single<TokenHandler> {
        object : TokenHandler {
            override fun saveToken(type: TokenType, token: String) { }
            override fun loadToken(type: TokenType): String { return "" }
            override fun hasToken(type: TokenType): Boolean { return true }
        }
    }
}