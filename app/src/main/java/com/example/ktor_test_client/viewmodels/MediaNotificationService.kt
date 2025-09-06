package com.example.ktor_test_client.viewmodels

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class MediaNotificationService : MediaSessionService() {
    private lateinit var mediaSession: MediaSession
    private lateinit var player: ExoPlayer

    override fun onCreate() {
        player = ExoPlayer.Builder(this).build()

        mediaSession = MediaSession.Builder(this, player).build()

        super.onCreate()
    }

    override fun onDestroy() {
        mediaSession.run {
            player.release()
            release()
        }

        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }
}