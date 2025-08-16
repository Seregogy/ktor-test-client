package com.example.ktor_test_client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ktor_test_client.api.KtorAPI
import com.example.ktor_test_client.api.TokenHandler
import com.example.ktor_test_client.api.TokenType
import com.example.ktor_test_client.api.methods.AlbumResponse
import com.example.ktor_test_client.api.methods.getAlbumById
import com.example.ktor_test_client.screens.AlbumPage
import com.example.ktor_test_client.screens.ArtistHomePage
import com.example.ktor_test_client.screens.ArtistsCardSwipeables
import com.example.ktor_test_client.screens.BottomSheetPlayerPage
import com.example.ktor_test_client.screens.PlayerPage
import com.example.ktor_test_client.ui.theme.KtortestclientTheme
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val ktorApi = KtorAPI(tokenHandler = object : TokenHandler {
            override fun saveToken(type: TokenType, token: String) { }
            override fun loadToken(type: TokenType): String = ""
            override fun hasToken(type: TokenType): Boolean = true
        })

        setContent {
            val screenHeight = LocalConfiguration.current.screenHeightDp.dp
            val miniPlayerHeight = 100.dp
            var allInit by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                delay(300)
                allInit = true
            }

            KtortestclientTheme {
                val coroutineScope = rememberCoroutineScope()
                val navController = rememberNavController()
                val bottomSheetState = rememberBottomSheetScaffoldState()

                val isExpanded = remember {
                    derivedStateOf {
                        bottomSheetState.bottomSheetState.currentValue == SheetValue.Expanded
                    }
                }

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
                    BottomSheetScaffold(
                        sheetPeekHeight = miniPlayerHeight + innerPadding.calculateBottomPadding(),
                        scaffoldState = bottomSheetState,
                        sheetDragHandle = { },
                        sheetShape = RoundedCornerShape(0.dp),
                        sheetContent = {
                            BottomSheetPlayerPage(
                                yCurrentOffset,
                                miniPlayerHeight,
                                innerPadding,
                                ktorApi,
                                viewModel<AudioPlayerViewModel>(),
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
                        }
                    ) {
                        NavRoutes(navController, ktorApi, innerPadding)
                    }
                }
            }
        }
    }

    @Composable
    fun NavRoutes(
        navController: NavHostController,
        ktorApi: KtorAPI,
        innerPadding: PaddingValues
    ) {
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
                Text(
                    text = "Главная страница",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.W800,
                    modifier = Modifier
                        .padding(innerPadding)
                )
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
    }
}