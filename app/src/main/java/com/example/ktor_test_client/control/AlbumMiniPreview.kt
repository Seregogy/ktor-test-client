package com.example.ktor_test_client.control

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.ktor_test_client.api.dtos.BaseAlbum

@Composable
fun AlbumMiniPreview(
    onAlbumClicked: (artistId: String) -> Unit,
    album: BaseAlbum
) {
    Column(
        modifier = Modifier
            .width(180.dp)

    ) {
        AsyncImage(
            model = album.imageUrl,
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .fillMaxWidth()
                .aspectRatio(1f)
                .clickable {
                    onAlbumClicked(album.id)
                },
            contentDescription = "${album.name} image",
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = album.name,
            fontWeight = FontWeight.W800,
            fontSize = 22.sp,
            lineHeight = 16.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(end = 5.dp)
        )

        Text(
            text = album.artists.first().name,
            lineHeight = 14.sp
        )
    }
}