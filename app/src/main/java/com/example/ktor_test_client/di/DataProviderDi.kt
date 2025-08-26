package com.example.ktor_test_client.di

import com.example.ktor_test_client.data.providers.DataProvider
import com.example.ktor_test_client.data.providers.NetworkDataProvider
import org.koin.dsl.module

val dataProviderDi = module {
    factory<DataProvider> {
        NetworkDataProvider(apiService = get())
    }
}