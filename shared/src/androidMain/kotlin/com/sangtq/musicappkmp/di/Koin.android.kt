package com.sangtq.musicappkmp.di

import com.sangtq.musicappkmp.core.playback.AudioPlayer
import com.sangtq.musicappkmp.core.playback.ExoAudioPlayer
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<AudioPlayer> { ExoAudioPlayer(androidContext()) }
}
