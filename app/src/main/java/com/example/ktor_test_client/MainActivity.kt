package com.example.ktor_test_client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import com.example.ktor_test_client.ui.theme.KtortestclientTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KtortestclientTheme {
                Scaffold { innerPadding ->
                    MainPage(
                        modifier = Modifier.padding(innerPadding),
                        MainPageViewModel(
                            hostUrl = "http://192.168.1.64",
                            port = 8080
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun MainPage(
    modifier: Modifier,
    viewModel: MainPageViewModel
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