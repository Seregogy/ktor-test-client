package com.example.ktor_test_client.di

import android.annotation.SuppressLint
import android.util.Log
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import com.example.ktor_test_client.data.sources.RandomTrackDataSource
import com.example.ktor_test_client.viewmodels.AlbumViewModel
import com.example.ktor_test_client.viewmodels.ArtistViewModel
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import com.example.ktor_test_client.viewmodels.DefaultPlayerConfig
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

@SuppressLint("UnsafeOptInUsageError")
val viewModelDi = module {
    single<ExoPlayer> {
        val audioLoadControl = DefaultLoadControl.Builder()
            .setBackBuffer(DefaultPlayerConfig.backBufferMs, true)
            .setBufferDurationsMs(
                DefaultPlayerConfig.minBufferMs,
                DefaultPlayerConfig.maxBufferMs,
                DefaultPlayerConfig.bufferForPlaybackMs,
                DefaultPlayerConfig.bufferForPlaybackAfterRebuffedMs
            )
            .setTargetBufferBytes(DefaultPlayerConfig.targetBufferBytesSize)
            .build()

        Log.d("Dependency Injection", "ExoPlayer initialized")

        return@single ExoPlayer.Builder(get())
            .setLoadControl(audioLoadControl)
            .build()
    }

    single<AudioPlayerViewModel> { (mediaController: MediaController) ->
        AudioPlayerViewModel(
            repository = get(parameters = { parametersOf(get<RandomTrackDataSource>()) }),
            context = get(),
            mediaController = mediaController
        )
    }

    factory<AlbumViewModel> {
        AlbumViewModel(apiService = get())
    }

    factory<ArtistViewModel> { (artistId: String) ->
        ArtistViewModel(
            artistId = artistId,
            apiService = get()
        )
    }
}