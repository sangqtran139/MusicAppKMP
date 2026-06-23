package com.sangtq.musicappkmp.di

import com.sangtq.musicappkmp.catalog.di.catalogModule
import com.sangtq.musicappkmp.core.database.databaseModule
import com.sangtq.musicappkmp.feature.albumdetail.di.albumDetailModule
import com.sangtq.musicappkmp.feature.artistdetail.di.artistDetailModule
import com.sangtq.musicappkmp.feature.home.di.homeModule
import com.sangtq.musicappkmp.feature.playlistdetail.di.playlistDetailModule
import com.sangtq.musicappkmp.feature.library.di.libraryModule
import com.sangtq.musicappkmp.feature.player.di.playerModule
import com.sangtq.musicappkmp.feature.search.di.searchModule
import org.koin.core.module.Module

/** Tất cả Koin module của app. Đăng ký một chỗ (xem docs/Features/README.md bước 4). */
val appModules: List<Module> = listOf(
    catalogModule,
    databaseModule,
    searchModule,
    playerModule,
    homeModule,
    libraryModule,
    albumDetailModule,
    artistDetailModule,
    playlistDetailModule,
)
