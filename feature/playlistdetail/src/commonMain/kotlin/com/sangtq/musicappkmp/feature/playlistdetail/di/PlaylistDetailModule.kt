package com.sangtq.musicappkmp.feature.playlistdetail.di

import com.sangtq.musicappkmp.feature.playlistdetail.presentation.PlaylistDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playlistDetailModule = module {
    viewModel { (playlistId: Long) -> PlaylistDetailViewModel(playlistId, get()) }
}
