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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ktor_test_client.api.KtorAPI
import com.example.ktor_test_client.api.TokenHandler
import com.example.ktor_test_client.api.TokenType
import com.example.ktor_test_client.api.dtos.Album
import com.example.ktor_test_client.api.dtos.BaseAlbum
import com.example.ktor_test_client.api.methods.getAlbum
import com.example.ktor_test_client.api.methods.getAlbumsFromArtist
import com.example.ktor_test_client.data.providers.NetworkDataProvider
import com.example.ktor_test_client.data.repositories.BaseNetworkRepository
import com.example.ktor_test_client.data.sources.PlaylistDataSource
import com.example.ktor_test_client.data.sources.RandomTrackDataSource
import com.example.ktor_test_client.api.InternetConnectionChecker
import com.example.ktor_test_client.screens.AlbumPage
import com.example.ktor_test_client.screens.ArtistsCardSwipeables
import com.example.ktor_test_client.screens.BottomSheetPlayerPage
import com.example.ktor_test_client.ui.theme.KtortestclientTheme
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var playerViewModel: AudioPlayerViewModel
    private var sheetPeekHeight: Dp = 0.dp

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KtortestclientTheme {
                val miniPlayerHeight = 100.dp
                var allInit by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    delay(300)
                    allInit = true
                }

                val context = LocalContext.current

                val connectionChecker = InternetConnectionChecker(context)

                val ktorApi = KtorAPI(context = context, tokenHandler = object : TokenHandler {
                    override fun saveToken(type: TokenType, token: String) { }
                    override fun loadToken(type: TokenType): String = ""
                    override fun hasToken(type: TokenType): Boolean = true
                })

                val networkDataProvider = NetworkDataProvider(ktorApi, context)
                val randomTrackDataSource = RandomTrackDataSource()
                val repository = BaseNetworkRepository(networkDataProvider, randomTrackDataSource)

                playerViewModel = AudioPlayerViewModel(repository)

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
        val context = LocalContext.current

        NavHost(
            navController = navController,
            startDestination = "Home"
        ) {
            composable(
                route = "AlbumPage/?id={albumId}",
                arguments = listOf(navArgument("albumId") { type = NavType.StringType })
            ) {
                val albumId = it.arguments?.getString("albumId")
                var album: Album? by remember { mutableStateOf(null) }
                var otherAlbums: List<BaseAlbum> by remember { mutableStateOf(listOf()) }

                LaunchedEffect(Unit) {
                    album = ktorApi.getAlbum(albumId ?: "")
                    otherAlbums = ktorApi.getAlbumsFromArtist(album?.artists?.first()?.id ?: "")?.albums ?: listOf()
                }

                when {
                    album == null -> Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                    else -> {
                        AlbumPage(
                            album = album!!,
                            otherAlbums = otherAlbums,
                            bottomPadding = sheetPeekHeight,
                            onArtistClicked = { artistId ->
                                navController.navigate("ArtistPage/?id=$artistId")
                            },
                            onAlbumClicked = { otherAlbumId ->
                                navController.navigate("AlbumPage/?id=$otherAlbumId")
                            }
                        ) { clickedTrack ->
                            album?.let { album ->
                                //TODO: инжектить дата соурс, который будет храниться в вью модели и оттуда получать индекс текущего трека, чтобы отобразить трек с анимацией в альбоме
                                playerViewModel.injectDataSource(context, PlaylistDataSource(
                                    tracksId = album.tracks.map { track ->
                                        track.id
                                    },
                                    firstTrack = clickedTrack.indexInAlbum
                                ))

                                playerViewModel.exoPlayer?.prepare()
                            }
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
                        .padding(25.dp)
                )
            }

            composable(
                route = "ArtistPage/?id={artistId}",
                arguments = listOf(navArgument("artistId") { type = NavType.IntType })
            ) {
                val artistId = it.arguments?.getInt("artistId")

                //TODO(вызов api)
                //ArtistHomePage()
            }

            composable(
                route = "ArtistsCardSwipeables"
            ) {
                ArtistsCardSwipeables(
                    modifier = Modifier
                        .padding(innerPadding),
                    listOf()
                ) {
                    navController.navigate("ArtistPage/?id=${it.id}")
                }
            }
        }
    }
}