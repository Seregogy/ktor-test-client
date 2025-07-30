package com.example.ktor_test_client

import androidx.compose.ui.graphics.Color
import com.example.ktor_test_client.models.Album
import com.example.ktor_test_client.models.Artist
import com.example.ktor_test_client.models.Track
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

object Library {
    val artists = listOf(
        Artist(
            id = 2,
            name = "twenty one pilots",
            listeningInMonth = 2_198_867,
            likes = 1_739_838,
            albums = listOf(),
            bestTracks = listOf(),
            imagesUrl = listOf(
                "https://the-flow.ru/uploads/images/catalog/element/665087c3320df.png" to Color(162, 48, 44)
            )
        ),
        Artist(
            id = 0,
            name = "Post Malone",
            about = "American raper, producer",
            listeningInMonth = 1_301_945,
            likes = 1_052_006,
            imagesUrl = listOf(
                "https://www.soyuz.ru/public/uploads/files/2/7390868/2019091211303003a5833f99.jpg" to Color(150, 159, 170),
                "https://cdn-image.zvuk.com/pic?type=artist&id=3289907&size=medium&hash=bbdb7895-d42f-40ca-801c-540fc2bc7f2c" to Color(140, 140, 140)
            )
        )
    )

    val albums = listOf(
        Album(
            id = 0,
            artistId = 0,
            name = "Stoney",
            likes = 2661,
            tracksId = listOf(),
            bestTracks = listOf(),
            totalListening = 1000000,
            releaseDate = 1752758049,
            imageUrl = "https://m.media-amazon.com/images/I/91VAjJ6YxrL._UF1000,1000_QL80_.jpg",
            primaryColor = Color(230, 90, 48)
        ),
        Album(
            id = 2,
            artistId = 2,
            "Scaled And Icy",
            likes = 11_342,
            releaseDate = 1627302212,
            label = "Fueled By Ramen",
            primaryColor = Color(119, 170, 176),
            imageUrl = "https://upload.wikimedia.org/wikipedia/ru/c/c8/Twenty_One_Pilots_-_Scaled_And_Icy.jpg"
        )
    )

    val tracks = listOf(
        Track(
            id = 1,
            artistsId = listOf(0),
            albumId = 0,
            name = "Go Flex",
            seconds = 179,
            lyrics = "Lighting stog after stog, choke on the smoke\n" +
                    "They tell me to quit, don't listen what I'm told\n" +
                    "Help me forget that this world is so cold\n" +
                    "I don't even know what I'm chasin' no more\n" +
                    "Tell me what I want, just keep searchin' on\n" +
                    "It's never enough, cup after cup, blunt after blunt\n" +
                    "I wouldn't give one if I could find a fuck, ha, ha, ha\n" +
                    "In the cut and I put that on my momma\n" +
                    "And my bitch called talkin' 'bout some drama\n" +
                    "I swear there ain't no time for women on the come up\n" +
                    "It's either the pussy or the commas\n" +
                    "Man I just wanna go flex\n" +
                    "Gold on my teeth and on my neck\n" +
                    "And I'm stone cold with the flex\n" +
                    "With my squad and I'm smokin' up a check\n" +
                    "Man I just wanna go flex\n" +
                    "Gold on my teeth and on my neck\n" +
                    "And I'm stone cold with the flex\n" +
                    "With my squad and I'm smokin' up a check\n" +
                    "Push the gas, get a motherfuckin' nose bleed\n" +
                    "She ain't never met a youngin' do it like me\n" +
                    "She got a man but says she really like me\n" +
                    "She doin' things to excite me\n" +
                    "She sending all her friends snaps of my new tracks\n" +
                    "'Cause all these hoes know what's about to come next\n" +
                    "I hit my plug up, got the paper connect\n" +
                    "I drop a couple bands I just wanna go\n" +
                    "Man I just wanna go flex\n" +
                    "Gold on my teeth and on my neck\n" +
                    "And I'm stone cold with the flex\n" +
                    "With my squad and I'm smokin' up a check\n" +
                    "Man I just wanna go flex\n" +
                    "Gold on my teeth and on my neck\n" +
                    "And I'm stone cold with the flex\n" +
                    "With my squad and I'm smokin' up a check\n" +
                    "Knowing all of this\n" +
                    "Just don't make a difference\n" +
                    "I'm just talking shit to the ones that'll listen\n" +
                    "I came with the heat man, I swear I'm never missing\n" +
                    "And I'm still the same and I swear I'm never switching\n" +
                    "Knowing all of this\n" +
                    "It just don't make a difference\n" +
                    "I'm just talking shit to the ones that'll listen\n" +
                    "I came with the heat man, I swear I'm never missing\n" +
                    "And I'm still the same and I swear I'm never switching\n" +
                    "Man I just wanna go flex\n" +
                    "Gold on my teeth and on my neck\n" +
                    "And I'm stone cold with the flex\n" +
                    "With my squad and I'm smokin' up a check\n" +
                    "Man I just wanna go flex\n" +
                    "Gold on my teeth and on my neck\n" +
                    "And I'm stone cold with the flex\n" +
                    "With my squad and I'm smokin' up a check\n" +
                    "Man I just wanna go flex\n" +
                    "Gold on my teeth and on my neck\n" +
                    "And I'm stone cold with the flex\n" +
                    "So cold with the flex"
        ),
        Track(
            id = 2,
            albumId = 2,
            name = "The Outside",
            seconds = 216,
            artistsId = listOf(2)
        )
    )
}

class MusicRepository @Inject constructor(
    val dataProvider: DataProvider
) {
    fun getByName(name: String): TrackModel? {
        return dataProvider.getAll().firstOrNull { it.name == name }
    }

    fun getAll(): List<TrackModel> {
        return dataProvider.getAll()
    }

    fun getMultipleByNames(names: Array<String>): List<TrackModel> {
        return dataProvider.getAll().filter {
            for (currentName in names)
                if (it.name == currentName)
                    return@filter true

            return@filter false
        }
    }
}

data class TrackModel(
    val name: String,
    val artistName: String,
    val label: String
)

@Module
@InstallIn(SingletonComponent::class)
object DataProviderModule {

    @Singleton
    @Provides
    fun createDataProvider() = LocalDataProvider()
}

interface DataProvider {
    fun getAll(): List<TrackModel>
}

class LocalDataProvider : DataProvider {
    override fun getAll() = listOf(
        TrackModel("Overcompensate", "twenty one pilots", "Fueled By Ramen"),
        TrackModel("Go Flex","Post Malone", "A Republic Records"),
        TrackModel("Deja Vu", "Eminem", "Aftermath")
    )
}

class RemoteDataProvider : DataProvider {
    override fun getAll() = listOf(
        TrackModel("Better Off (Dying)", "Lil peep", "AUTNMY"),
        TrackModel("The Hype", "twenty one pilots", "Fueled By Ramen"),
        TrackModel("Fuck Love", "XXXTentacion, Trippe Red", "Bad Vibes Forever"),
        TrackModel("Fast", "Juice WRLD", "Grade A"),
        TrackModel("more than life", "mgk, glaive", "-")
    )
}