package com.sangtq.musicappkmp.di

import com.sangtq.musicappkmp.core.playback.AudioPlayer
import com.sangtq.musicappkmp.core.playback.AvAudioPlayer
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<AudioPlayer> { AvAudioPlayer() }
}

/** Gọi từ iOS (MainViewController) trước khi dựng Compose. */
fun initKoinIos() = initKoin()
