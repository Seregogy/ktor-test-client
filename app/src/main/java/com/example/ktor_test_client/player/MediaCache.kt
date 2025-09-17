package com.example.ktor_test_client.player

import android.util.Log
import androidx.collection.LruCache

class MediaCache {
    private companion object {
        val lruCache: LruCache<String, Track> = LruCache(maxSize = 100)
    }

    fun putAll(tracks: List<Track>) {
        tracks.forEach {
            put(it)
        }

        Log.d("Cache", tracks.joinToString { it.data.name })
    }

    fun put(track: Track) {
        lruCache.put(track.data.id, track)
    }

    fun loadFromCache(trackIds: List<String>): List<Track> = trackIds.mapNotNull {
        lruCache[it]
    }
}
