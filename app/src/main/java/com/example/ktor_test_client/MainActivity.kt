package com.example.ktor_test_client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.ktor_test_client.controls.player.AudioPlayerScaffold
import com.example.ktor_test_client.di.apiClientDi
import com.example.ktor_test_client.di.apiServiceDi
import com.example.ktor_test_client.di.dataProviderDi
import com.example.ktor_test_client.di.dataSourceDi
import com.example.ktor_test_client.di.repositoryDi
import com.example.ktor_test_client.di.tokenHandlerDi
import com.example.ktor_test_client.di.viewModelDi
import com.example.ktor_test_client.routers.AlbumPageRouter
import com.example.ktor_test_client.routers.ArtistHomePageRouter
import com.example.ktor_test_client.routers.ArtistsCardPageRouter
import com.example.ktor_test_client.ui.theme.KtortestclientTheme
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.koinApplication

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KtortestclientTheme {
                val context = LocalContext.current

                KoinApplication(
                    application = {
                        modules(listOf(apiServiceDi, apiClientDi, tokenHandlerDi, dataProviderDi, dataSourceDi, repositoryDi, viewModelDi))
                        androidContext(context)
                    }
                ) {
                    val navController = rememberNavController()

                    Scaffold { innerPadding ->
                        AudioPlayerScaffold(
                            innerPadding = innerPadding,
                            navController = navController
                        ) { sheetPeekHeight, _ ->
                            NavRoutes(
                                navController = navController,
                                innerPadding = innerPadding,
                                additionalBottomPadding = sheetPeekHeight
                            )
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun NavRoutes(
        navController: NavHostController,
        innerPadding: PaddingValues,
        additionalBottomPadding: Dp
    ) {
        val audioPlayerViewModel: AudioPlayerViewModel = koinViewModel()

        NavHost(
            navController = navController,
            startDestination = "ArtistsCardSwipeables"
        ) {
            composable(
                route = "ArtistPage?id={artistId}",
                arguments = listOf(navArgument("artistId") { type = NavType.StringType })
            ) {
                val artistId = it.arguments?.getString("artistId")
                ArtistHomePageRouter(artistId, audioPlayerViewModel, additionalBottomPadding,
                    onTrackClicked = {

                    },
                    onAlbumClicked = { albumId ->
                        navController.navigate("AlbumPage?id=$albumId")
                    }
                )
            }

            composable(
                route = "AlbumPage?id={albumId}",
                arguments = listOf(navArgument("albumId") { type = NavType.StringType })
            ) {
                val albumId = it.arguments?.getString("albumId")
                AlbumPageRouter(albumId, additionalBottomPadding, audioPlayerViewModel,
                    onArtistClicked = { artistId ->
                        navController.navigate("ArtistPage?id=$artistId")
                    },
                    onAlbumClicked = { otherAlbumId ->
                        navController.navigate("AlbumPage?id=$otherAlbumId")
                    }
                )
            }

            composable(
                route = "ArtistsCardSwipeables"
            ) {
                ArtistsCardPageRouter(Modifier.padding(innerPadding), koinInject()) {
                    navController.navigate("ArtistPage?id=${it.id}")
                }
            }
        }
    }
}