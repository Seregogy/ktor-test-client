package com.example.ktor_test_client.helper

import java.text.SimpleDateFormat

fun Long.toDate(format: String = "dd MMMM yyyy"): String {
    return SimpleDateFormat(format).format(this * 1000L)
}