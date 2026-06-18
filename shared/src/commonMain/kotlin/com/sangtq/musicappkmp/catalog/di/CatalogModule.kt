package com.sangtq.musicappkmp.catalog.di

import com.sangtq.musicappkmp.catalog.data.remote.CatalogApi
import com.sangtq.musicappkmp.catalog.data.repository.CatalogRepositoryImpl
import com.sangtq.musicappkmp.catalog.domain.repository.CatalogRepository
import com.sangtq.musicappkmp.catalog.domain.usecase.GetAlbumUseCase
import com.sangtq.musicappkmp.catalog.domain.usecase.GetArtistUseCase
import com.sangtq.musicappkmp.catalog.domain.usecase.GetPlaylistUseCase
import com.sangtq.musicappkmp.catalog.domain.usecase.GetTrackUseCase
import com.sangtq.musicappkmp.catalog.domain.usecase.SearchCatalogUseCase
import com.sangtq.musicappkmp.core.common.DefaultDispatcherProvider
import com.sangtq.musicappkmp.core.common.DispatcherProvider
import com.sangtq.musicappkmp.core.network.ApiConfig
import com.sangtq.musicappkmp.core.network.createHttpClient
import com.sangtq.musicappkmp.core.network.provideJson
import org.koin.dsl.module

val catalogModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single { provideJson() }
    single { ApiConfig() }
    single { createHttpClient(get(), get()) }
    single { CatalogApi(get(), get()) }
    single<CatalogRepository> { CatalogRepositoryImpl(get(), get()) }

    factory { SearchCatalogUseCase(get()) }
    factory { GetTrackUseCase(get()) }
    factory { GetAlbumUseCase(get()) }
    factory { GetArtistUseCase(get()) }
    factory { GetPlaylistUseCase(get()) }
}
