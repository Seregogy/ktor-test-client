package com.example.ktor_test_client.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class ImagePaletteViewModel : ViewModel() {
    private val _bitmap: MutableStateFlow<Bitmap?> = MutableStateFlow(null)
    val bitmap: StateFlow<Bitmap?> = _bitmap.asStateFlow()

    private val _palette: MutableStateFlow<Palette?> = MutableStateFlow(null)
    val palette: StateFlow<Palette?> = _palette.asStateFlow()

    suspend fun fetchImageByUrl(
        context: Context,
        imageUrl: String
    ) {
        _bitmap.value = ImageLoader(context).execute(
            ImageRequest.Builder(context)
                .data(imageUrl)
                .build()
        ).image!!.toBitmap()

        tryExtractPaletteFromCurrentBitmap()
    }

    private fun tryExtractPaletteFromCurrentBitmap() {
        _bitmap.value?.let { extractPalette(it) }
    }

    private fun extractPalette(
        bitmap: Bitmap
    ) {
        _palette.value = Palette.from(bitmap.copy(Bitmap.Config.ARGB_8888, false)).generate()
    }
}