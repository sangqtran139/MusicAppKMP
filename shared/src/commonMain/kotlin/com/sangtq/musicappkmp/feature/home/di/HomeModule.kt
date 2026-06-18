package com.sangtq.musicappkmp.feature.home.di

import com.sangtq.musicappkmp.feature.home.domain.usecase.GetHomeSectionsUseCase
import com.sangtq.musicappkmp.feature.home.presentation.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    factory { GetHomeSectionsUseCase(get()) }
    viewModelOf(::HomeViewModel)
}
