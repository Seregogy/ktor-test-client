package com.example.ktor_test_client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
import com.example.ktor_test_client.routers.AlbumPageRouter
import com.example.ktor_test_client.routers.ArtistHomePageRouter
import com.example.ktor_test_client.routers.ArtistsCardPageRouter
import com.example.ktor_test_client.screens.BottomSheetPlayerPage
import com.example.ktor_test_client.ui.theme.KtortestclientTheme
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.KoinApplication

class MainActivity : ComponentActivity() {
    private val playerViewModel by viewModel<AudioPlayerViewModel>()
    private var sheetPeekHeight: Dp = 0.dp

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current

            KoinApplication(
                application = {
                    modules(listOf(apiServiceDi, apiClientDi, tokenHandlerDi, dataProviderDi, dataSourceDi, repositoryDi, viewModelDi))
                    androidContext(context)
                }
            ) {
                KtortestclientTheme {

                    val miniPlayerHeight = 100.dp
                    var allInit by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        delay(300)
                        allInit = true
                    }

                    val coroutineScope = rememberCoroutineScope()
                    val navController = rememberNavController()
                    val bottomSheetState = rememberBottomSheetScaffoldState()

                    val yCurrentOffset = remember {
                        derivedStateOf {
                            return@derivedStateOf if (allInit) {
                                bottomSheetState.bottomSheetState.requireOffset()
                            } else {
                                0f
                            }
                        }
                    }

                    Scaffold { innerPadding ->
                        sheetPeekHeight = miniPlayerHeight + innerPadding.calculateBottomPadding()
                        BottomSheetScaffold(
                            sheetPeekHeight = sheetPeekHeight,
                            scaffoldState = bottomSheetState,
                            sheetDragHandle = { },
                            sheetShape = RoundedCornerShape(0.dp),
                            sheetContent = {
                                BottomSheetPlayerPage(
                                    yCurrentOffset,
                                    miniPlayerHeight,
                                    innerPadding,
                                    playerViewModel,
                                    Modifier.padding(top = innerPadding.calculateTopPadding()),
                                    onExpandRequest = {
                                        coroutineScope.launch {
                                            bottomSheetState.bottomSheetState.expand()
                                        }
                                    },
                                    onCollapseRequest = {
                                        coroutineScope.launch {
                                            bottomSheetState.bottomSheetState.partialExpand()
                                        }
                                    },
                                    onAlbumClicked = { albumId ->
                                        coroutineScope.launch {
                                            navController.navigate("AlbumPage/?id=$albumId")
                                            bottomSheetState.bottomSheetState.partialExpand()
                                        }
                                    },
                                    onArtistClicked = { artistId ->
                                        coroutineScope.launch {
                                            navController.navigate("ArtistPage/?id=$artistId")
                                            bottomSheetState.bottomSheetState.partialExpand()
                                        }
                                    }
                                )
                            },
                            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(.5f)
                        ) {
                            NavRoutes(navController, inject<MusicApiService>().value, innerPadding)
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
        innerPadding: PaddingValues
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
                AlbumPageRouter(albumId, musicApiService, navController, playerViewModel, sheetPeekHeight, context)
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