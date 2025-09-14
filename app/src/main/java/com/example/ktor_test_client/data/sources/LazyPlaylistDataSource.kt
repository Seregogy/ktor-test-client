package com.example.ktor_test_client.data.sources

import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.data.providers.DataProvider
import com.example.ktor_test_client.data.repositories.Repository
import kotlinx.coroutines.runBlocking

class LazyPlaylistDataSource(
    var tracksId: List<String>,
    firstTrack: Int = 0
) : DataSource() {
    override var currentIndex: Int = firstTrack

    var tracks: MutableList<Lazy<Pair<MediaItem?, Track?>>>? = null

    var onNext: (index: Int) -> Unit = { }
    var onCurrent: (index: Int) -> Unit = { }
    var onPrev: (index: Int) -> Unit = { }

    override suspend fun nextTrack(dataProvider: DataProvider): Track? {
        if (tracks == null) {
            initTracks(dataProvider)
        }

        currentIndex = (currentIndex + 1) % tracks!!.size

        tracks!![currentIndex].value
        onNext(currentIndex)

        tracks?.forEach {
            Log.d("ASS", it.toString())
        }
        return tracks!![currentIndex].value.second
    }

    override suspend fun currentTrack(dataProvider: DataProvider): Track? {
        if (tracks == null)
            initTracks(dataProvider)

        tracks!![currentIndex].value

        onCurrent(currentIndex)
        tracks?.forEach {
            Log.d("ASS", it.toString())
        }
        return tracks!![currentIndex].value.second
    }

    override suspend fun previousTrack(dataProvider: DataProvider): Track? {
        if (tracks == null)
            initTracks(dataProvider)

        currentIndex = (currentIndex - 1).coerceIn(tracks!!.indices)

        tracks!![currentIndex].value
        onPrev(currentIndex)

        tracks?.forEach {
            Log.d("ASS", it.toString())
        }
        return tracks!![currentIndex].value.second
    }

    fun initTracks(dataProvider: DataProvider) {
        tracks = tracksId.map {
            lazy {
                runBlocking {
                    dataProvider.getTrack(it)?.also { track ->
                        val artistsName = track.album.artists.joinToString(",") { it.name }

                        return@runBlocking MediaItem.Builder()
                            .setUri(Uri.parse(track.audioUrl))
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setAlbumArtist(artistsName)
                                    .setTitle(track.name)
                                    .setTrackNumber(track.indexInAlbum)
                                    .setArtworkUri(Uri.parse(track.imageUrl))
                                    .setAlbumArtist(artistsName)
                                    .setAlbumTitle(track.album.name)
                                    .setDisplayTitle(track.name)
                                    .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                                    .build()
                            )
                            .build() to track
                    }

                    return@runBlocking null to null
                }
            }
        }.toMutableList()

        tracks!![currentIndex].value
    }
}