package com.example.ktor_test_client.api.tools

enum class TokenType {
	AccessToken,
	RefreshToken
}

interface TokenHandler {
	fun saveToken(type: TokenType, token: String)
	fun loadToken(type: TokenType): String
	fun hasToken(type: TokenType): Boolean
}