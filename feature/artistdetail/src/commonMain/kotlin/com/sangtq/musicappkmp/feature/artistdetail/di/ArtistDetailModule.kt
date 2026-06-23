package com.sangtq.musicappkmp.feature.artistdetail.di

import com.sangtq.musicappkmp.feature.artistdetail.presentation.ArtistDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val artistDetailModule = module {
    viewModel { (artistId: Long) -> ArtistDetailViewModel(artistId, get()) }
}
