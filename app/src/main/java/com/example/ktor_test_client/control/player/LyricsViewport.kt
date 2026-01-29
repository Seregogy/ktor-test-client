package com.example.ktor_test_client.control.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ktor_test_client.control.coloredscaffold.ColoredScaffoldState
import com.example.ktor_test_client.viewmodel.AudioPlayerViewModel

@Composable
fun ColoredScaffoldState.LyricsViewport(
    viewModel: AudioPlayerViewModel
) {
    val density = LocalDensity.current
    val trackName by remember {
        derivedStateOf {
            viewModel.audioPlayer.currentTrack.value?.data?.name ?: "unknown"
        }
    }
    val lyrics by viewModel.audioPlayer.currentLyrics.collectAsStateWithLifecycle()

    when {
        lyrics == null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = onBackgroundColorAnimated.value
                )
            }
        }
        lyrics?.syncedText?.isNotEmpty() ?: false -> {
            var currentPosition by remember { viewModel.audioPlayer.currentPosition }
            val lazyListState = rememberLazyListState()
            val syncedTextPairs = remember {
                return@remember lyrics!!.syncedText!!.map { it.key to it.value.trim() }.toMutableList().apply {
                    add(Long.MAX_VALUE to "")
                }
            }
            val syncedTextSizes = remember { mutableMapOf<Long, IntSize>() }

            var columnSize by remember { mutableStateOf(IntSize.Zero) }
            var currentIndex by remember { mutableIntStateOf(-1) }

            LaunchedEffect(currentPosition) {
                currentIndex = findCurrentIndex(currentPosition, syncedTextPairs)
            }

            LaunchedEffect(currentIndex) {
                if (currentIndex in syncedTextPairs.indices) {
                    lazyListState.animateScrollToItem(currentIndex + 1,
                        -(columnSize.height / 2) + ((syncedTextSizes[syncedTextPairs[currentIndex].first]?.height ?: 0) / 2)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .onSizeChanged {
                        columnSize = it
                    },
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(70.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(Modifier.height(with(density) { columnSize.height.toDp() / 2 }))
                }

                itemsIndexed(
                    items = syncedTextPairs,
                    key = { _, item -> item.first }
                ) { index, item ->
                    Text(
                        text = item.second,
                        fontSize = 32.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.W700,
                        textAlign = TextAlign.Center,
                        color = if (currentIndex == index) bodyTextOnBackgroundAnimated.value else bodyTextOnBackgroundAnimated.value.copy(.2f),
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                viewModel.audioPlayer.seek(item.first)
                            },
                        onTextLayout = {
                            syncedTextSizes.put(item.first, it.size)
                        }
                    )
                }

                item {
                    Text(
                        "Lyrics provider ${lyrics?.provider} open library",
                        fontSize = 13.sp,
                        color = bodyTextOnBackgroundAnimated.value
                    )

                    Spacer(Modifier.height(100.dp))
                }
            }
        }
        lyrics?.plainText?.isNotEmpty() ?: false -> {
            Column(
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 100.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = trackName,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.W700,
                    color = bodyTextOnBackgroundAnimated.value
                )

                Text(
                    text = lyrics!!.plainText!!,
                    modifier = Modifier,
                    color = bodyTextOnBackgroundAnimated.value
                )

                Text(
                    "Lyrics provider ${lyrics?.provider} open library",
                    fontSize = 13.sp,
                    color = bodyTextOnBackgroundAnimated.value
                )
            }
        }
        else -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Error,
                        modifier = Modifier
                            .size(52.dp),
                        contentDescription = "Lyrics warning",
                        tint = onBackgroundColor.value
                    )

                    Text(
                        text = "Не удалось найти текст для этого трека",
                        color = onBackgroundColor.value,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun findCurrentIndex(position: Long, pairs: List<Pair<Long, String>>): Int {
    var low = 0
    var high = pairs.size - 2

    while (low <= high) {
        val mid = (low + high) / 2
        if (position in pairs[mid].first..pairs[mid + 1].first) {
            return mid
        } else if (position < pairs[mid].first) {
            high = mid - 1
        } else {
            low = mid + 1
        }
    }
    return -1
}