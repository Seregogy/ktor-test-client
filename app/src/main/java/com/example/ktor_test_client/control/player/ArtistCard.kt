package com.example.ktor_test_client.control.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktor_test_client.api.dtos.BaseArtist
import com.example.ktor_test_client.control.coloredscaffold.ColoredScaffold
import com.example.ktor_test_client.control.coloredscaffold.rememberColoredScaffoldState
import com.example.ktor_test_client.viewmodel.ImagePaletteExtractor
import com.example.ktor_test_client.viewmodel.ImagePaletteViewModel

@Composable
fun ArtistCard(
    modifier: Modifier = Modifier,
    artist: BaseArtist,
    imagePaletteExtractor: ImagePaletteExtractor,
    onClick: (artist: BaseArtist) -> Unit
) {
    val density = LocalDensity.current

    val bitmap by imagePaletteExtractor.bitmap.collectAsState()
    val context = LocalContext.current

    var cardHeight by remember { mutableStateOf(0.dp) }
    var contentHeight by remember { mutableStateOf(0.dp) }

    LaunchedEffect(artist) {
        artist.imageUrl?.let {
            imagePaletteExtractor.fetchImageByUrl(context, it)
        }
    }

    ColoredScaffold(
        state = rememberColoredScaffoldState {
            imagePaletteExtractor.palette.collectAsState()
        }
    ) {
        Box(
            modifier = modifier
                .onSizeChanged {
                    cardHeight = with(density) {
                        it.height.toDp()
                    }
                }
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick(artist) }
                .background(Color.Black)
                .background(primaryOrBackgroundColor.value.copy(.2f))
        ) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                        .drawWithContent {
                            drawContent()

                            drawRect(
                                brush = Brush.verticalGradient(
                                    0f to Color.White,
                                    (cardHeight - contentHeight - 80.dp) / cardHeight to Color.White,
                                    (cardHeight - contentHeight + 50.dp) / cardHeight to Color.Transparent,
                                    1f to Color.Transparent
                                ),
                                blendMode = BlendMode.DstIn
                            )
                        },
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .onSizeChanged {
                        contentHeight = with(density) {
                            it.height.toDp()
                        }
                    }
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = artist.name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.W700,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )

                artist.about?.let {
                    Text(
                        text = artist.about,
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White.copy(.7f)
                    )
                }
            }
        }
    }
}

class ArtistParameterProvider : PreviewParameterProvider<Pair<BaseArtist, ImagePaletteExtractor>> {
    override val values: Sequence<Pair<BaseArtist, ImagePaletteExtractor>> = sequence {
        yield(BaseArtist(
            "", "Machine Gun Kelly",
            "Колсон Бейкер (англ. Colson Baker; 22 апреля 1990, Хьюстон, Техас), более известный под псевдонимом Machine Gun Kelly (Маши́н Ган Ке́лли) или MGK (ЭмДжиКей), — американский рэпер, рок-музыкант, певец, актёр. Свой псевдоним он взял в честь знаменитого гангстера Джорджа «Пулемёта» Келли. Получил известность после выпуска своих первых четырёх микстейпов: Stamp of Approval (2006), Homecoming (2008), 100 Words and Running (2010) и Lace Up (2010). В середине 2011 года он подписал контракт с компанией Young and Reckless Clothing. 14 декабря 2011 года был назван MTV Hottest Breakthrough MC 2011 года. 18 марта 2012 года он выиграл награду MTVu Breaking Woodie. В 2012 году его фотография появилась на обложке журнала XXL. По состоянию на август 2025 года, выпустил семь студийных альбомов.",
            "https://encrypted-tbn3.gstatic.com/licensed-image?q=tbn:ANd9GcRvpYhLU2gBTptjiCb3lvMm9KrUGoanoasSFr995IwwJZWym5KzoKJwODrMt_Z4kZvVsakwfHuBveVX4oU"
        ) to ImagePaletteExtractor())
        yield(BaseArtist(
            "", "The Weeknd",
            "Э́йбел Макко́нен Те́сфайе (англ. Abel Makkonen Tesfaye; род. 16 февраля 1990 года, Торонто, Онтарио, Канада), также известный как The Weeknd, — канадский певец, автор песен и актёр. Наиболее известен своим теноровым вокальным диапазоном, мрачной лирикой и альтернативным R&B-звучанием. Обладатель множества музыкальных наград, в том числе четырёх премий «Грэмми», двадцати премий Billboard Music Awards, восьми премий American Music Awards и трёх премий MTV Video Music Awards.",
            "https://i.redd.it/the-weeknd-high-quality-phone-wallpaper-1179x2556px-v0-dwplo8pjbjeb1.jpg?width=1179&format=pjpg&auto=webp&s=c7582347f3fa7924b3dfdb6e2705901faebc6622"
        ) to ImagePaletteExtractor())
        yield(BaseArtist(
            "", "Post Malone",
            "О́стин Ри́чард Пост (англ. Austin Richard Post; род. 4 июля 1995, Сиракьюс, Нью-Йорк, США), более известен как Post Malone — американский рэпер, певец и автор песен. Свой псевдоним он придумал просто — вбил в генератор рэп-имён своё имя и получилось Post Malone.",
            "https://i.redd.it/post-malone-4k-wallpapers-v0-y2egahdcxqne1.jpg?width=3840&format=pjpg&auto=webp&s=0cc4e23c6263ecff76e99c12981f42b3eec77308"
        ) to ImagePaletteExtractor())
    }
}

@Preview(showBackground = true, heightDp = 600, widthDp = 400)
@Composable
fun PreviewArtistCard(
    @PreviewParameter(ArtistParameterProvider::class) data: Pair<BaseArtist, ImagePaletteExtractor>
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ArtistCard(
            modifier = Modifier
                .size(320.dp, 420.dp),
            artist = data.first,
            imagePaletteExtractor = data.second
        ) { }
    }
}