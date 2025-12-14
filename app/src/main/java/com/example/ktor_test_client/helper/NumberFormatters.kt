package com.example.ktor_test_client.helper

import android.annotation.SuppressLint
import kotlin.math.absoluteValue

@SuppressLint("DefaultLocale")
fun Number.formatNumber(): String {
    return String.format("%,d", this).replace(",", " ")
}

@SuppressLint("DefaultLocale")
fun formatMinuteTimer(seconds: Int): String {
    return "${if(seconds < 0) "-" else ""}${(seconds.div(60).absoluteValue).toString().padStart(1, '0')}:${(seconds % 60).absoluteValue.toString().padStart(2, '0')}"
}