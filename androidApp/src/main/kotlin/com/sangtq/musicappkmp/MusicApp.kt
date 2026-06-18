package com.sangtq.musicappkmp

import android.app.Application
import com.sangtq.musicappkmp.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MusicApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@MusicApp)
        }
    }
}
