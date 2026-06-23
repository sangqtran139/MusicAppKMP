package com.sangtq.musicappkmp.feature.albumdetail.di

import com.sangtq.musicappkmp.feature.albumdetail.presentation.AlbumDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val albumDetailModule = module {
    viewModel { (albumId: Long) -> AlbumDetailViewModel(albumId, get()) }
}
