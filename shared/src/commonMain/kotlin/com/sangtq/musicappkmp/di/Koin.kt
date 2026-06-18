package com.sangtq.musicappkmp.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

/** Module phụ thuộc nền tảng (AudioPlayer ExoPlayer/AVPlayer…). */
expect fun platformModule(): Module

/** Khởi tạo Koin — gọi ở platform entry (Android Application / iOS bootstrap). */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(appModules + platformModule())
    }
}
