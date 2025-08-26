package com.example.ktor_test_client.api.tools

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.DisposableHandle

class InternetConnectionChecker(
    context: Context
) : DisposableHandle {
    private val connectionReturnsListenersOnce: MutableList<() -> Unit> = mutableListOf()

    private val _isConnected: MutableState<Boolean> = mutableStateOf(false)
    val isConnected: State<Boolean> = _isConnected

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)

            if (!isConnected.value) {
                connectionReturnsListenersOnce.forEach { listener ->
                    listener()
                }
                connectionReturnsListenersOnce.clear()
            }

            _isConnected.value = true
            Log.d("Network", "Network available: $network")
        }

        override fun onUnavailable() {
            super.onUnavailable()

            _isConnected.value = false
            Log.d("Network", "Network unavailable")
        }

        override fun onLost(network: Network) {
            super.onLost(network)

            _isConnected.value = false
            Log.d("Network", "Network lost")
        }
    }

    private val networkRequest = NetworkRequest.Builder().build()

    init {
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    fun waitForConnectionReturnsOnce(callback: () -> Unit) {
        connectionReturnsListenersOnce.add(callback)
    }

    override fun dispose() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}