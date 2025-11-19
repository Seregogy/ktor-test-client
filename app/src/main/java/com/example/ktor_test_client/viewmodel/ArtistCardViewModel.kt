package com.example.ktor_test_client.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.palette.graphics.Palette
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ArtistCardViewModel {
    private val innerBitmapState: MutableStateFlow<Bitmap?> = MutableStateFlow(null)
    val bitmap: StateFlow<Bitmap?> = innerBitmapState.asStateFlow()

    private val innerPaletteState: MutableStateFlow<Palette?> = MutableStateFlow(null)
    val palette: StateFlow<Palette?> = innerPaletteState.asStateFlow()

    suspend fun fetchImageByUrl(
        context: Context,
        imageUrl: String
    ) {
        innerBitmapState.value = ImageLoader(context).execute(
            ImageRequest.Builder(context)
                .data(imageUrl)
                .build()
        ).image!!.toBitmap()

        tryExtractPaletteFromCurrentBitmap()
    }

    fun tryExtractPaletteFromCurrentBitmap() = innerBitmapState.value?.let { extractPalette(it) }

    fun extractPalette(
        bitmap: Bitmap
    ) {
        innerPaletteState.value = Palette.from(bitmap.copy(Bitmap.Config.ARGB_8888, false)).generate()
    }
}