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
import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.di.apiClientDi
import com.example.ktor_test_client.di.apiServiceDi
import com.example.ktor_test_client.di.dataProviderDi
import com.example.ktor_test_client.di.dataSourceDi
import com.example.ktor_test_client.di.repositoryDi
import com.example.ktor_test_client.di.tokenHandlerDi
import com.example.ktor_test_client.di.viewModelDi
import com.example.ktor_test_client.controls.player.AudioPlayerScaffold
import com.example.ktor_test_client.routers.AlbumPageRouter
import com.example.ktor_test_client.routers.ArtistHomePageRouter
import com.example.ktor_test_client.routers.ArtistsCardPageRouter
import com.example.ktor_test_client.ui.theme.KtortestclientTheme
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.KoinApplication

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
                                musicApiService = inject<MusicApiService>().value,
                                audioPlayerViewModel = viewModel<AudioPlayerViewModel>().value,
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
        musicApiService: MusicApiService,
        audioPlayerViewModel: AudioPlayerViewModel,
        innerPadding: PaddingValues,
        additionalBottomPadding: Dp
    ) {
        val context = LocalContext.current

        NavHost(
            navController = navController,
            startDestination = "ArtistsCardSwipeables"
        ) {
            composable(
                route = "AlbumPage/?id={albumId}",
                arguments = listOf(navArgument("albumId") { type = NavType.StringType })
            ) {
                val albumId = it.arguments?.getString("albumId") ?: ""
                AlbumPageRouter(albumId, musicApiService, navController, audioPlayerViewModel, additionalBottomPadding, context)
            }

            composable(
                route = "ArtistPage/?id={artistId}",
                arguments = listOf(navArgument("artistId") { type = NavType.IntType })
            ) {
                val artistId = it.arguments?.getInt("artistId")
                ArtistHomePageRouter()
            }

            composable(
                route = "ArtistsCardSwipeables"
            ) {
                ArtistsCardPageRouter(Modifier.padding(innerPadding), musicApiService) {
                    navController.navigate("ArtistPage/?id=${it.id}")
                }
            }
        }
    }
}