package com.example.ktor_test_client.di

import com.example.ktor_test_client.data.repositories.BaseNetworkRepository
import com.example.ktor_test_client.data.repositories.Repository
import com.example.ktor_test_client.data.sources.DataSource
import org.koin.dsl.module

val repositoryDi = module {
    factory<Repository> { (dataSource: DataSource) ->
        BaseNetworkRepository(
            dataProvider = get()
        ).apply {
            injectDataSource(dataSource)
        }
    }
}