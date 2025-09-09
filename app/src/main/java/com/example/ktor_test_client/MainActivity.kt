package com.example.ktor_test_client

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ktor_test_client.controls.player.AudioPlayerScaffold
import com.example.ktor_test_client.di.apiClientDi
import com.example.ktor_test_client.di.apiServiceDi
import com.example.ktor_test_client.di.dataProviderDi
import com.example.ktor_test_client.di.dataSourceDi
import com.example.ktor_test_client.di.repositoryDi
import com.example.ktor_test_client.di.tokenHandlerDi
import com.example.ktor_test_client.di.viewModelDi
import com.example.ktor_test_client.routers.AlbumPageRouter
import com.example.ktor_test_client.routers.ArtistPageRouter
import com.example.ktor_test_client.routers.ArtistsCardPageRouter
import com.example.ktor_test_client.ui.theme.KtortestclientTheme
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import com.example.ktor_test_client.viewmodels.MediaNotificationService
import com.google.common.util.concurrent.MoreExecutors
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {
    private val mediaController: MutableState<MediaController?> = mutableStateOf(null)

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
                    val hazeState = rememberHazeState()

                    when {
                        mediaController.value != null -> {
                            val audioPlayer = koinInject<AudioPlayerViewModel>(parameters = { parametersOf(mediaController.value) })

                            Scaffold { innerPadding ->
                                AudioPlayerScaffold(
                                    viewModel = audioPlayer,
                                    innerPadding = innerPadding,
                                    navController = navController,
                                    hazeState = hazeState,
                                ) { sheetPeekHeight, _ ->
                                    NavRoutes(
                                        navController = navController,
                                        innerPadding = innerPadding,
                                        additionalBottomPadding = sheetPeekHeight,
                                        hazeState = hazeState
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val sessionToken = SessionToken(this, ComponentName(this, MediaNotificationService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()

        controllerFuture.addListener({
            mediaController.value = controllerFuture.get()
        }, MoreExecutors.directExecutor())
    }

    @Composable
    fun NavRoutes(
        navController: NavHostController,
        innerPadding: PaddingValues,
        additionalBottomPadding: Dp,
        hazeState: HazeState
    ) {
        val audioPlayerViewModel: AudioPlayerViewModel = koinInject()

        NavHost(
            navController = navController,
            startDestination = "ArtistsCardSwipeables"
        ) {
            composable(
                route = "ArtistPage?id={artistId}",
                arguments = listOf(navArgument("artistId") { type = NavType.StringType })
            ) {
                val artistId = it.arguments?.getString("artistId")

                ArtistPageRouter(
                    artistId = artistId,
                    playerViewModel = audioPlayerViewModel,
                    innerPadding = innerPadding,
                    bottomPadding = additionalBottomPadding,
                    hazeState = hazeState,
                    onBackRequest = {
                        navController.popBackStack()
                    },
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

                AlbumPageRouter(
                    albumId = albumId,
                    playerViewModel = audioPlayerViewModel,
                    innerPadding = innerPadding,
                    bottomPadding = additionalBottomPadding,
                    hazeState = hazeState,
                    onBackRequest = {
                        navController.popBackStack()
                    },
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
                ArtistsCardPageRouter(Modifier.padding(innerPadding), koinInject(), hazeState) {
                    navController.navigate("ArtistPage?id=${it.id}")
                }
            }
        }
    }
}