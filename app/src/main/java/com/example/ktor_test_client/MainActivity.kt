package com.example.ktor_test_client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ktor_test_client.api.KtorAPI
import com.example.ktor_test_client.api.TokenHandler
import com.example.ktor_test_client.api.TokenType
import com.example.ktor_test_client.api.methods.RandomTrackResponse
import com.example.ktor_test_client.api.methods.getRandomTrack
import com.example.ktor_test_client.models.Album
import com.example.ktor_test_client.models.Artist
import com.example.ktor_test_client.models.Track
import com.example.ktor_test_client.screens.AlbumPage
import com.example.ktor_test_client.screens.ArtistHomePage
import com.example.ktor_test_client.screens.ArtistsCardSwipeables
import com.example.ktor_test_client.screens.PaletteTestScreen
import com.example.ktor_test_client.screens.PlayerPage
import com.example.ktor_test_client.ui.theme.KtortestclientTheme
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import com.example.ktor_test_client.viewmodels.ImagePaletteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KtortestclientTheme {
                Scaffold { innerPadding ->
                    Column {
                        val navController = rememberNavController()

                        NavHost(
                            navController = navController,
                            startDestination = "Player"
                        ) {
                            composable(
                                route = "AlbumPage/?id={albumId}",
                                arguments = listOf(navArgument("albumId") { type = NavType.IntType })
                            ) {
                                val albumId = it.arguments?.getInt("albumId")

                                AlbumPage(Library.albums.first { album -> album.id == albumId }) { artistId ->
                                    navController.navigate("ArtistPage/?id=$artistId")
                                }
                            }

                            composable(
                                route = "ArtistPage/?id={artistId}",
                                arguments = listOf(navArgument("artistId") { type = NavType.IntType })
                            ) {
                                val artistId = it.arguments?.getInt("artistId")

                                ArtistHomePage(Library.artists.first { artist -> artist.id == artistId })
                            }

                            composable(
                                route = "Player"
                            ) {
                                val ktorApi = KtorAPI(tokenHandler = object : TokenHandler {
                                    override fun saveToken(type: TokenType, token: String) { }

                                    override fun loadToken(type: TokenType): String = ""

                                    override fun hasToken(type: TokenType): Boolean = true
                                })

                                var randomTrack by remember { mutableStateOf<RandomTrackResponse?>(null) }
                                var isLoading by remember { mutableStateOf(true) }

                                LaunchedEffect(Unit) {
                                    isLoading = true
                                    try {
                                        randomTrack = ktorApi.getRandomTrack()
                                    } finally {
                                        isLoading = false
                                    }
                                }

                                when {
                                    isLoading -> Box(Modifier.fillMaxSize()) {
                                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                                    }

                                    else -> {
                                        val context = LocalContext.current

                                        val track = Track(
                                            id = 0,
                                            albumId = 0,
                                            name = randomTrack?.track?.name ?: "unknown",
                                            seconds = 0,
                                            artistsId = listOf()
                                        )

                                        val album = Album(
                                            id = 0,
                                            artistId = 0,
                                            name = randomTrack?.album?.name ?: "unknown",
                                            likes = 10,
                                            tracksId = listOf(),
                                            bestTracks = listOf(),
                                            totalListening = 0,
                                            releaseDate = 0,
                                            imageUrl = randomTrack?.album?.imageUrl ?: "",
                                            label = "",
                                            primaryColor = Color.Transparent
                                        )

                                        val artist = Artist(
                                            id = 0,
                                            name = randomTrack?.artist?.first()?.name ?: "unknown",
                                            imagesUrl = listOf(
                                                (randomTrack?.artist?.first()?.imageUrl ?: "") to Color.Transparent
                                            )
                                        )

                                        val viewModel: AudioPlayerViewModel = viewModel()

                                        LaunchedEffect(Unit) {
                                            viewModel.initializePlayer(context)
                                            viewModel.playFromUri(android.net.Uri.parse(randomTrack?.track?.audioUrl ?: "https://culinario-resources.hb.ru-msk.vkcloud-storage.ru/audio/b19cbec5-9883-44f9-8ea9-771c8e26fc3e.mp3"))

                                            println(randomTrack)
                                        }

                                        PlayerPage(
                                            viewModel,
                                            track,
                                            album,
                                            artist,
                                            innerPadding,
                                            onAlbumClicked = { albumId ->
                                                navController.navigate("AlbumPage/?id=$albumId")
                                            },
                                            onArtistClicked = { artistId ->
                                                navController.navigate("ArtistPage/?id=$artistId")
                                            }
                                        )
                                    }
                                }
                            }

                            composable(
                                route = "PaletteTest"
                            ) {
                                PaletteTestScreen(viewModel<ImagePaletteViewModel>())
                            }

                            composable(
                                route = "ArtistsCardSwipeables"
                            ) {
                                ArtistsCardSwipeables(
                                    modifier = Modifier
                                        .padding(innerPadding)
                                ) {
                                    navController.navigate("ArtistPage/?id=${it.id}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}