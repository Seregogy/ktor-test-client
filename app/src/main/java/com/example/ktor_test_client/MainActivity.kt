package com.example.ktor_test_client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ktor_test_client.controls.ApiCard
import com.example.ktor_test_client.controls.ApiMethodModel
import com.example.ktor_test_client.pages.PlayerPage
import com.example.ktor_test_client.screens.AlbumPage
import com.example.ktor_test_client.screens.ArtistHomePage
import com.example.ktor_test_client.screens.PaletteTestScreen
import com.example.ktor_test_client.ui.theme.KtortestclientTheme
import com.example.ktor_test_client.viewmodels.PaletteTestScreenViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
                            startDestination = "PaletteTest"
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
                                val trackId = it.arguments?.getInt("trackId")

                                val track = Library.tracks.first { track -> track.id == trackId }
                                val album = Library.albums.first { album -> album.id == track.albumId }
                                val artist = Library.artists.first { artist -> artist.id == track.artistsId.first() }

                                PlayerPage(
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
                                route = "Test"
                            ) {
                                val density = LocalDensity.current
                                val widthPx = with(density) { (LocalConfiguration.current.screenWidthDp.dp - 50.dp).toPx() }

                                val rectSize = 120.dp
                                val rectSizePx = with(density) { rectSize.toPx() }

                                val state = remember {
                                    AnchoredDraggableState(
                                        initialValue = 0,
                                        anchors = DraggableAnchors {
                                            0 at 0f
                                            1 at widthPx / 2
                                            2 at widthPx
                                        },
                                        //positionalThreshold = { distance: Float -> distance * .5f },
                                        //velocityThreshold = { with(density) { 100.dp.toPx() } },
                                        //animationSpec = tween()
                                    )
                                }

                                Box(
                                    Modifier
                                        .padding(top = innerPadding.calculateTopPadding())
                                        .offset {
                                            IntOffset(
                                                x = state
                                                    .requireOffset()
                                                    .roundToInt(),
                                                y = 0,
                                            )
                                        }
                                        .anchoredDraggable(
                                            state,
                                            Orientation.Horizontal,
                                        )
                                        .size(rectSize)
                                        .background(Color.LightGray)
                                )
                            }

                            composable(
                                route = "PaletteTest"
                            ) {
                                PaletteTestScreen(viewModel<PaletteTestScreenViewModel>())
                            }
                        }
                    }

                    //FetchUserGet(Modifier.padding(innerPadding), FetchUserViewModel("http://192.168.1.64", 8080))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainPage(
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier
            .padding(20.dp)
            .clip(MaterialTheme.shapes.largeIncreased)
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        val apiMethods = mutableListOf(
            ApiMethodModel("/user", method = "get", params = mapOf("id" to "Int", "name" to "String")),
            ApiMethodModel("/user", method = "post"),
            ApiMethodModel("/user", method = "delete")
        )
        itemsIndexed(apiMethods) { index, item ->
            ApiCard(
                apiMethodModel = item,
                onDisable = {
                    //apiMethods.remove(it)
                }
            )

            if (index < apiMethods.count() - 1) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .padding(start = 30.dp)
                )
            }
        }
    }
}

@Composable
fun FetchUserGet(
    modifier: Modifier = Modifier,
    viewModel: FetchUserViewModel
) {
    var user: User? by remember {
        mutableStateOf(null)
    }

    var selectedId by remember {
        mutableStateOf("")
    }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (user != null) {
                Card(
                    modifier = Modifier
                        .padding(20.dp),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 15.dp
                    )
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        Text(
                            text = user!!.name ?: "unknown",
                            fontSize = MaterialTheme.typography.labelLarge.fontSize,
                            fontWeight = MaterialTheme.typography.labelLarge.fontWeight
                        )

                        Row {
                            Text(
                                text = user!!.about ?: "none@gmail.com"
                            )

                            HorizontalDivider()

                            Text(
                                text = user!!.id.toString(),
                            )
                        }

                        Text(
                            text = user!!.about ?: "none"
                        )
                    }
                }
            }


            Text(
                text = "Fetch user by id",
                modifier = Modifier,
                fontSize = MaterialTheme.typography.labelLarge.fontSize,
                fontWeight = MaterialTheme.typography.labelLarge.fontWeight
            )

            TextField(
                value = selectedId,
                onValueChange = {
                    selectedId = it
                },
                label = {
                    Text(text = "Укажите индекс")
                }
            )

            Button(onClick = {
                coroutineScope.launch {
                    user = viewModel.fetchUser(selectedId.toInt())
                }
            }) {
                Text(text = "Получить пользователя по ID")
            }
        }
    }
}