package com.example.ktor_test_client.helper

import androidx.compose.foundation.pager.PagerState
import kotlin.math.absoluteValue

fun PagerState.offsetForPage(page: Int) = (currentPage - page) + currentPageOffsetFraction

fun PagerState.indicatorOffsetForPage(page: Int) =  1f - offsetForPage(page).coerceIn(-1f, 1f).absoluteValue