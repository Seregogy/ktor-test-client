package com.example.ktor_test_client.di

import com.example.ktor_test_client.viewmodels.AudioPlayerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelDi = module {
    viewModel<AudioPlayerViewModel> {
        AudioPlayerViewModel(repository = get())
    }
}