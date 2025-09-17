package com.example.ktor_test_client.di

import android.annotation.SuppressLint
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import com.example.ktor_test_client.player.AudioPlayer
import com.example.ktor_test_client.player.DefaultPlayerConfig
import com.example.ktor_test_client.player.MediaCache
import com.example.ktor_test_client.data.repositories.BaseNetworkRepository
import com.example.ktor_test_client.viewmodels.AlbumViewModel
import com.example.ktor_test_client.viewmodels.ArtistViewModel
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
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

        return@single ExoPlayer.Builder(get())
            .setLoadControl(audioLoadControl)
            .build()
    }

    single<AudioPlayerViewModel> { (mediaController: MediaController) ->
        AudioPlayerViewModel(
            audioPlayer = AudioPlayer(
                mediaController = mediaController,
                mediaCache = MediaCache(),
                repository = get<BaseNetworkRepository>(),
                context = get()
            ),
            context = get()
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