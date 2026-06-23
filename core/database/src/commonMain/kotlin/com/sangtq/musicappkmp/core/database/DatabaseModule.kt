package com.sangtq.musicappkmp.core.database

import org.koin.dsl.module

/**
 * Cung cấp [MusicDatabase] từ SqlDriver theo nền tảng (đăng ký ở platformModule —
 * AndroidSqliteDriver / NativeSqliteDriver). Xem docs/ADR/0007.
 */
val databaseModule = module {
    single { MusicDatabase(get()) }
}
