package com.example.ktor_test_client.helpers

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun formatNumber(number: Number): String {
    return String.format("%,d", number).replace(",", " ")
}

@SuppressLint("DefaultLocale")
fun formatMinuteTimer(seconds: Int): String {
    return "${(seconds.div(60)).toString().padStart(2, '0')}:${(seconds % 60).toString().padStart(2, '0')}"
}