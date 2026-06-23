package com.sangtq.musicappkmp.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.sangtq.musicappkmp.core.database.MusicDatabase
import com.sangtq.musicappkmp.core.playback.AudioPlayer
import com.sangtq.musicappkmp.core.playback.ExoAudioPlayer
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<AudioPlayer> { ExoAudioPlayer(androidContext()) }
    single<SqlDriver> { AndroidSqliteDriver(MusicDatabase.Schema, androidContext(), "music.db") }
}
