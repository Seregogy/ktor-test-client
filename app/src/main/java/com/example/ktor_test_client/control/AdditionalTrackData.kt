package com.example.ktor_test_client.control

import android.R
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.rounded.Headset
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktor_test_client.api.dtos.Album
import com.example.ktor_test_client.api.dtos.BaseArtist
import com.example.ktor_test_client.api.dtos.TrackFullDto
import com.example.ktor_test_client.helper.formatNumber
import com.example.ktor_test_client.helper.toDate
import com.example.ktor_test_client.layout.TagsRow
import dev.jeziellago.compose.markdowntext.MarkdownText

data class AdditionalTrackData(
    val fullTitle: String?,
    val descriptionPreview: String?,
    val description: String?,
    val producers: List<String>?,
    val writers: List<String>?,
    val tags: List<String>?,
    val duration: Long?,
    val credits: Map<String, List<String>>?,
    val language: String?,
    val recordingLocation: String?,
    val releaseDate: Long?
)

val mockBlindingLightsTrackFullDto = TrackFullDto(
    id = "5QTxFnGygVM4jFQiBovmRo",
    name = "Blinding Lights",
    imageUrl = "https://i.scdn.co/image/ab67616d0000b2738863bc11d2aa12b54f5aeb36",
    indexInAlbum = 2,
    durationSeconds = 200, // 3:20
    hasLyrics = true,
    listening = 2847563921L,
    isExplicit = true,
    album = Album(
        id = "2yIp3poCeTI1PK1iqS8SpE",
        name = "After Hours",
        imageUrl = "https://i.scdn.co/image/ab67616d0000b2738863bc11d2aa12b54f5aeb36",
        artists = listOf(
            BaseArtist(
                id = "fkawjflj21k34j13",
                name = "The Weeknd"
            ),
        )
    )
)

private val mockAdditionalTrackData = AdditionalTrackData(
    fullTitle = "Blinding Lights",
    descriptionPreview = "\"Blinding Lights\" serves as the second single for The Weeknd's fourth studio album...",
    description = "\"Blinding Lights\" serves as the second single for The Weeknd's fourth studio album, [*After Hours*](/albums/The-weeknd/After-hours). It follows the record's lead single, [\"Heartless,\"](https://genius.com/The-weeknd-heartless-lyrics) which was released two days prior.\n\nThe track finds Abel in a constant state of distraction that he only gets relief from when in the presence of a significant other, as he sings over an up-tempo electropop instrumental that features large '80s-inspired synths and synthwave drums, similar to the sonic direction of his third studio album, [*Starboy.*](/albums/The-weeknd/Starboy) \n\n\"Blinding Lights\" was [first announced](https://www.youtube.com/watch?v=irVHy1SsNJ0&feature=youtu.be&fbclid=IwAR0gSwHDIMI-KGjzNXXmGgmW_7HxEqKr-6pSLtOfUlN_vWqDWrWsUF5LYx8) on November 24, 2019, in the trailer for a Mercedes-Benz advertisement. The [full commercial](https://www.youtube.com/watch?v=97eZHGbaGZQ) debuted minutes after the song's release and is heavily centered around it. In the ad, The Weeknd drives around in a Mercedes car as the track plays in the background. \n\nThe song leaked on November 26, 2019, a few days before its official release. Abel acknowledged the incident in [a tweet](https://twitter.com/theweeknd/status/1200146504058408961?s=20), where he confirmed its official release:\n\n> oh yah... and more new music tonight (in HQ\ud83d\ude09)\n\nOn January 21, 2020, the [music video](https://www.youtube.com/watch?v=4NRXx6U8ABQ) was released as a continuation of the visuals from [\"Heartless,\"](https://genius.com/The-weeknd-heartless-lyrics) starting when Abel stopped his manic run through Las Vegas. From there, Abel continues to rip through the city while under the influence of substances. He is seen going to a strip club and driving a Mercedes-Benz AMG-GT.\n\n\"Blinding Lights\" holds the [record](https://www.billboard.com/lists/the-weeknd-blinding-lights-top-100-top-chart-hits-21st-century/) for the longest stay on the ranking of all time for a soloist with a 57-week [run](https://www.billboard.com/lists/the-weeknd-blinding-lights-top-100-top-chart-hits-21st-century/) in the Hot 100's Top 10, including four weeks at #1, and almost 90 [weeks](https://www.billboard.com/artist/the-weeknd/) in the chart's Top 40. It [won](https://www.billboard.com/music/awards/the-weeknd-wins-top-artist-billboard-music-awards-9577047//) three _Billboard_ Music Awards for Top Hot 100 Song, Top Radio Song, and Top R&B Song. It was [nominated](https://www.billboard.com/music/awards/2021-kids-choice-awards-nominations-music-9519632/) for a Kids' Choice Award. At the 2020 MTV Video Music Awards, The Weeknd [took home](https://pitchfork.com/news/mtv-vmas-2020-the-weeknds-blinding-lights-wins-video-of-the-year/) Video of the Year and Best R&B Video \n\nIn November 2021, the synth-pop anthem [made](https://www.billboard.com/music/features/the-weeknd-blinding-lights-billboard-cover-story-2021-interview-1235001282/) history as the top _Billboard_ Hot 100 song of all time. The Weeknd [expressed](https://www.billboard.com/music/features/the-weeknd-blinding-lights-billboard-cover-story-2021-interview-1235001282/) his gratitude regarding this major milestone:\n> I don\u2019t think [the success of \"Blinding Lights\"] has hit me yet. I try not to dwell on it too much. I just count my blessings, and I\u2019m just grateful.\n\nIn 2024, \u201cBlinding Lights\u201d [became](https://www.billboard.com/music/music-news/the-weeknd-blinding-lights-4-billion-streams-spotify-1235580557/) the first Spotify track to pass the four billion stream mark. In January 2025, _Billboard_ [named](https://www.billboard.com/lists/the-weeknd-blinding-lights-top-100-top-chart-hits-21st-century/) it the #1 song of the 21st century.",
    producers = listOf("Max Martin", "Oscar Holter"),
    writers = listOf("The Weeknd", "Max Martin", "Oscar Holter"),
    tags = listOf("synthpop", "synthwave", "pop", "2019", "2010s", "electropop", "The Weeknd", "synth-pop", "2020s", "electropop", "The Weeknd", "synth-pop", "2020s"),
    duration = 200,
    credits = mapOf(
        "Video SFX Department" to listOf("Mathematic"),
        "Publisher" to listOf(
            "BOTA Publishing II",
            "New World Music",
            "Warner Music Group",
            "Kobalt Music",
            "Warner/Chappell",
            "Universal Music Group",
            "Wolf Cousins",
            "Sal and Co LP",
            "MXM Music",
            "KMR Music Royalties II SCSP"
        ),
        "Video Steadicam Operator" to listOf("Niels Lindelien"),
        "Video Graphic Designer" to listOf("Aleksi Tammi"),
        "Keyboards" to listOf("The Weeknd", "Oscar Holter", "Max Martin"),
        "Engineer" to listOf("Shin Kamiyama"),
        "Mixing Engineer" to listOf("John Hanes", "Șerban Ghenea"),
        "Distributor" to listOf("Universal Music Group"),
        "Label" to listOf("Republic Records", "XO Records"),
        "Copyright ©" to listOf("Universal Music Group", "Republic Records", "XO Records"),
        "Phonographic Copyright ℗" to listOf("Universal Music Group", "Republic Records", "XO Records"),
        "Video 3D Team" to listOf("Oscar Böckerman"),
        "Video Sound Designer" to listOf("Akseli Soini"),
        "Video Colorist" to listOf("Nicke Jacobsson"),
        "Video Post-Production Supervisor" to listOf("Alex Ernst"),
        "Video Editor" to listOf("Tim Montana", "Janne Vartia"),
        "Video 1st Assistant Director" to listOf("Kenneth Taylor"),
        "Video Production Designer" to listOf("Adam William Wilson"),
        "Video Key Grip" to listOf("Marlow Nunez"),
        "Video Gaffer" to listOf("Nizar Najm"),
        "Video Director Of Photography" to listOf("Devin \"Daddy\" Karringten", "Oliver Miller"),
        "Video Producer" to listOf("Sarah Park"),
        "Video Executive Producer" to listOf("Saskia Whinney"),
        "Video Production Company" to listOf("Somesuch"),
        "Video Director" to listOf("Anton Tammi"),
        "Recording Engineer" to listOf("John Hanes"),
        "Programmer" to listOf("The Weeknd", "Oscar Holter", "Max Martin"),
        "Mastering Engineer" to listOf("Kevin Peterson", "Dave Kutch"),
        "Guitar" to listOf("The Weeknd", "Oscar Holter", "Max Martin"),
        "Drums" to listOf("The Weeknd", "Oscar Holter", "Max Martin"),
        "Bass" to listOf("The Weeknd", "Oscar Holter", "Max Martin"),
        "Assistant Recording Engineer" to listOf("Sean Klein", "Jeremy Lertola", "Cory Bice")
    ),
    language = "English",
    recordingLocation = "Los Angeles, California",
    releaseDate = 1574974800
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewAdditionalTrackData() {
    var show by remember { mutableStateOf(false) }

    if (show) {
        ModalBottomSheet(
            onDismissRequest = { show = false },
            containerColor = Color.Black,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(20.dp)
                        .width(40.dp)
                        .height(3.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(Color.White.copy(.5f))
                )
            }
        ) {
            MockAdditionalTrackData(PaddingValues(top = 100.dp), Color.Transparent)
        }
    }
}
@Composable
fun MockAdditionalTrackData(
    padding: PaddingValues,
    imagePrimaryColor: Color
) {
    AdditionalTrackData(
        track = mockBlindingLightsTrackFullDto,
        additionalData = mockAdditionalTrackData,
        padding = padding,
        imagePrimaryColor = imagePrimaryColor
    )
}

@Composable
fun AdditionalTrackData(
    track: TrackFullDto,
    additionalData: AdditionalTrackData,
    padding: PaddingValues,
    imagePrimaryColor: Color
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    var descriptionExpanded by remember { mutableStateOf(false) }

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight - 100.dp)
                .background(
                    Brush.radialGradient(
                        listOf(imagePrimaryColor.copy(.3f), Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = 1500f
                    )
                )
        )

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp)
                .heightIn(max = screenHeight - 100.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            item {
                Text(
                    text = additionalData.fullTitle ?: track.name,
                    modifier = Modifier
                        .padding(top = 15.dp),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.W700
                )
            }

            item {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Headset,
                            contentDescription = "",
                            tint = Color.White.copy(.7f),
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            text = track.listening.formatNumber(),
                            fontWeight = FontWeight.W500,
                            color = Color.White.copy(.7f)
                        )
                    }

                    Text(
                        text = "${track.album.artists.joinToString(", ") { it.name }} • ${(track.durationSeconds / 60).toString().padStart(2, '0')}:${track.durationSeconds % 60}",
                        fontWeight = FontWeight.W500,
                        color = Color.White.copy(.7f)
                    )

                    Text(
                        text = additionalData.releaseDate?.toDate() ?: "",
                        fontWeight = FontWeight.W500,
                        color = Color.White.copy(.7f)
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(Color.White.copy(.07f))
                ) {
                    ContextMenuButton("Нравится", { })
                    HorizontalDivider(Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(.9f).alpha(.3f))

                    ContextMenuButton("Добавить в плейлист", { })
                    HorizontalDivider(Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(.9f).alpha(.3f))

                    ContextMenuButton("Скачать", { })
                    HorizontalDivider(Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(.9f).alpha(.3f))

                    ContextMenuButton("Текст", { })
                    HorizontalDivider(Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(.9f).alpha(.3f))

                    ContextMenuButton("Перейти к альбому", { })
                    HorizontalDivider(Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(.9f).alpha(.3f))

                    ContextMenuButton("Перейти к артисту", { })
                    HorizontalDivider(Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(.9f).alpha(.3f))

                    ContextMenuButton("Поделиться", { })
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .animateContentSize()
                        .clip(MaterialTheme.shapes.small)
                        .clickable {
                            descriptionExpanded = !descriptionExpanded
                        }
                        .background(Color.White.copy(.07f))
                        .padding(15.dp)
                ) {
                    MarkdownText(
                        markdown = if(!descriptionExpanded) additionalData.descriptionPreview ?: "" else additionalData.description ?: "",
                        syntaxHighlightColor = Color.White.copy(.07f),
                        style = TextStyle(
                            color = Color.White.copy(.7f),
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
                        )
                    )
                }
            }

            item {
                additionalData.tags?.let { tags ->
                    TagsRow(
                        horizontalSpace = 8.dp,
                        verticalSpace = 8.dp
                    ) {
                        tags.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White.copy(.07f))
                                    .clickable {
                                        //TODO: Поиск по тегу при нажатии
                                    }
                                    .padding(horizontal = 10.dp)
                                    .padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = "#${tag}",
                                    fontWeight = FontWeight.W500,
                                    color = Color.White.copy(.7f)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(25.dp))
            }
        }
    }
}

@Composable
private fun ContextMenuButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(vertical = 15.dp)
    ) {
        Text(
            text = text,
            color = Color.White.copy(.7f),
            fontWeight = FontWeight.W500
        )
    }
}