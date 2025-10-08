package com.example.ktor_test_client.helpers

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import kotlin.collections.List

val Player.mediaItems: List<MediaItem>
    get() = List(this.mediaItemCount) {
        this.getMediaItemAt(it)
    }
