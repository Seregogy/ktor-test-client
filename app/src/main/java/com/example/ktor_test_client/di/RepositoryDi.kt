package com.example.ktor_test_client.di

import com.example.ktor_test_client.data.repositories.BaseNetworkRepository
import com.example.ktor_test_client.data.repositories.Repository
import com.example.ktor_test_client.data.sources.RandomTrackDataSource
import org.koin.dsl.module

val repositoryDi = module {
    single<Repository> {
        BaseNetworkRepository(
            dataProvider = get(),
            dataSource = get<RandomTrackDataSource>()
        )
    }
}