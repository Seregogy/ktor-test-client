package com.example.ktor_test_client

import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ktor_test_client.controls.ApiCard
import com.example.ktor_test_client.controls.ApiMethodModel
import com.example.ktor_test_client.screens.ArtistHomePage
import com.example.ktor_test_client.screens.postMalone
import com.example.ktor_test_client.ui.theme.KtortestclientTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var paused by remember { mutableStateOf(false) }

            KtortestclientTheme {
                Scaffold { innerPadding ->
                    Column {
                        ArtistHomePage(Modifier.padding(innerPadding), postMalone)

//                        MainPage(Modifier.padding(innerPadding))
                    }
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