package com.example.ktor_test_client.control.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ktor_test_client.R
import com.example.ktor_test_client.api.dtos.TrackFullDto
import com.example.ktor_test_client.control.MarqueeText
import com.example.ktor_test_client.control.coloredscaffold.ColoredScaffoldState

@Composable
fun ColoredScaffoldState.TopBar(
    modifier: Modifier = Modifier,
    currentTrackFullDto: TrackFullDto?,
    onCollapseRequest: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                onCollapseRequest()
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_down_icon),
                contentDescription = "",
                modifier = Modifier
                    .size(30.dp),
                tint = onBackgroundColorAnimated.value
            )
        }

        MarqueeText(
            text = "Плейлист \"${currentTrackFullDto?.album?.name ?: "unknown"}\"",
            fontWeight = FontWeight.W700,
            color = onBackgroundColorAnimated.value,
            maxLines = 1,
            textAlign = Alignment.Center,
            containerModifier = Modifier
                .weight(.6f)
        )


        IconButton(
            onClick = { }
        ) {
            Icon(
                painter = painterResource(R.drawable.queue_music_icon),
                contentDescription = "",
                tint = onBackgroundColorAnimated.value
            )
        }
    }
}