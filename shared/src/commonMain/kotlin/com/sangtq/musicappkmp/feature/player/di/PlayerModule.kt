package com.sangtq.musicappkmp.feature.player.di

import com.sangtq.musicappkmp.feature.player.presentation.PlayerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val playerModule = module {
    viewModelOf(::PlayerViewModel)
}
