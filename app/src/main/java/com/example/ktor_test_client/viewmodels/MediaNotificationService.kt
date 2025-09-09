package com.example.ktor_test_client.viewmodels

import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.Player.COMMAND_SEEK_TO_NEXT
import androidx.media3.common.Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM
import androidx.media3.common.Player.COMMAND_SEEK_TO_PREVIOUS
import androidx.media3.common.Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

class MediaNotificationService : MediaSessionService() {
    private lateinit var mediaSession: MediaSession
    private lateinit var player: ExoPlayer

    private val favoriteCommand = SessionCommand(ACTIVITY_SERVICE, Bundle.EMPTY)
    private val unfavoriteCommand = SessionCommand(ACCOUNT_SERVICE, Bundle.EMPTY)

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        val favoriteButton =
            CommandButton.Builder(CommandButton.ICON_HEART_UNFILLED)
                .setDisplayName("Save to favorites")
                .setSessionCommand(favoriteCommand)
                .build()

        val unfavoriteButton =
            CommandButton.Builder(CommandButton.ICON_MINUS_CIRCLE_UNFILLED)
                .setDisplayName("Save to unfavorites")
                .setSessionCommand(unfavoriteCommand)
                .build()

        player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player)
            .setCallback(object : MediaSession.Callback {
                override fun onConnect(
                    session: MediaSession,
                    controller: MediaSession.ControllerInfo
                ): ConnectionResult {
                    return ConnectionResult.AcceptedResultBuilder(session)
                        .setMediaButtonPreferences(
                            ImmutableList.of(
                                unfavoriteButton,
                                favoriteButton
                            ))
                        .setAvailableSessionCommands(
                            ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                                .add(unfavoriteCommand)
                                .add(favoriteCommand)
                                .build()
                        )
                        .build()
                }

                override fun onCustomCommand(
                    session: MediaSession,
                    controller: MediaSession.ControllerInfo,
                    customCommand: SessionCommand,
                    args: Bundle
                ): ListenableFuture<SessionResult> {
                    when(customCommand.customAction) {
                        "SET_FAVORITE" -> Log.d("Media Session", "SET FAVORITE ACTION")
                        "UNSET_FAVORITE" -> Log.d("Media Session", "UNSET FAVORITE ACTION")
                    }

                    return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }
            })
            .setMediaButtonPreferences(ImmutableList.of(
                favoriteButton, unfavoriteButton
            ))
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaSession.run {
            player.release()
            release()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }
}