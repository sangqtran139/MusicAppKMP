package com.sangtq.musicappkmp.feature.home.di

import com.sangtq.musicappkmp.feature.home.domain.usecase.GetFeaturedPlaylistsUseCase
import com.sangtq.musicappkmp.feature.home.domain.usecase.GetHomeSectionsUseCase
import com.sangtq.musicappkmp.feature.home.presentation.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    factory { GetHomeSectionsUseCase(get()) }
    factory { GetFeaturedPlaylistsUseCase(get()) }
    viewModelOf(::HomeViewModel)
}
