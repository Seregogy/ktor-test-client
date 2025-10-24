package com.example.ktor_test_client.controls

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.ktor_test_client.api.dtos.TrackFullDto
import com.example.ktor_test_client.controls.coloredscaffold.ColoredScaffoldState

@Composable
fun TrackControl(
    modifier: Modifier = Modifier,
    trackFullDto: TrackFullDto,
    onClick: (it: TrackFullDto) -> Unit = { },
    controls: @Composable RowScope.() -> Unit
) {
    val artistsNames = trackFullDto.album.artists.joinToString(",") { it.name }

    Row(
        modifier = modifier
            .clickable {
                onClick(trackFullDto)
            }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .weight(5f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            AsyncImage(
                model = trackFullDto.imageUrl,
                modifier = Modifier
                    .height(50.dp)
                    .aspectRatio(1f)
                    .fillMaxHeight()
                    .clip(MaterialTheme.shapes.small),
                contentDescription = "mini track image",
                contentScale = ContentScale.Crop
            )

            Column {
                MarqueeText(
                    text = trackFullDto.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W700,
                    color = Color.White,
                    maxLines = 1,
                    lineHeight = 18.sp,
                    textAlign = Alignment.CenterStart
                )

                MarqueeText(
                    text = artistsNames,
                    fontSize = 13.sp,
                    color = Color.White.copy(.7f),
                    lineHeight = 13.sp,
                    textAlign = Alignment.CenterStart
                )
            }
        }

        controls()
    }
}