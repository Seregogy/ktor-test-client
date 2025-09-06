package com.example.ktor_test_client.controls

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.ktor_test_client.api.dtos.Track

@Composable
fun TrackControl(
    modifier: Modifier = Modifier,
    track: Track,
    foregroundColor: Color = MaterialTheme.colorScheme.onBackground,
    onClick: (it: Track) -> Unit = { },
    controls: @Composable () -> Unit
) {
    val artistsNames = track.album.artists.joinToString(",") { it.name }

    Row(
        modifier = modifier
            .clickable {
                onClick(track)
            }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            AsyncImage(
                model = track.imageUrl,
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(5.dp)),
                contentDescription = "mini track image",
                contentScale = ContentScale.Crop
            )

            Column {
                Text(
                    text = track.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W700,
                    color = foregroundColor,
                    maxLines = 1,
                    modifier = Modifier
                        .basicMarquee(),
                    lineHeight = 18.sp
                )
                Text(
                    text = artistsNames,
                    fontSize = 13.sp,
                    color = foregroundColor,
                    lineHeight = 13.sp
                )
            }
        }

        controls()
    }
}