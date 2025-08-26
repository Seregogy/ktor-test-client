package com.example.ktor_test_client.di

import com.example.ktor_test_client.data.sources.DataSource
import com.example.ktor_test_client.data.sources.PlaylistDataSource
import com.example.ktor_test_client.data.sources.RandomTrackDataSource
import com.example.ktor_test_client.data.sources.SingleTrackDataSource
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.dsl.module

object PlaylistDataSourceQualifier : Qualifier {
    override val value: QualifierValue
        get() = "playlist data source"
}

object SingleDataSourceQualifier : Qualifier {
    override val value: QualifierValue
        get() = "single data source"
}

object RandomDataSourceQualifier : Qualifier {
    override val value: QualifierValue
        get() = "single data source"
}

val dataSourceDi = module {
    factory<DataSource>(PlaylistDataSourceQualifier) { (tracksId: List<String>, firstTrack: Int) ->
        PlaylistDataSource(tracksId = tracksId, firstTrack = firstTrack)
    }

    factory<DataSource>(SingleDataSourceQualifier) { (id: String) ->
        SingleTrackDataSource(id)
    }

    factory<DataSource>(RandomDataSourceQualifier) {
        RandomTrackDataSource(apiService = get())
    }
}