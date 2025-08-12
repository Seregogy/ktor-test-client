package com.example.ktor_test_client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ktor_test_client.api.KtorAPI
import com.example.ktor_test_client.api.TokenHandler
import com.example.ktor_test_client.api.TokenType
import com.example.ktor_test_client.api.methods.getRandomTrack
import com.example.ktor_test_client.controls.ApiCard
import com.example.ktor_test_client.controls.ApiMethodModel
import com.example.ktor_test_client.screens.AlbumPage
import com.example.ktor_test_client.screens.ArtistHomePage
import com.example.ktor_test_client.screens.ArtistsCardSwipeables
import com.example.ktor_test_client.screens.PaletteTestScreen
import com.example.ktor_test_client.screens.PlayerPage
import com.example.ktor_test_client.ui.theme.KtortestclientTheme
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import com.example.ktor_test_client.viewmodels.PaletteTestScreenViewModel
import kotlinx.coroutines.launch

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
                            startDestination = "Player/?id=1"
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
                                route = "Player/?id={trackId}",
                                arguments = listOf(navArgument("trackId") { type = NavType.IntType })
                            ) {
                                val ktorApi = KtorAPI(tokenHandler = object : TokenHandler {
                                    override fun saveToken(type: TokenType, token: String) { }

                                    override fun loadToken(type: TokenType): String = ""

                                    override fun hasToken(type: TokenType): Boolean = true

                                })

                                val context = LocalContext.current

                                val trackId = it.arguments?.getInt("trackId")

                                val track = Library.tracks.first { track -> track.id == trackId }
                                val album = Library.albums.first { album -> album.id == track.albumId }
                                val artist = Library.artists.first { artist -> artist.id == track.artistsId.first() }

                                val viewModel: AudioPlayerViewModel = viewModel()

                                LaunchedEffect(Unit) {
                                    val randomTrack = ktorApi.getRandomTrack()

                                    viewModel.initializePlayer(context)
                                    viewModel.playFromUri(android.net.Uri.parse(randomTrack?.track?.audioUrl ?: "http://192.168.1.64:8080/audio/86a08862-6e2e-4aeb-9a1a-c5005edfd90a.mp3"))
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

                            composable(
                                route = "PaletteTest"
                            ) {
                                PaletteTestScreen(viewModel<PaletteTestScreenViewModel>())
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