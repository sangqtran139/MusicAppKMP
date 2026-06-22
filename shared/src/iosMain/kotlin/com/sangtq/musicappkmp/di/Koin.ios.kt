package com.sangtq.musicappkmp.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.sangtq.musicappkmp.core.database.MusicDatabase
import com.sangtq.musicappkmp.core.playback.AudioPlayer
import com.sangtq.musicappkmp.core.playback.AvAudioPlayer
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<AudioPlayer> { AvAudioPlayer() }
    single<SqlDriver> { NativeSqliteDriver(MusicDatabase.Schema, "music.db") }
}

/** Gọi từ iOS (MainViewController) trước khi dựng Compose. */
fun initKoinIos() = initKoin()
