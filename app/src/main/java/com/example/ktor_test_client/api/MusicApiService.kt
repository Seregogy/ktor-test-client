package com.example.ktor_test_client.api

import com.example.ktor_test_client.api.dtos.Album
import com.example.ktor_test_client.api.dtos.BaseAlbum
import com.example.ktor_test_client.api.dtos.BaseArtist
import com.example.ktor_test_client.api.dtos.BaseTrack
import com.example.ktor_test_client.api.dtos.BaseTrackWithArtists
import com.example.ktor_test_client.api.dtos.Lyrics
import com.example.ktor_test_client.api.dtos.TrackFullDto
import com.example.ktor_test_client.api.methods.GetArtistResponse
import com.example.ktor_test_client.api.methods.GetLastReleaseByArtistResponse
import com.example.ktor_test_client.api.methods.GetReleasesByArtistsResponse
import com.example.ktor_test_client.api.methods.GetSinglesByArtistsResponse
import com.example.ktor_test_client.api.methods.getAlbum
import com.example.ktor_test_client.api.methods.getAlbumsByArtist
import com.example.ktor_test_client.api.methods.getArtist
import com.example.ktor_test_client.api.methods.getTopTracksByArtist
import com.example.ktor_test_client.api.methods.getRandomTrack
import com.example.ktor_test_client.api.methods.getRandomTrackId
import com.example.ktor_test_client.api.methods.getTopArtists
import com.example.ktor_test_client.api.methods.getTrack
import com.example.ktor_test_client.api.methods.toggleLike
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

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
    suspend fun getRandomTrack(): Result<TrackFullDto> = safeRequest {
        apiClient.getRandomTrack()!!
    }

    suspend fun getAlbum(albumId: String): Result<Album> = safeRequest {
        apiClient.getAlbum(albumId)!!
    }

    suspend fun getTracksByAlbum(albumId: String): Result<List<BaseTrack>> = safeRequest {
        apiClient.httpClient
            .get("/api/v1/albums/$albumId/tracks").call.body<Map<String, List<BaseTrack>>>()["tracks"]!!
    }

    suspend fun getAlbumsByArtist(artistId: String): Result<List<BaseAlbum>> =
        safeRequest {
            apiClient.getAlbumsByArtist(artistId)!!.albums
        }

    suspend fun getRandomTrackId(): Result<String> = safeRequest {
        apiClient.getRandomTrackId()?.id!!
    }

    suspend fun getTrack(trackId: String): Result<TrackFullDto> = safeRequest {
        apiClient.getTrack(trackId)!!
    }

    @Serializable
    data class GetTracksRequest(
        val tracks: List<String>
    )

    @Serializable
    data class GetTracksResponse(
        val tracks: List<TrackFullDto>
    )

    suspend fun getTracks(tracksId: List<String>): Result<List<TrackFullDto>> = safeRequest {
        apiClient.httpClient
            .post("/api/v1/tracks") {
                contentType(ContentType.Application.Json)
                setBody(GetTracksRequest(tracksId))
            }.body<GetTracksResponse>().tracks
    }

    suspend fun getArtist(artistId: String): Result<GetArtistResponse> = safeRequest {
        apiClient.getArtist(artistId)!!
    }

    suspend fun getArtistTopTracks(artistId: String, limit: Int = 9): Result<List<BaseTrackWithArtists>> = safeRequest {
        apiClient.getTopTracksByArtist(artistId, limit)!!.tracks
    }

    suspend fun getTopArtists(): Result<List<BaseArtist>> = safeRequest {
        apiClient.getTopArtists(10)?.artists!!
    }

    suspend fun getSinglesByArtist(artistId: String): Result<List<BaseAlbum>> = safeRequest {
        apiClient.httpClient.get {
            url("/api/v1/artists/$artistId/singles")
        }.body<GetSinglesByArtistsResponse>().singles
    }

    suspend fun getReleasesByArtist(artistId: String): Result<List<BaseAlbum>> = safeRequest {
        apiClient.httpClient.get {
            url("/api/v1/artists/$artistId/releases")
        }.body<GetReleasesByArtistsResponse>().releases
    }

    suspend fun getLyrics(trackId: String): Result<Lyrics> = safeRequest {
        apiClient.httpClient.get {
            url("/api/v1/lyrics/$trackId")
        }.body()
    }

    suspend fun getLastReleaseByArtist(artistId: String): Result<Pair<BaseAlbum, Long>> = safeRequest {
        val response = apiClient.httpClient.get {
            url("/api/v1/artists/$artistId/albums/latest")
        }

        val lastRelease = response.body<GetLastReleaseByArtistResponse>()
        return@safeRequest lastRelease.lastAlbum to lastRelease.releaseDate
    }

    suspend fun like(trackId: String): Result<Boolean> = safeRequest {
        apiClient.toggleLike(trackId)?.liked!!
    }
}