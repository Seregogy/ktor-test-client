package com.example.ktor_test_client.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ktor_test_client.viewmodels.ImagePaletteViewModel
import kotlinx.coroutines.launch

@Composable
fun PaletteTestScreen(
    viewModel: ImagePaletteViewModel
) {
    val bitmap: MutableState<Bitmap?> = remember { mutableStateOf(null) }

    var imageUrl by remember { mutableStateOf("https://the-flow.ru/uploads/images/catalog/element/665087c3320df.png") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val colorScheme = MaterialTheme.colorScheme
    val currentColor = remember { mutableStateOf(colorScheme.background) }

    val animatedCurrentColor = animateColorAsState(
        targetValue = currentColor.value,
        animationSpec = tween(500),
        label = "color animation"
    )

    LaunchedEffect(Unit) {
        viewModel.fetchImageByUrl(context, imageUrl)
    }

    LaunchedEffect(Unit) {
        viewModel.bitmap.collect {
            bitmap.value = it
        }
    }

    LaunchedEffect(Unit) {
        viewModel.palette.collect {
            if (it?.swatches != null) {
                currentColor.value = Color(it.vibrantSwatch?.rgb ?: 0xFFFFFF)
            }
        }
    }

    if(bitmap.value != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedCurrentColor.value)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(.7f)
                    .offset(y = (-120).dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Image(
                    bitmap = bitmap.value!!.asImageBitmap(),
                    contentDescription = "Idk",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )

                TextField(
                    value = imageUrl,
                    onValueChange = {
                        imageUrl = it
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = .5f),
                        focusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = .5f)
                    )
                )

                AnimatedVisibility(
                    visible = imageUrl.isNotEmpty()
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch {
                                viewModel.fetchImageByUrl(context, imageUrl)
                            }
                        }
                    ) {
                        Text("Fetch image")
                    }
                }
            }
        }
    }
}