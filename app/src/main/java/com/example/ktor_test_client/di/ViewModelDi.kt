package com.example.ktor_test_client.di

import android.annotation.SuppressLint
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import com.example.ktor_test_client.player.AudioPlayer
import com.example.ktor_test_client.player.MediaCache
import com.example.ktor_test_client.data.repositories.BaseNetworkRepository
import com.example.ktor_test_client.viewmodel.AlbumViewModel
import com.example.ktor_test_client.viewmodel.ArtistViewModel
import com.example.ktor_test_client.viewmodel.AudioPlayerViewModel
import org.koin.dsl.module

object DefaultPlayerConfig {
    var isAutoplay: Boolean = false

    var backBufferMs = 300_000

    var minBufferMs = 5_000
    var maxBufferMs = 300_000
    var bufferForPlaybackMs = 5_000
    var bufferForPlaybackAfterRebuffedMs = 5_000

    var targetBufferBytesSize = 64 * 1024 * 1024
}

@SuppressLint("UnsafeOptInUsageError")
val viewModelDi = module {
    single<ExoPlayer> {
        return@single ExoPlayer.Builder(get())
            .setLoadControl(
                DefaultLoadControl.Builder()
                    .setBackBuffer(DefaultPlayerConfig.backBufferMs, true)
                    .setBufferDurationsMs(
                        DefaultPlayerConfig.minBufferMs,
                        DefaultPlayerConfig.maxBufferMs,
                        DefaultPlayerConfig.bufferForPlaybackMs,
                        DefaultPlayerConfig.bufferForPlaybackAfterRebuffedMs
                    )
                    .setTargetBufferBytes(DefaultPlayerConfig.targetBufferBytesSize)
                    .build()
            )
            .build()
    }

    single<AudioPlayerViewModel> { (mediaController: MediaController) ->
        AudioPlayerViewModel(
            audioPlayer = AudioPlayer(
                mediaController = mediaController,
                mediaCache = MediaCache(),
                repository = get<BaseNetworkRepository>()
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