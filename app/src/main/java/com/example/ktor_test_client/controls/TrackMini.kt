package com.example.ktor_test_client.controls

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.ktor_test_client.api.dtos.BaseTrack
import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.helpers.times
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import kotlinx.coroutines.launch

@Composable
fun MiniTrack(
    modifier: Modifier = Modifier,
    track: BaseTrack = BaseTrack(),
    primaryColor: Color,
    indexPlusOne: Boolean = true,
    infiniteTransition: InfiniteTransition = rememberInfiniteTransition("infinity transition animation"),
    onClick: (it: BaseTrack) -> Unit = { }
) {
    val interactionSource = remember { MutableInteractionSource() }
    val coroutineScope = rememberCoroutineScope()

    val isCurrentlyPlay by remember {
        derivedStateOf {
            AudioPlayerViewModel.currentlyPlayTrackId.value == track.id
        }
    }

    val animatedValue by infiniteTransition.animateFloat(
        initialValue = .7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "animated_float_value"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick(track)
                coroutineScope.launch {
                    interactionSource.emit(PressInteraction.Press(Offset.Zero))
                }
            }
            .then(
                if (isCurrentlyPlay)
                    Modifier.background(primaryColor.copy(.3f))
                else
                    Modifier
            )
            .padding(horizontal = 20.dp)
            .padding(vertical = 5.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (isCurrentlyPlay) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .scale(animatedValue)
                        .clip(CircleShape)
                        .size(12.dp)
                        .background(primaryColor * 1.5f)
                )
            }
        } else {
            Text(
                text = (track.indexInAlbum.plus(
                    if(indexPlusOne) 1 else 0
                )).toString(),
                color = MaterialTheme.colorScheme.onBackground * .8f,
                fontWeight = FontWeight.W700,
                fontSize = 12.sp
            )
        }

        Text(
            text = track.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.W600,
            maxLines = 1,
            modifier = Modifier
                .padding(start = 25.dp)
                .basicMarquee()
        )

        IconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            onClick = {
                //TODO: контекстный bottom sheet
            }
        ) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = "dots"
            )
        }
    }

}

@Composable
fun TrackControl(
    modifier: Modifier = Modifier,
    track: Track = Track(imageUrl = "https://compote.slate.com/images/b3748bbb-6659-478c-b830-af8cf2ca0885.jpeg?crop=3108%2C2560%2Cx0%2Cy0"),
    foregroundColor: Color = MaterialTheme.colorScheme.onBackground,
    onClick: (it: Track) -> Unit = { }
) {
    val artistsNames = track.album.artists.joinToString(",") { it.name }

    Row(
        modifier = modifier
            .clickable {
                onClick(track)
            }
            .padding(10.dp),
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
}