package com.sangtq.musicappkmp.feature.library.di

import com.sangtq.musicappkmp.feature.library.data.LibraryLocalDataSource
import com.sangtq.musicappkmp.feature.library.data.PlaylistLocalDataSource
import com.sangtq.musicappkmp.feature.library.data.SqlDelightLibraryRepository
import com.sangtq.musicappkmp.feature.library.data.SqlDelightPlaylistRepository
import com.sangtq.musicappkmp.feature.library.domain.repository.LibraryRepository
import com.sangtq.musicappkmp.feature.library.domain.repository.PlaylistRepository
import com.sangtq.musicappkmp.feature.library.domain.usecase.AddRecentUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.AddTrackToPlaylistUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.CreatePlaylistUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.DeletePlaylistUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObserveLikedUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObservePlaylistNameUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObservePlaylistTracksUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObservePlaylistsUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObserveRecentUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.RemoveTrackFromPlaylistUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ToggleLikeUseCase
import com.sangtq.musicappkmp.feature.library.presentation.LibraryViewModel
import com.sangtq.musicappkmp.feature.library.presentation.UserPlaylistDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val libraryModule = module {
    single { LibraryLocalDataSource(get(), get()) }
    single<LibraryRepository> { SqlDelightLibraryRepository(get()) }
    single { PlaylistLocalDataSource(get(), get()) }
    single<PlaylistRepository> { SqlDelightPlaylistRepository(get()) }

    factory { ObserveLikedUseCase(get()) }
    factory { ObserveRecentUseCase(get()) }
    factory { ToggleLikeUseCase(get()) }
    factory { AddRecentUseCase(get()) }
    factory { ObservePlaylistsUseCase(get()) }
    factory { CreatePlaylistUseCase(get()) }
    factory { DeletePlaylistUseCase(get()) }
    factory { AddTrackToPlaylistUseCase(get()) }
    factory { RemoveTrackFromPlaylistUseCase(get()) }
    factory { ObservePlaylistNameUseCase(get()) }
    factory { ObservePlaylistTracksUseCase(get()) }

    viewModelOf(::LibraryViewModel)
    viewModel { (playlistId: Long) -> UserPlaylistDetailViewModel(playlistId, get(), get(), get()) }
}
