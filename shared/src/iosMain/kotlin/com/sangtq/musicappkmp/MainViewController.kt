package com.sangtq.musicappkmp

import androidx.compose.ui.window.ComposeUIViewController
import com.sangtq.musicappkmp.di.initKoinIos

private val koinStarted: Boolean by lazy {
    initKoinIos()
    true
}

fun MainViewController() = ComposeUIViewController {
    koinStarted
    App()
}
