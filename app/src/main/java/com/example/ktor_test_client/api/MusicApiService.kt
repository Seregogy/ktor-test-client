package com.example.ktor_test_client.api

import com.example.ktor_test_client.api.dtos.Album
import com.example.ktor_test_client.api.dtos.Artist
import com.example.ktor_test_client.api.dtos.BaseArtist
import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.api.methods.GetAlbumsByArtistResponse
import com.example.ktor_test_client.api.methods.getAlbum
import com.example.ktor_test_client.api.methods.getAlbumsFromArtist
import com.example.ktor_test_client.api.methods.getRandomTrack
import com.example.ktor_test_client.api.methods.getRandomTrackId
import com.example.ktor_test_client.api.methods.getTopArtists
import com.example.ktor_test_client.api.methods.getTrack
import com.example.ktor_test_client.api.methods.toggleLike

suspend fun <T>safeRequest(request: suspend () -> T): Result<T> {
    return try {
        Result.success(request())
    } catch (exc: Exception) {
        Result.failure(exc)
    }
}

class MusicApiService(
    private val apiClient: ApiClient
) {
    suspend fun getRandomTrack(): Result<Track> = safeRequest {
        apiClient.getRandomTrack()!!
    }

    suspend fun getAlbum(albumId: String): Result<Album> = safeRequest {
        apiClient.getAlbum(albumId)!!
    }

    suspend fun getAlbumsByArtist(artistId: String): Result<GetAlbumsByArtistResponse> = safeRequest {
        apiClient.getAlbumsFromArtist(artistId)!!
    }

    suspend fun getRandomTrackId(): Result<String> = safeRequest {
        apiClient.getRandomTrackId()?.id!!
    }

    suspend fun getTrack(trackId: String): Result<Track> = safeRequest {
        apiClient.getTrack(trackId)!!
    }

    /*suspend fun getArtist(artistId: String): Result<Artist> {
        apiClient.getArtist(artistId)!!
    }*/

    suspend fun getTopArtists(): Result<List<BaseArtist>> = safeRequest {
        apiClient.getTopArtists()?.artists!!
    }

    suspend fun like(trackId: String): Result<Boolean> = safeRequest {
        apiClient.toggleLike(trackId)?.liked!!
    }
}