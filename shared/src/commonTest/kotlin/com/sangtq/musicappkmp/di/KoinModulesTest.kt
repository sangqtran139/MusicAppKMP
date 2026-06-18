package com.sangtq.musicappkmp.di

import com.sangtq.musicappkmp.catalog.domain.repository.CatalogRepository
import com.sangtq.musicappkmp.catalog.domain.usecase.SearchCatalogUseCase
import com.sangtq.musicappkmp.core.playback.AudioPlayer
import com.sangtq.musicappkmp.core.playback.PlaybackStub
import com.sangtq.musicappkmp.feature.home.domain.usecase.GetHomeSectionsUseCase
import com.sangtq.musicappkmp.feature.library.domain.repository.LibraryRepository
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull

/** Kiểm tra Koin graph (không gồm ViewModel — cần CreationExtras) resolve được. */
class KoinModulesTest {

    @AfterTest
    fun tearDown() = stopKoin()

    @Test
    fun appGraph_resolvesCoreDependencies() {
        val koin = startKoin {
            modules(appModules + module { single<AudioPlayer> { PlaybackStub() } })
        }.koin

        assertNotNull(koin.get<CatalogRepository>())
        assertNotNull(koin.get<SearchCatalogUseCase>())
        assertNotNull(koin.get<GetHomeSectionsUseCase>())
        assertNotNull(koin.get<LibraryRepository>())
        assertNotNull(koin.get<AudioPlayer>())
    }
}
