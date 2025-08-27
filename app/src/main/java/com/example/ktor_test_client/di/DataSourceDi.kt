package com.example.ktor_test_client.di

import com.example.ktor_test_client.data.sources.PlaylistDataSource
import com.example.ktor_test_client.data.sources.RandomTrackDataSource
import com.example.ktor_test_client.data.sources.SingleTrackDataSource
import org.koin.dsl.module

val dataSourceDi = module {
    factory<PlaylistDataSource> { (tracksId: List<String>, firstTrack: Int) ->
        PlaylistDataSource(tracksId = tracksId, firstTrack = firstTrack)
    }

    factory<SingleTrackDataSource> { (id: String) ->
        SingleTrackDataSource(id)
    }

    factory<RandomTrackDataSource> {
        RandomTrackDataSource(apiService = get())
    }

}