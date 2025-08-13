package com.example.ktor_test_client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import com.example.ktor_test_client.api.methods.AlbumResponse
import com.example.ktor_test_client.api.methods.RandomTrackResponse
import com.example.ktor_test_client.api.methods.getAlbumById
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

        val ktorApi = KtorAPI(tokenHandler = object : TokenHandler {
            override fun saveToken(type: TokenType, token: String) { }

            override fun loadToken(type: TokenType): String = ""

            override fun hasToken(type: TokenType): Boolean = true
        })

        setContent {
            KtortestclientTheme {
                Scaffold { innerPadding ->
                    Column {
                        val navController = rememberNavController()

                        NavHost(
                            navController = navController,
                            startDestination = "Home"
                        ) {
                            composable(
                                route = "AlbumPage/?id={albumId}",
                                arguments = listOf(navArgument("albumId") { type = NavType.StringType })
                            ) {
                                val albumId = it.arguments?.getString("albumId")
                                var album: AlbumResponse? by remember { mutableStateOf(null) }

                                LaunchedEffect(Unit) {
                                    album = ktorApi.getAlbumById(albumId)
                                }

                                when {
                                    album == null -> Box(Modifier.fillMaxSize()) {
                                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                                    }
                                    else -> {
                                        AlbumPage(album!!) { artistId ->
                                            navController.navigate("ArtistPage/?id=$artistId")
                                        }
                                    }
                                }
                            }

                            composable(
                                route = "Home"
                            ) {

                            }

                            composable(
                                route = "ArtistPage/?id={artistId}",
                                arguments = listOf(navArgument("artistId") { type = NavType.IntType })
                            ) {
                                val artistId = it.arguments?.getInt("artistId")

                                ArtistHomePage(Library.artists.first { artist -> artist.id == artistId })
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

                        //TODO: протестровать обёртку BottomSheetScaffold
                        PlayerPage(
                            ktorApi,
                            viewModel<AudioPlayerViewModel>(),
                            Modifier.padding(innerPadding),
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
        }
    }
}