package com.example.ktor_test_client

import android.content.ComponentName
import android.util.Log
import android.widget.MediaController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.media3.session.SessionToken
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ktor_test_client.api.ApiClient
import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.api.tools.TokenHandler
import com.example.ktor_test_client.api.tools.TokenType
import com.example.ktor_test_client.data.AudioPlayer
import com.example.ktor_test_client.data.PlaylistContainer
import com.example.ktor_test_client.data.TracksCache
import com.example.ktor_test_client.data.providers.NetworkDataProvider
import com.example.ktor_test_client.data.repositories.BaseNetworkRepository
import com.example.ktor_test_client.viewmodels.MediaNotificationService
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.launch

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.koin.compose.koinInject

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun UseAppContext() {

    }
}