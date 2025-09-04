package com.example.ktor_test_client.api

import com.example.ktor_test_client.api.dtos.Album
import com.example.ktor_test_client.api.dtos.BaseAlbum
import com.example.ktor_test_client.api.dtos.BaseArtist
import com.example.ktor_test_client.api.dtos.BaseTrack
import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.api.endpoints.GetArtistResponse
import com.example.ktor_test_client.api.endpoints.getAlbum
import com.example.ktor_test_client.api.endpoints.getAlbumsFromArtist
import com.example.ktor_test_client.api.endpoints.getArtist
import com.example.ktor_test_client.api.endpoints.getArtistTopTracks
import com.example.ktor_test_client.api.endpoints.getRandomTrack
import com.example.ktor_test_client.api.endpoints.getRandomTrackId
import com.example.ktor_test_client.api.endpoints.getTopArtists
import com.example.ktor_test_client.api.endpoints.getTrack
import com.example.ktor_test_client.api.endpoints.toggleLike
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.client.request.url

suspend fun <T> safeRequest(request: suspend () -> T): Result<T> {
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

    suspend fun getAlbumsByArtist(artistId: String): Result<List<BaseAlbum>> =
        safeRequest {
            apiClient.getAlbumsFromArtist(artistId)!!.albums
        }

    suspend fun getRandomTrackId(): Result<String> = safeRequest {
        apiClient.getRandomTrackId()?.id!!
    }

    suspend fun getTrack(trackId: String): Result<Track> = safeRequest {
        apiClient.getTrack(trackId)!!
    }

    suspend fun getArtist(artistId: String): Result<GetArtistResponse> = safeRequest {
        apiClient.getArtist(artistId)!!
    }

    suspend fun getArtistTopTracks(artistId: String, limit: Int = 9): Result<List<BaseTrack>> = safeRequest {
        apiClient.getArtistTopTracks(artistId, limit)!!.tracks
    }

    suspend fun getTopArtists(): Result<List<BaseArtist>> = safeRequest {
        apiClient.getTopArtists()?.artists!!
    }

    suspend fun getSinglesByArtist(artistId: String): Result<List<BaseAlbum>> = safeRequest {
        val response = apiClient.httpClient.get {
            url("/api/v1/artists/$artistId/singles")
        }

        return@safeRequest response.body<List<BaseAlbum>>()
    }

    suspend fun postLike(trackId: String): Result<Boolean> = safeRequest {
        apiClient.toggleLike(trackId)?.liked!!
    }
}