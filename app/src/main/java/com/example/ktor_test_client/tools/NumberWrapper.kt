package com.example.ktor_test_client.tools

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun formatNumber(number: Number): String {
    return String.format("%,d", number).replace(",", " ")
}