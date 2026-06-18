package com.sangtq.musicappkmp.feature.library.di

import com.sangtq.musicappkmp.feature.library.data.InMemoryLibraryRepository
import com.sangtq.musicappkmp.feature.library.domain.repository.LibraryRepository
import com.sangtq.musicappkmp.feature.library.domain.usecase.AddRecentUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObserveLikedUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObserveRecentUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ToggleLikeUseCase
import com.sangtq.musicappkmp.feature.library.presentation.LibraryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val libraryModule = module {
    single<LibraryRepository> { InMemoryLibraryRepository() }
    factory { ObserveLikedUseCase(get()) }
    factory { ObserveRecentUseCase(get()) }
    factory { ToggleLikeUseCase(get()) }
    factory { AddRecentUseCase(get()) }
    viewModelOf(::LibraryViewModel)
}
