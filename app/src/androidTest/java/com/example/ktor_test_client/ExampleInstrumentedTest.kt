package com.example.ktor_test_client

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ktor_test_client.api.ApiClient
import com.example.ktor_test_client.api.MusicApiService
import com.example.ktor_test_client.api.tools.TokenHandler
import com.example.ktor_test_client.api.tools.TokenType
import com.example.ktor_test_client.data.PlaylistContainer
import com.example.ktor_test_client.data.providers.NetworkDataProvider

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun UseAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val tracksId = listOf(
            "b9ad47d2-e7ab-465b-a61b-fd9cdaec4072",
            "679cd4d6-fb3d-4895-89a7-8a906b65b9f3",
            "d960105f-b665-426a-9d69-9eff1ac0f02b",
            "27c3cefa-d6f9-4894-b87d-0118c63734a8"
        )
        val apiClient = ApiClient(appContext, tokenHandler = object : TokenHandler {
            override fun saveToken(type: TokenType, token: String) { }
            override fun loadToken(type: TokenType): String { return "" }
            override fun hasToken(type: TokenType): Boolean { return true }
        })
        val apiService = MusicApiService(apiClient)
        val dataProvider = NetworkDataProvider(apiService)

        val playlistContainer = PlaylistContainer(tracksId, dataProvider)
        val playlist = playlistContainer.getPlaylist()
        Log.d("API", playlist.currentNode?.data?.value.toString())
        Log.d("API", playlistContainer.tracks.toString())

        playlist.forward()
        Log.d("API", playlist.currentNode?.data?.value.toString())
        Log.d("API", playlistContainer.tracks.toString())

        playlist.forward()
        Log.d("API", playlist.currentNode?.data?.value.toString())
        Log.d("API", playlistContainer.tracks.toString())

        playlist.forward()
        Log.d("API", playlist.currentNode?.data?.value.toString())
        Log.d("API", playlistContainer.tracks.toString())

        playlistContainer.addTracks(listOf(
            "e2b22031-7060-4d85-a3ce-de319afaee76",
            "fe4b092e-08d1-4752-b699-1a2437dbd039",
            "8cb71df6-98fc-4217-8257-a8c24797108d"
        ))

        playlist.forward()
        playlist.forward()
        playlist.forward()
        Log.d("API", playlist.currentNode?.data?.value.toString())
        Log.d("API", playlistContainer.tracks.toString())


        assertEquals("com.example.ktor_test_client", appContext.packageName)
    }
}