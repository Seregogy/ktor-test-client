package com.example.ktor_test_client.controls

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.ktor_test_client.api.dtos.Track

@Composable
@Preview(showBackground = true)
fun MiniTrack(
    modifier: Modifier = Modifier,
    track: Track = Track(imageUrl = "https://compote.slate.com/images/b3748bbb-6659-478c-b830-af8cf2ca0885.jpeg?crop=3108%2C2560%2Cx0%2Cy0"),
    foregroundColor: Color = MaterialTheme.colorScheme.onBackground,
    onClick: (it: Track) -> Unit = { }
) {
    val artistsNames = track.artists.joinToString(",") { it.name }

    Row(
        modifier = modifier
            .clickable {
                onClick(track)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        AsyncImage(
            model = track.imageUrl,
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxHeight()
                .padding(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            contentDescription = "mini track image",
            contentScale = ContentScale.Crop
        )

        Column {
            Text(
                text = track.name ?: "Unknown",
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
}