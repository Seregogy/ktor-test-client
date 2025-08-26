package com.example.ktor_test_client.api

import com.example.ktor_test_client.api.dtos.Album
import com.example.ktor_test_client.api.dtos.Artist
import com.example.ktor_test_client.api.dtos.BaseAlbum
import com.example.ktor_test_client.api.dtos.BaseArtist
import com.example.ktor_test_client.api.dtos.BaseTrack
import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.api.methods.GetAlbumsByArtistResponse
import com.example.ktor_test_client.api.methods.GetArtistResponse
import com.example.ktor_test_client.api.methods.GetArtistTopTracksResponse
import com.example.ktor_test_client.api.methods.getAlbum
import com.example.ktor_test_client.api.methods.getAlbumsFromArtist
import com.example.ktor_test_client.api.methods.getArtist
import com.example.ktor_test_client.api.methods.getArtistTopTracks
import com.example.ktor_test_client.api.methods.getRandomTrack
import com.example.ktor_test_client.api.methods.getRandomTrackId
import com.example.ktor_test_client.api.methods.getTopArtists
import com.example.ktor_test_client.api.methods.getTrack
import com.example.ktor_test_client.api.methods.toggleLike

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

    suspend fun like(trackId: String): Result<Boolean> = safeRequest {
        apiClient.toggleLike(trackId)?.liked!!
    }
}