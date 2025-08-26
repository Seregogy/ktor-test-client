package com.example.ktor_test_client.di

import com.example.ktor_test_client.viewmodels.AlbumViewModel
import com.example.ktor_test_client.viewmodels.ArtistViewModel
import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelDi = module {
    viewModel<AudioPlayerViewModel> {
        AudioPlayerViewModel(repository = get(), context = get())
    }

    factory<AlbumViewModel> {
        AlbumViewModel(apiService = get(), audioPlayerViewModel = get())
    }

    factory<ArtistViewModel> {
        ArtistViewModel(apiService = get())
    }
}