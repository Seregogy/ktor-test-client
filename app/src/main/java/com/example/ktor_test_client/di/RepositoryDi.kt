package com.example.ktor_test_client.di

import com.example.ktor_test_client.data.repositories.BaseNetworkRepository
import org.koin.dsl.module

val repositoryDi = module {
    factory<BaseNetworkRepository> {
        BaseNetworkRepository(service = get())
    }
}